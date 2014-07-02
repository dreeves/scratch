#!/usr/bin/env ζ₀ core

var sub_Emod_obj = function(o,m,f){o[m] = f(o[m]); return o}

sub_Emod_obj(fs('package.json'),'$',function(v){return JSON.stringify(sub_Emod_obj(JSON.parse(v),'version',function(v){return sub_Emod_obj(v.split('.'),2,function(v){return (parseInt(v)+1)+''}).join('.')}),null,'\t')})