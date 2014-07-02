#!/usr/bin/env ζ₀ core

fs.SUB_BYs = function(b){return this.split(b)}
var SUB_Fs = function(s,f){return s.slice(f).slice(1).join(f)}
var SUB_Ts = function(s,t){return s.slice(t)[0]}

fs('snippets').SUB_BYs('\n\n').map(function(v,i){
	fs('Packages/User/'+i+'.sublime-snippet').$ = '<snippet><content><![CDATA['+SUB_Fs(v,'\n')+'\n'+']]></content><tabTrigger>'+SUB_Ts(v,'\n')+'</tabTrigger></snippet>'
})