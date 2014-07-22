#!/usr/bin/env ζ₀ core

var jsdom = require('jsdom')
var exec = require('child_process').exec
var sync = require('sync')
var async = require('async')

var session = 'Or7h8xEvn1Pc2ASGi5S_3W7no8_-szyU'

var err_print = function(f){return function(){try{f()} catch (e) {console.log('ERROR:',e,e.stack)}}}
sync(err_print(function(){var t

//===----------------------===// valid story ids //===---------------------===//

var stories = fs('stories.txt').exists()? _.object(t=fs('stories.txt').$.split('\n'),t.map(ζ0_int)) : {}
var stories_len = _.max(_.values(stories))+1

var is_story = function(v){return !JSON.parse(exec.sync(null,'curl http://www.fimfiction.net/api/story.php?story='+v)[0]).error}
var extend_stories = function(len,cb){async.parallel(_.range(stories_len,stories_len+len).map(function(i){return function(cb){sync(err_print(function(){stories_len++; if (is_story(i)) stories[i] = i; cb()}))}}),cb)}

// _.range((210000-stories_len)/32).map(function(i){
// 	extend_stories.sync(null,32)
// 	process.stdout.write('\rstory max: '+stories_len)
// 	if (i%20===0) fs('stories.txt').$ = _.sortBy(_.values(stories)).join('\n')
// })
// fs('stories.txt').$ = _.sortBy(_.values(stories)).join('\n')

//===------------------------===// words_read //===------------------------===//

var words_read = function λ(story_id,cb){
	if (λ.memo[story_id]) cb(null,λ.memo[story_id])
	else jsdom.env({
		html: exec.sync(null,'curl --cookie "session_token='+session+';view_mature=true" http://www.fimfiction.net/story/'+story_id,{maxBuffer:64*1024*1024})[0],
		scripts: ['http://code.jquery.com/jquery.js'],
		done: function(e,window){var t
			var $ = window.$
			cb(null,λ.memo[story_id] = (t=$('.chapter_container')).length===0? 0 : t.filter(function(_,v){return $(v).find('.chapter-read').length > 0}).map(function(_,v){return $(v).find('.word_count').text()}).get().map(function(v){return parseInt(v.replace(/\D/g,''))}).reduce(function(a,b){return a+b},0))
		}
	})
}

words_read.memo = fs('wc.txt').exists()? JSON.parse(fs('wc.txt').$) : {}

//===---------------------------===// main //===---------------------------===//

stories = _.values(stories)
var j=0; stories.some(function(v,i){
	if (words_read.memo[v]===undefined) {print('words read in',v,'@',i+':',words_read.sync(null,v)); j++}
	if (j%10===0) fs('wc.txt').$ = JSON.stringify(words_read.memo,null,'\t')
})
fs('wc.txt').$ = JSON.stringify(words_read.memo,null,'\t')

}))