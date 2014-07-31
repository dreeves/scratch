#!/usr/bin/env ζ₀ core

var jsdom = require('jsdom')
var exec = require('child_process').exec
var sync = require('sync')
var async = require('async')

var session = '242uG9Ai_rZsZ9PcS-4CZqeEdUhMFoag'

var err_print = function(f){return function(){try{f()} catch (e) {console.log('ERROR:',e,e.stack)}}}
sync(err_print(function(){var t

var M = function(v){return pad(Math.round(v/1000)/1000+'','0'.repeat(6))+'M'}

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

var wc = fs('wc.txt').exists()? JSON.parse(fs('wc.txt').$) : {}

stories = _.values(stories).filter(function(v){return !(v in wc)})
var total = _.max(_.values(stories))

;(function advance_wc(){
	async.parallel(_.range(0,10).map(function(i){return function(cb){
		if (stories[i]) exec('jsdom_usage.js '+stories[i],function(e,v){cb(e,[i,parseInt(v.trim())])})
	}}),function(e,vs){
		var latest
		vs.forEach(function(iv){var i = iv[0]; var v = iv[1]
			wc[stories[i]] = v
			latest = stories[i]
		})
		process.stdout.write('\rcumulative words read @ '+latest+'/'+total+': '+M(_.values(wc).reduce(function(a,b){return a+b},0)))
		stories = stories.slice(10)
		fs('wc.txt.bak').$ = fs('wc.txt').$+''
		fs('wc.txt').$ = JSON.stringify(wc,null,'\t')
		advance_wc()
	})
})()

}))