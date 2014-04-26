fs = require('fs')

console.log(fs.readdirSync('.').filter(function(v){return fs.lstatSync(v).isFile()}).map(function(v){return '"'+v+'"'}).join(' '))