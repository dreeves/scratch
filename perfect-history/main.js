#!/usr/bin/env node

var fs = require('fs')
var chokidar = require('chokidar')
var m = require('moment')

var print = console.log.bind(console)

var watch = process.argv[2]

var out = function(path,msg){
	var t = path.match(/^(.*)\/([^\/]*)$/)
	return t[1]+'/.history/'+m.utc().toISOString()+' '+msg+' '+t[2]
}

var change = function(path){fs.createReadStream(path).pipe(fs.createWriteStream(out(path,'+')))}
var del = function(path){fs.writeFileSync(out(path,'-'),'')}

chokidar.watch(watch, {ignored: /[\/]\./, persistent: true})
	.on('add',   function(path){if (!path.match(/.history/)) {print('add',   path); change(path)}})
	.on('change',function(path){if (!path.match(/.history/)) {print('change',path); change(path)}})
	.on('unlink',function(path){if (!path.match(/.history/)) {print('unlink',path); del(   path)}})
	.on('error',function(e){print('chokidar error:',e)})