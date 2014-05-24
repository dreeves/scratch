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

var args = minimist(process.argv.slice(2))

var auth = JSON.parse(fs.readFileSync(F('~/.spotiman/auth.json')))

var timer = function λ(){var t = Date.now()/1000; var r = t - λ.now; λ.now = t; return Math.round(r*100)/100+'s'}; timer()
var poll = function(f,cb){(function λ(){if (f()) cb(); else setTimeout(λ,50)})()}

var login = function(){
	spotify.login(auth.username, auth.password, false, false)
	spotify.ready.sync()
	print('spotify ready!',timer()); login = C() }
var tags = function(){
	login()
	var r = spotify.playlistContainer.getPlaylists().filter(function(v){return !v.type})
	poll.sync(null,function(){return r.every(function(v){return v.isLoaded})})
	var t; if (t=seq(_.groupBy(r,'name')).map(function(v){return v[1].length===1? undefined : v[1][0].name}).filter(function(v){return v})[0]) err('oh no! duplicate tag! '+t)
	print('tags loaded!',timer()); return (tags = C(r))()}
var tracks = function(){
	var r = tags().map(function(v){return v.getTracks()}).m_concat()
	poll.sync(null,function(){return r.every(function(v){return v.isLoaded})})
	r = _.values(_.indexBy(r,'link'))
	print('tracks loaded!',timer())

	// clone
	r = r.map(function(v){var r = JSON.parse(JSON.stringify(v)); r.original = v; return r})

	// .tags
	r.map(function(v){v.tags=v.tags||[]})
	var t = _.indexBy(r,'link')
	tags().map(function(tag){tag.getTracks().map(function(v){if (_.indexOf(t[v.link].tags,tag.name) === -1) t[v.link].tags.push(tag.name)})})

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
	var tr = tracks()
	var tgs = _.indexBy(tags(),'name')
	// next: remove all tracks from starred that don't match this filter OR make a playlist with just the tracks in this filter OR add the tracks in this filter to a chosen playlist
	//print(tr.filter(function(v){return v.tags.length===1 && v.tags[0]==='starred'}).map(function(v){return v.unique_name}))
	//print(tgs.tmp.getTracks())
	//print(tgs.tmp.addTracks(tr.filter(function(v){return v.tags.length===1 && v.tags[0]==='starred'}).map(function(v){return v.original}),0))
	print(tgs.tmp,tgs.temp)
	// gah. why can't i edit anything?
	}
var save_history = function(){
	fs.writeFileSync(F('~/ali/history/auto/spotify/'+m().toISOString()+'.json'),JSON.stringify(tracks().map(function(v){delete(v.original); return v}),null,'\t'))
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
μ // mp3 . is a local file
[π:playlist]
[tag]
*/

}))