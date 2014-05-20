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
var poll = function(f,cb){(function λ(){if (f()) cb(); else setTimeout(λ,50)})()}

//!! might be losing playlist tags !!

var login = function(){
	spotify.login(auth.username, auth.password, false, false)
	spotify.ready.sync()
	login = C(); print('spotify ready!',timer()) }
var playlists_ = function(){
	login()
	var l = spotify.playlistContainer.getPlaylists()
	poll.sync(null,function(){return l.every(function(v){return v.isLoaded || v.type})})
	var r_p = []; var r_t = []; var out = r_p
	l.filter(function(v){return v.name!=='-'}).map(function(v){if (v.isLoaded) out.push(v); else if (v.name==='tags') out = r_t; else out = r_p})
	playlists = C(r_p)
	tags = C(r_t)
	var t; if ((t=seq(_.groupBy(playlists().concat(tags()),'name')).map(function(v){return v[1].length===1? undefined : _.pluck(v[1],'name')}).filter(function(v){return v}).m_concat()).length > 0) err('oh no! duplicate playlists! '+t)
	print('playlists loaded!',timer()) }
var playlists = function(){playlists_(); return playlists()}
var tags = function(){playlists_(); return tags()}
var tracks = function(){
	var r = [playlists().map(function(v){return v.getTracks()}).m_concat(),tags().map(function(v){return v.getTracks()}).m_concat()].m_concat()
	poll.sync(null,function(){return r.every(function(v){return v.isLoaded})})
	tracks = C(r); print('tracks loaded!',timer()); return r}
var tracks_nice = function(){
	var set_playlists = function(tracks){
		tracks.map(function(v){v.playlists=v.playlists||[]; v.tags=v.tags||[]})
		var t = _.indexBy(tracks,'link')
		playlists().map(function(l){l.getTracks().map(function(v){if (_.indexOf(t[v.link].playlists,l.name)===-1) t[v.link].playlists.push(l.name)})})
		tags(     ).map(function(l){l.getTracks().map(function(v){if (_.indexOf(t[v.link].tags     ,l.name)===-1) t[v.link].tags     .push(l.name)})})
		}
	var set_unique_names = function(tracks){
		seq(_.groupBy(tracks,function(v){return v.artists[0].name===''? v.name : v.artists[0].name+' → '+v.name})).map(function(v){
			if (v[1].length===1) v[1][0].unique_name = v[0]
			else v[1].map(function(v){v.unique_name = v.artists[0].name+' → '+v.album.name+' → '+v.name})
			}) }
	var delete_boring = function(tracks){
		tracks.map(function(v){
			delete(v.isLoaded)
			delete(v.starred)
			delete(v.popularity)
			delete(v.album.isLoaded)
			v.artists.map(function(v){delete(v.isLoaded)})
			}) }
	var r = JSON.parse(JSON.stringify(tracks()))
	set_playlists(r)
	set_unique_names(r)
	delete_boring(r)
	tracks_nice = C(r); print('tracks niceified!',timer()); return r}

var main = function(){
	var tr = tracks_nice()
	print(tr.slice(0,10))
	// okay . we've generated an unique name for all tracks. what next?
	}
var save_history = function(){
	fs.writeFileSync(F('~/ali/history/auto/spotify/'+m().toISOString()+'.json'),JSON.stringify(tracks_nice(),null,'\t'))
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
[π:playlist]
[tag]
*/

}))