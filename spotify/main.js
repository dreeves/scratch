#!/usr/bin/env node

var F = function(v){return v.replace(/^~\//,process.env.HOME+'/')}

var fs = require('fs')

var spotify = require('node-spotify')({appkeyFile: F('~/.spotiman/spotify_appkey.key')})
var _ = require('underscore')
var m = require('moment')
var minimist = require('minimist')
var sync = require('sync')

var err_print = function(f){return function(){try{f()} catch (e) {console.log('error!',e,e.message,e.stack)}}}
sync(err_print(function(){

// http://gizmodo.com/you-can-download-any-spotify-song-as-an-mp3-with-this-c-494493386 ??

var print = console.log.bind(console)
var seq = function(v){return typeof v === 'string'? v.split('') : v instanceof Array? v : Object.keys(v).map(function(k){return [k,v[k]]})}
Array.prototype.m_concat = function(){return Array.prototype.concat.apply([],this)}
var object = function(v){return v.reduce(function(r,v){r[v[0]] = v[1]; return r},{})}
var C = function(v){return function(){return v}}
var err = function(v){throw Error(v)}

var args = minimist(process.argv.slice(2))

var auth = JSON.parse(fs.readFileSync(F('~/.spotiman/auth.json')))

var timer = function λ(){var t = Date.now()/1000; var r = t - λ.now; λ.now = t; return Math.round(r*100)/100+'s'}; timer()
//var sleep = function(time){(function(cb){setTimeout(cb,time*1000)}).sync()}
//var poll = function(f){while (!f()) sleep(0.05)}
var poll = function(f,cb){(function λ(){if (f()) cb(); else setTimeout(λ,50)})()}

var login = function(){
	spotify.login(auth.username, auth.password, false, false)
	spotify.ready.sync()
	login = C()
	print('spotify ready!',timer()) }
var playlists_ = function(){
	login()
	var l = spotify.playlistContainer.getPlaylists()
	poll.sync(null,function(){return l.every(function(v){return v.isLoaded || v.type})})
	var r_p = []; var r_t = []; var out = r_p
	l.filter(function(v){return v.name!=='-'}).map(function(v){if (v.isLoaded) out.push(v); else if (v.name==='tags') out = r_t; else out = r_p})
	playlists = C(r_p)
	tags = C(r_t)
	var t = seq(_.groupBy(playlists().concat(tags()),'name')).map(function(v){return v[1].length===1? undefined : _.pluck(v[1],'name')}).filter(function(v){return v}).m_concat()
	if (t.length > 0) err('oh no! duplicate playlists! '+t)
	print('playlists loaded!',timer()) }
var playlists = function(){playlists_(); return playlists()}
var tags = function(){playlists_(); return tags()}
var tracks = function(){
	var r = []
	playlists().map(function(l){l.getTracks().map(function(v){(v.playlists=v.playlists||[]).push(l.name); r.push(v)})})
	tags(     ).map(function(l){l.getTracks().map(function(v){(v.tags     =v.tags     ||[]).push(l.name); r.push(v)})})
	r.map(function(v){v.playlists=v.playlists||[]; v.tags=v.tags||[]})
	poll.sync(null,function(){return r.every(function(v){return v.isLoaded})})
	r = seq(_.groupBy(r,'link')).map(function(v){v[1][0].playlists = _.uniq(_.pluck(v[1],'playlists').m_concat()); v[1][0].tags = _.uniq(_.pluck(v[1],'tags').m_concat()); return v[1][0]})
	tracks = C(r)
	print('tracks loaded!',timer())
	return r}

var main = function(){
	var tr = tracks()

	tr = seq(_.groupBy(tr,function(v){return v.artists[0].name===''? v.name : v.artists[0].name+' @ '+v.name}))
		.map(function(v){return v[1].length===1? [[v[0],v[1][0]]] : seq(_.groupBy(v[1],function(v){return v.artists[0].name+' @ '+v.album.name+' @ '+v.name}))}).m_concat()

	// okay . we've generated an unique name for all tracks. what next?
	// .tags seems to be broken! see: tr[v].tags &&

	tr = object(tr)
	Object.keys(tr).map(function(v,i){if (tr[v].tags && tr[v].tags.length > 0) print(v,tr[v].playlists,tr[v].tags)})
	}

var save_history = function(){
	var stringify_tracks = function(tracks){
		tracks = JSON.parse(JSON.stringify(tracks))
		tracks.map(function(v){
			delete(v.isLoaded)
			delete(v.popularity)
			delete(v.starred)
			delete(v.album.isLoaded)
			delete(v.duration)
			v.artists.map(function(v){delete(v.isLoaded)})
			v.playlists = _.pluck(v.playlists,'name')
			})
		return JSON.stringify(tracks,null,'\t')}
	fs.writeFileSync(F('~/ali/history/auto/spotify/'+m().toISOString()+'.json'),stringify_tracks(tracks()))
	print('history saved!',timer()) }

//===----------------===// call function based on args //===---------------===//

if (module.parent) print("oh my goodness, so sorry, but, spotiman isn't built to be require()'d!")
else switch (args._[0]) {
case undefined     : main(); break
case 'save-history': save_history(); break
case 'e'           : print(eval(process.argv.slice(3).join(' '))); break
default            : print('usage: $ <etc>'); break
}

//===---------------------------===// <end> //===--------------------------===//

process.exit()

/*
track: name [tag]
tag:
	α:artist
	ρ:album
	τ:track
	λ:length
	π:playlist-not-in-tags
	playlist-in-tags
*/

}))