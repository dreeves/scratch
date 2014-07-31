#!/usr/bin/env ζ₀ core

var jsdom = require('jsdom')
var exec = require('child_process').exec
var sync = require('sync')

var session = '242uG9Ai_rZsZ9PcS-4CZqeEdUhMFoag'

var err_print = function(f){return function(){try{f()} catch (e) {console.log('ERROR:',e,e.stack)}}}
sync(err_print(function(){var t

var J = function(v){return Object.keys(v)}

var words_read = function λ(story_id,cb){
	var html = exec.sync(null,'curl --cookie "session_token='+session+';view_mature=true" http://www.fimfiction.net/story/'+story_id,{maxBuffer:64*1024*1024})[0]
	jsdom.env({
		html: html,
		scripts: ['http://code.jquery.com/jquery.js'],
		done: function(e,window){var t
			var $ = window.$
			if ($('#unread_favs').length===0) cb(null,'error')
			else cb(null,(t=$('.chapter_container')).length===0? 0 : t.filter(function(_,v){return $(v).find('.chapter-read').length > 0}).map(function(_,v){return $(v).find('.word_count').text()}).get().map(function(v){return parseInt(v.replace(/\D/g,''))}).reduce(function(a,b){return a+b},0))
		}
	})
}

words_read(parseInt(process.argv[4]),function(e,v){console.log(v)})

}))