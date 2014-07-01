#!/usr/bin/env node

var fs = require('fs')
var path = require('path')
var chokidar = require('chokidar')
var m = require('moment')

var print = console.log.bind(console)

var watch = process.argv[2]

var out = function(fl,msg){return watch+'/.history/'+m.utc().toISOString()+' '+msg+' '+path.basename(fl)}

var change = function(fl){fs.createReadStream(fl).pipe(fs.createWriteStream(out(fl,'+')))}
var del = function(fl){fs.writeFileSync(out(fl,'-'),'')}

chokidar.watch(watch, {persistent: true, ignoreInitial: true})
	.on('add',   function(fl){if (!fl.match(/.history/)) {print('add   ',m.utc().toISOString(),path.basename(fl)); change(fl)}})
	.on('change',function(fl){if (!fl.match(/.history/)) {print('change',m.utc().toISOString(),path.basename(fl)); change(fl)}})
	.on('unlink',function(fl){if (!fl.match(/.history/)) {print('unlink',m.utc().toISOString(),path.basename(fl)); del(   fl)}})
	.on('error',function(e){print('chokidar error:',e)})