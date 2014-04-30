#!/usr/bin/env node

var fs = require('fs')
var spotify = require('node-spotify')({appkeyFile: process.env.HOME+'/.spotiman/spotify_appkey.key'})

var print = console.log.bind(console)

var auth = JSON.parse(fs.readFileSync(process.env.HOME+'/.spotiman/auth.json'))

var t = Date.now()/1000
spotify.ready(function() {
	print('ready!',Date.now()/1000-t)
	var l = spotify.playlistContainer.getPlaylists()
	l.forEach(function(v,i){
		var f = function(){
			if (v.isLoaded) print('playlist!',i,v)
			else setTimeout(f,1000)
		}
		setTimeout(f,1000)
	})
})
spotify.login(auth.username, auth.password, false, false)

/*isLoaded
owner
on
off
getPlaylists
getStarred
addPlaylist
addFolder
deletePlaylist
movePlaylist*/