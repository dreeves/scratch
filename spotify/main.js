#!/usr/bin/env node

var F = function(v){return v[0]==='~'? process.env.HOME+v.slice(1) : v}

var fs = require('fs')
var spotify = require('node-spotify')({appkeyFile: F('~/.spotiman/spotify_appkey.key')})
var _ = require('underscore')
var m = require('moment')

// http://gizmodo.com/you-can-download-any-spotify-song-as-an-mp3-with-this-c-494493386 ??

var print = console.log.bind(console)
Array.prototype.group_by = function(f){var r = {}; this.map(function(v){var t; (r[t=f(v)]=r[t]||[]).push(v)}); return r}
var seq = function(v){return typeof v === 'string'? v.split('') : v instanceof Array? v : Object.keys(v).map(function(k){return [k,v[k]]})}
Array.prototype.m_concat = function(){return Array.prototype.concat.apply([],this)}
var object = function(v){return v.reduce(function(r,v){r[v[0]] = v[1]; return r},{})}

var auth = JSON.parse(fs.readFileSync(F('~/.spotiman/auth.json')))

var timer = function λ(){var t = Date.now()/1000; var r = t - λ.now; λ.now = t; return Math.round(r*100)/100+'s'}; timer()

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

spotify.ready(function(){
	print('spotify ready!',timer())
	var l = spotify.playlistContainer.getPlaylists()
	;(function λ(){
		if (l.every(function(v){return v.isLoaded || v.type})) {
			print('playlists loaded!',timer())
			var playlists = []; var tags = []; var out = playlists
			l.filter(function(v){return v.name!=='-'}).map(function(v){if (v.isLoaded) out.push(v); else if (v.name==='tags') out = tags; else out = playlists})
			var tracks = []
			playlists.map(function(playlist){playlist.getTracks().map(function(v){
				(v.playlists=v.playlists||[]).push(playlist)
				tracks.push(v)
			})})
			;(function λ(){
				if (tracks.every(function(v){return v.isLoaded})) {
					print('tracks loaded!',timer())

					fs.writeFileSync(F('~/ali/history/spotify/'+m().toISOString()+'.json'),stringify_tracks(tracks))
					print('history saved!',timer())

					tracks = seq(tracks.group_by(function(v){return v.link})).map(function(v){v[1][0].playlists = _.uniq(v[1].map(function(v){return v.playlists}).m_concat(),'link'); return v[1][0]})
					tracks = seq(tracks.group_by(function(v){return v.artists[0].name===''? v.name : v.artists[0].name+' @ '+v.name}))
						//.map(function(v){return v[1].length===1? [v] : seq(v[1].group_by(function(v){return v.artists[0].name+' @ '+v.name}))}).m_concat()
						.map(function(v){return v[1].length===1? [v] : seq(v[1].group_by(function(v){return v.artists[0].name+' @ '+v.album.name+' @ '+v.name}))}).m_concat()

					// okay . we've generated an unique name for all tracks. what next?
					// i think maybe the *first* thing we should do / should've done is just add version control
					// if we stick with nodespotify's format, we can do this really simply by just saving the json
					// we might sorta care about dates ... but eh ... screw them, it's too hard to keep track of that bullshit
					// aaand done!



					//Object.keys(object(tracks)).map(function(v){print(v)})
					/*tracks.map(function(v){
						print(v)
						{ isLoaded: true,
						  popularity: 0,
						  starred: true,
						  album: 
						   { isLoaded: true,
						     link: 'spotify:album:0000000000000000000000',
						     name: '' },
						  artists: 
						   [ { isLoaded: true,
						       link: 'spotify:artist:0000000000000000000000',
						       name: '' } ],
						  duration: 320,
						  link: 'spotify:local:::Elyot+Grant+-+Epic+Battle:320',
						  name: 'Elyot Grant - Epic Battle' }
						//if (v.album.name.match(/→/) || v.name.match(/→/)) print('what',v)
					})*/
					/*tracks.map(function(v,i){
						if ((v.artists[0].name).match(/@/) || v.album.name.match(/@/) || v.name.match(/@/)) print(i,v)
					})*/
					process.exit()
				} else setTimeout(λ,50) })()
		} else setTimeout(λ,50) })()
})
spotify.login(auth.username, auth.password, false, false)

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

/*
??:
	isLoaded
	owner
	on
	off
	getPlaylists
	getStarred
	addPlaylist
	addFolder
	deletePlaylist
	movePlaylist
playlist:
	isLoaded
	description
	link
	name
	on
	off
	getTracks
	addTracks
	removeTracks
	reorderTracks
	*/