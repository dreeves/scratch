#!/usr/bin/env node

var fs = require('fs')
var spotify = require('node-spotify')({appkeyFile: process.env.HOME+'/.spotiman/spotify_appkey.key'})

var print = console.log.bind(console)

var auth = JSON.parse(fs.readFileSync(process.env.HOME+'/.spotiman/auth.json'))

var timer = function λ(){var t = Date.now()/1000; var r = t - λ.now; λ.now = t; return r}; timer()

spotify.ready(function() {
	print('spotify ready!',timer())
	var l = spotify.playlistContainer.getPlaylists()
	;(function λ(){
		if (l.every(function(v){return v.isLoaded || v.type})) {
			print('playlists loaded!',timer())
			print(l[32].getTracks())
			for (v in l[32].getTracks()) print(l[32].name, v)
		} else setTimeout(λ,20) })()
})
spotify.login(auth.username, auth.password, false, false)

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