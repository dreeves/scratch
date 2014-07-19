#!/usr/bin/env ζ₀ core

ζ0_memb_Emod_obj(fs(argv._[0]),'$',function(v){
	return JSON.stringify(ζ0_memb_Emod_obj(JSON.parse(v),'version',
		function(v){return ζ0_memb_Emod_obj(v.split('.'),2,function(v){return (parseInt(v)+1)+''}).join('.')})
		,null,'\t')})