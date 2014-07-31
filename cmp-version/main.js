#!/usr/bin/env node

var fs = require('fs')
var exec = require('child_process').exec

var i = function(v){return parseInt(v)}
var version_lt = function(a,b){a=a.split('.').map(i); b=b.split('.').map(i); return a[0]<b[0] || (a[0]===b[0] && (a[1]<b[1] || (a[1]===b[1] && a[2]<b[2])))}

var local = JSON.parse(fs.readFileSync('package.json')+'')
exec("curl '"+'https://raw.githubusercontent.com/'+local.repository.url.match(/^https:\/\/github.com\/([\w-]+\/[\w-]+)\.git$/)[1]+'/master/package.json'+"'",function(e,v){
	if (v!=='') {
		var canon = JSON.parse(v)
		if (version_lt(local.version,canon.version)) console.log(local.version+' → '+canon.version)
	}
})