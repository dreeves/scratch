#!/usr/bin/env node

var fs = require('fs')

var sub_Emod_obj = function(o,m,f){o[m] = f(o[m]); return o}

var t = JSON.parse(fs.readFileSync('package.json'))
t.version = sub_Emod_obj(t.version.split('.'),2,function(v){return (parseInt(v)+1)+''}).join('.')
fs.writeFileSync('package.json',JSON.stringify(t,null,'\t'))