#!/usr/bin/env node

var fs = require('fs')

;(fs.readFileSync('snippets')+'').split('\n\n').map(function(v,i){
	v = v.split('\n')
	fs.writeFileSync('Packages/User/'+i+'.sublime-snippet','<snippet><content><![CDATA[\n'+v.slice(1).join('\n')+'\n'+']]></content><tabTrigger>'+v[0]+'</tabTrigger></snippet>')
	})