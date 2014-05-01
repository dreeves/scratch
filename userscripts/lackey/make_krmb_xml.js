#!/usr/bin/env node

//===----------------===// shared with make_keylayout //===----------------===//

var builder = require('xmlbuilder')
var fs = require('fs')

var print = console.log.bind(console)
var range = function(l){var r = []; for (var i=0;i<l;i++) r.push(i); return r}
var seq = function(v){return typeof v === 'string'? v.split('') : v instanceof Array? v : Object.keys(v).map(function(k){return [k,v[k]]})}
var hex = function(v,l){var r = v.toString(16); while (r.length < l) r = '0'+r; return r}

//===----------------------------===// own //===---------------------------===//

var read_lines = function(fl){return (fs.readFileSync(fl)+'').split('\n')}

/*var shells = []
var autogens = []

function shell(v){var id = (Math.random()*Math.pow(2,50)).toString(36); shells.push([id,v]); return id}
function chord(keys,out){
	keys = seq(keys)
	var left =
		keys[0]==='⌥'? '__KeyToKey__ KeyCode::'+KeyCode(keys[1])+', VK_OPTION | ModifierFlag::NONE,' :
		keys[0]==='R⌘'? '__KeyToKey__ KeyCode::'+KeyCode(keys[1])+', ModifierFlag::COMMAND_R,' :
			'__SimultaneousKeyPresses__ KeyCode::'+KeyCode(keys[0])+', KeyCode::'+KeyCode(keys[1])+','
	var right = typeof(out)==='string'? kc_hex(out) : 'KeyCode::VK_OPEN_URL_'+shell(out[0])+','
	autogens.push(left + right)
	}

chord('⌥y','✓')
chord(';l','λ')
chord('-,','←')
chord('-.','→')
chord(['R⌘','d'],['/usr/bin/osascript -e \'tell app "System Events" to keystroke "bar"\''])

var xml = builder.create({root: {
		'#list': shells.map(function(v){return {vkopenurldef: {name:'KeyCode::VK_OPEN_URL_'+v[0], url:{'@type':'shell', '#cdata':v[1]}}}}),
		item: {
			name: 'lackey',
			identifier: 'lackey',
			'#list': autogens.map(function(v){return {autogen:v}})
		}
	}})
	.end({pretty:true})*/

function parse_lackey(lackey){
	var chords = []
	lackey = lackey.filter(function(v){return !(v==='' || v.match(/^\/\//))})
	lackey = lackey.filter(function(v){var r = v.match(/^(\S+) +: +(\S+)$/); if (r) chords.push([r[2],r[1]]); return !r})
	var boards = []
	for (var i=0;i<lackey.length;i++) {
		if (lackey[i].match(/^---+/)) {
			var j=i+1; for (;j<lackey.length;j++) if (lackey[j].match(/^(-+ *)+$/)) break
			boards.push(lackey.slice(i,j+1))
			i = j } }
	boards = boards.map(function(v){var t; return (t=v[0].match(/^((-+ .+ -+)  )-+ .+ -+$/))? [v.map(function(v){return v.slice(0,t[2].length)}), v.map(function(v){return v.slice(t[1].length)})] : [v]})
	boards = boards.map(function(v){return v.map(function(v){
		var head = v[0].match(/^-+ (.+) -+$/)[1]
		var body = v.slice(1,-1).map(function(v){return v.match(/^\|(.+)\|$/)[1].match(/\S\S?/g)})
		return [head,body]})})
}

function make_krmb_xml(lackey){
	lackey = parse_lackey(lackey)
	function keycode(v){return lackey.osx_keycode[v]}
	function KeyCode(v){return v.match(/\d/)? 'KEY_'+v : v.match(/[a-z]/)? v.toUpperCase() : 'RawValue::0x'+keycode(v).toString(16)}
	function kc_hex(v){return hex(v.charCodeAt(0),4).split('').map(function(v){return 'KeyCode::'+KeyCode(v)+',ModifierFlag::OPTION_L,'}).join('')}
}

//===---------------------===// choose from argv //===---------------------===//

if (!module.parent) {
	var v = process.argv.slice(2)
	if (v.length===1) print(make_krmb_xml(read_lines(v[0])))
	else print('usage:',process.argv[1],'<.lackey file>')
	}