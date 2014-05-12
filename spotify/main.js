#!/usr/bin/env node

var F = function(v){return v[0]==='~'? process.env.HOME+v.slice(1) : v}

var fs = require('fs')

var spotify = require('node-spotify')({appkeyFile: F('~/.spotiman/spotify_appkey.key')})
var _ = require('underscore')
var m = require('moment')
var minimist = require('minimist')
var sync = require('sync')

var err_print = function(f){return function(){try{f()} catch (e) {console.log('error!',e,e.message,e.stack)}}}
sync(err_print(function(){

// http://gizmodo.com/you-can-download-any-spotify-song-as-an-mp3-with-this-c-494493386 ??
// deprecate group_by and see if underscore handles any other such things

var print = console.log.bind(console)
Array.prototype.group_by = function(f){var r = {}; this.map(function(v){var t; (r[t=f(v)]=r[t]||[]).push(v)}); return r}
var seq = function(v){return typeof v === 'string'? v.split('') : v instanceof Array? v : Object.keys(v).map(function(k){return [k,v[k]]})}
Array.prototype.m_concat = function(){return Array.prototype.concat.apply([],this)}
var object = function(v){return v.reduce(function(r,v){r[v[0]] = v[1]; return r},{})}
var C = function(v){return function(){return v}}
var err = function(v){throw Error(v)}

var args = minimist(process.argv.slice(2))

var auth = JSON.parse(fs.readFileSync(F('~/.spotiman/auth.json')))

var timer = function λ(){var t = Date.now()/1000; var r = t - λ.now; λ.now = t; return Math.round(r*100)/100+'s'}; timer()
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
	var t = seq(_.groupBy(playlists().concat(tags()),'name')).map(function(v){return v[1].length===1? undefined : v[1].map(function(v){return v.name})}).filter(function(v){return v}).m_concat()
	if (t.length > 0) err('oh no! duplicate playlists! '+t)
	print('playlists loaded!',timer()) }
var playlists = function(){playlists_(); return playlists()}
var tags = function(){playlists_(); return tags()}
var tracks = function(){
	var r = []
	playlists().map(function(playlist){playlist.getTracks().map(function(v){
		(v.playlists=v.playlists||[]).push(playlist)
		r.push(v)
	})})
	poll.sync(null,function(){return r.every(function(v){return v.isLoaded})})
	tracks = C(r)
	print('tracks loaded!',timer())
	return r}

var main = function(){
	var tr = tracks()
	tr = seq(tr.group_by(function(v){return v.link})).map(function(v){v[1][0].playlists = _.uniq(v[1].map(function(v){return v.playlists}).m_concat(),'link'); return v[1][0]})
	tr = seq(tr.group_by(function(v){return v.artists[0].name===''? v.name : v.artists[0].name+' @ '+v.name}))
		//.map(function(v){return v[1].length===1? [v] : seq(v[1].group_by(function(v){return v.artists[0].name+' @ '+v.name}))}).m_concat()
		.map(function(v){return v[1].length===1? [v] : seq(v[1].group_by(function(v){return v.artists[0].name+' @ '+v.album.name+' @ '+v.name}))}).m_concat()

	// okay . we've generated an unique name for all tracks. what next?
	// i think maybe the *first* thing we should do / should've done is just add version control
	// if we stick with nodespotify's format, we can do this really simply by just saving the json
	// we might sorta care about dates ... but eh ... screw them, it's too hard to keep track of that bullshit
	// aaand done!

	Object.keys(object(tr)).map(function(v,i){if (i < 10) print(v)})
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
			v.playlists = v.playlists.map(function(v){return v.name})
			})
		return JSON.stringify(tracks,null,'\t')}
	fs.writeFileSync(F('~/ali/history/spotify/'+m().toISOString()+'.json'),stringify_tracks(tracks()))
	print('history saved!',timer()) }

//===----------------===// call function based on args //===---------------===//

if (module.parent) print("oh my goodness, so sorry, but, spotiman isn't built to be require()'d!")
else switch (args._[0]) {
case undefined     : main(); break
case 'save-history': save_history(); break
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