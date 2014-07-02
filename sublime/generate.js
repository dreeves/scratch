#!/usr/bin/env ζ₀ core

fs('snippets').ζ0_SUB_BYs('\n\n').map(function(v,i){
	fs('Packages/User/'+i+'.sublime-snippet').$ = '<snippet><content><![CDATA['+ζ0_SUB_Fs(v,'\n')+'\n'+']]></content><tabTrigger>'+ζ0_SUB_Ts(v,'\n')+'</tabTrigger></snippet>'
})