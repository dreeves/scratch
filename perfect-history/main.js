#!/usr/bin/env ζ₀ core

chokidar.watch(argv._[0],{persistent:true, ignoreInitial:true}).on('all', function(ev,fl){if ({add:1,change:1,unlink:1}[ev] && !fl.match(/\/\.history\//)) {
	var now = moment().toISOString(); var base = path.basename(fl)
	print(pad(ev,' '.repeat(6)),now,base)
	fs(argv._[0]+'/.history/'+now+' '+(ev==='unlink'?'-':'+')+' '+base).$ = ev==='unlink'? '' : fs(fl)
}})