#!/usr/bin/env node

var fs = require('fs')
var spotify = require('spotify')({appkeyFile: 'spotify_appkeyFile.key'})

var auth = JSON.parse(fs.readFileSync('auth.json'))

spotify.ready(function() {
	var starredTracks = spotify.playlistContainer.getStarred().getTracks()
	spotify.player.play(starredTracks[0])
	})

spotify.login(auth.username, auth.password, false, false)