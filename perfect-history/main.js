#!/usr/bin/env ζ₀ core

chokidar.watch(argv._[0],{persistent:true, ignoreInitial:true}).on('all', function(ev,fl){if ({add:1,change:1,unlink:1}[ev] && !fl.match(/\/\.history\//)) {
	var now = moment().toISOString(); var short_ = fl.slice(argv._[0].length)
	print(pad(ev,' '.repeat(6)),now,short_)
	fs(argv._[0]+'/.history/'+now+' '+(ev==='unlink'?'-':'+')+' '+short_.replace(/\//g,'::')).$ = ev==='unlink'? '' : fs(fl)
}})