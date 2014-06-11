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

// http://gizmodo.com/you-can-download-any-spotify-song-as-an-mp3-with-this-c-494493386 ?
// we need to be able to query things other than our tracks. like artists and such.

var print = console.log.bind(console)
var seq = function(v){return typeof v === 'string'? v.split('') : v instanceof Array? v : Object.keys(v).map(function(k){return [k,v[k]]})}
Array.prototype.m_concat = function(){return Array.prototype.concat.apply([],this)}
var object = function(v){return v.reduce(function(r,v){r[v[0]] = v[1]; return r},{})}
var C = function(v){return function(){return v}}
var err = function(v){throw(Error(v))}
var is = function(v){return v !== undefined}

var args = minimist(process.argv.slice(2))

var auth = JSON.parse(fs.readFileSync(F('~/.spotiman/auth.json')))

var timer = function λ(){var t = Date.now()/1000; var r = t - λ.now; λ.now = t; return Math.round(r*100)/100+'s'}; timer()
var poll = function(f,cb){(function λ(){if (f()) cb(); else setTimeout(λ,50)})()}

var login = function(){
	spotify.login(auth.username, auth.password, false, false)
	spotify.ready.sync()
	print('spotify ready!',timer()); login = C() }
var playlists = function(){
	login()
	var r = spotify.playlistContainer.getPlaylists().filter(function(v){return !v.type})
	poll.sync(null,function(){return r.every(function(v){return v.isLoaded})})
	var t; if (t=seq(_.groupBy(r,'name')).map(function(v){return v[1].length===1? undefined : v[1][0].name}).filter(function(v){return v})[0]) err('oh no! duplicate playlist! '+t)
	print('playlists loaded!',timer()); return (playlists = C(r))()}
var tracks = function(){
	var r = playlists().map(function(v){return v.getTracks()}).m_concat()
	poll.sync(null,function(){return r.every(function(v){return v.isLoaded})})
	r = _.values(_.indexBy(r,'link'))
	print('tracks loaded!',timer())

	// clone
	r = r.map(function(v){var r = JSON.parse(JSON.stringify(v)); r.original = v; return r})

	// .playlists
	r.map(function(v){v.playlists = {}})
	var t = _.indexBy(r,'link')
	var dup
	playlists().map(function(playlist){playlist.getTracks().map(function(v,i){
		var l = t[v.link].playlists
		if (is(l[playlist.name])) {dup = true; print('dupl track:',playlist.name,'::',v.name,'@',l[playlist.name],'&',i)}
		else l[playlist.name] = i
	})})
	if (dup) err('there was a duplicate track :(')

	// .unique_name
	seq(_.groupBy(r,function(v){return v.artists[0].name===''? v.name : v.artists[0].name+' → '+v.name})).map(function(v){
		if (v[1].length===1) v[1][0].unique_name = v[0]
		else v[1].map(function(v){v.unique_name = v.artists[0].name+' → '+v.album.name+' → '+v.name})
		})

	// delete boring
	r.map(function(v){
		delete(v.isLoaded)
		delete(v.starred)
		delete(v.popularity)
		delete(v.album.isLoaded)
		v.artists.map(function(v){delete(v.isLoaded)})
		})

	print('tracks niceified!',timer()); return (tracks = C(r))()}

var main = function(){
	var add_tracks = function(p,ts){
		print('adding',Math.min(ts.length,500),'tracks out of',ts.length,'tracks')
		p.addTracks(ts.slice(0,500).map(function(v){return v.original}),0)
		}
	var pl = _.indexBy(playlists(),'name')
	var tr = _.indexBy(tracks(),'link')
	var get_tracks = function(p){return p.getTracks().map(function(v){return tr[v.link]})}

	var t = tracks().filter(function(v){return is(v.playlists['clbuttic']) && is(v.playlists['special'])})
	
	t = _.sortBy(t.map(function(v){return v.playlists['clbuttic']}))
	print(pl.clbuttic.removeTracks(t))
	//print(get_tracks(pl['don\'t want to hear again']))
	//add_tracks(pl['meh pile'],tracks().filter(function(v){return v.playlists.length===1 && v.playlists[0]==='mehp'}))
	}
var backup = function(){
	var tr = tracks()
	tr.map(function(v){
		delete(v.original)
		//v.tracks = Object.keys(v.tracks)
	})
	fs.writeFileSync(F('~/ali/history/auto/spotify/'+m().toISOString()+'.json'),JSON.stringify(tr,null,'\t'))
	print('backup saved!',timer()) }

/*
track: ?name? [
	α:<artist>
	ρ:<album>
	τ:<track>
	λ:<length>
	μ:local
	[playlist]
	]
*/

//===----------------===// call function based on args //===---------------===//

if (module.parent) print("oh my goodness, so sorry, but, spotiman isn't built to be require()'d!")

switch (args._[0]) {
case undefined: main(); break
case 'backup' : backup(); break
case 'e'      : print(eval(process.argv.slice(3).join(' '))); break
default       : print('usage: $ <etc>'); break
}

//spotify.logout.sync(spotify)
//process.exit()

//===---------------------------===// <end> //===--------------------------===//

}))