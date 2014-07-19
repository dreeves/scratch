#!/usr/bin/env ζ₀ core

//===----------------===// shared with make_keylayout //===----------------===//

var builder = require('xmlbuilder')

var range = function(l){var r = []; for (var i=0;i<l;i++) r.push(i); return r}
var seq = function(v){return typeof v === 'string'? v.split('') : v instanceof Array? v : Object.keys(v).map(function(k){return [k,v[k]]})}
var hex = function(v,l){var r = v.toString(16); while (r.length < l) r = '0'+r; return r}

//===----------------------------===// own //===---------------------------===//

var _ = require('underscore')

//! right command is maybe problematic

Array.prototype.m_concat = function(){return Array.prototype.concat.apply([],this)}
var read_lines = function(fl){return fs(fl).$.split('\n')}
var object = function(v){return v.reduce(function(r,v){r[v[0]] = v[1]; return r},{})}

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
	.end({pretty:true})

print(xml)*/

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
	boards = boards.map(function(v){return v.map(function(v,i){
		var hots = v[0].match(/^-+ (.+) -+$/)[1]
		var keys = v.slice(1,-1).map(function(v){return v.match(/^\|(.+)\|$/)[1].match(/\S\S?/g)})
		var t; return i===0? (t=hots.match(/^(.+) (\S+)$/))? {hots:t[1], name:t[2], keys:keys} : {name:hots, keys:keys} : {hots:hots, keys:keys} }) })
	var homoiconic;  boards = boards.filter(function(v){if (v[0].name==='homoiconic' ) {homoiconic  = v[0]; return false} return true})
	var osx_keycode; boards = boards.filter(function(v){if (v[0].name==='osx_keycode') {osx_keycode = v[0]; return false} return true})
	boards.map(function(v){v.map(function(v){
		var f_ = function(){return '_'}
		if (v.keys.length < homoiconic.keys.length)
			v.keys = [].concat([homoiconic.keys[0].map(f_)],v.keys,[homoiconic.keys.slice(-1)[0].map(f_)])
		})})
	;[homoiconic,osx_keycode].concat(boards.m_concat()).map(function(v){v.keys = v.keys.m_concat()})
	;[osx_keycode].concat(boards.m_concat()).map(function(v){v.keys = _.zip(homoiconic.keys,v.keys).filter(function(v){return v[1]!=='_'})})
	boards.map(function(boards){boards.slice(1).map(function(v){v.hots += boards[0].hots})})
	chords = chords.concat(boards.m_concat().map(function(board){return board.keys.map(function(v){return [board.hots+v[0],v[1]]})}).m_concat())
	osx_keycode = object(osx_keycode.keys)
	return {osx_keycode:osx_keycode, chords:chords}}
function parse_hots(hots){
	return hots.split('')
}
function make_xml(lackey){
	lackey = parse_lackey(lackey)
	function keycode(v){return lackey.osx_keycode[v]}
	function KeyCode(v){return v.match(/\d/)? 'KEY_'+v : v.match(/[a-z]/)? v.toUpperCase() : 'RawValue::0x'+keycode(v).toString(16)}
	function kc_hex(v){return seq(v).map(function(v){return hex(v.charCodeAt(0),4).split('').map(function(v){return 'KeyCode::'+KeyCode(v)+',ModifierFlag::OPTION_L,'}).join('')}).join('')}
	var autogens = [
	    '__KeyToKey__ KeyCode::PC_APPLICATION, VK_CONTROL, KeyCode::TAB,ModifierFlag::CONTROL_L|ModifierFlag::SHIFT_L',
	    '__KeyToKey__ KeyCode::PC_APPLICATION, ModifierFlag::NONE, KeyCode::RawValue::0x82',
	    ]
	var shells = [['dummy','echo dummy']]
	function shell(v){var id = (Math.random()*Math.pow(2,50)).toString(36); shells.push([id,v]); return id}
	lackey.chords.map(function(v){
		var hots = parse_hots(v[0])
		var out = v[1]
		if (hots.length===3) return
		var left =
			hots[0]==='⌥'? '__KeyToKey__ KeyCode::'+KeyCode(hots[1])+', VK_OPTION|ModifierFlag::NONE,' :
			//hots[0]==='R⌘'? '__KeyToKey__ KeyCode::'+KeyCode(keys[1])+', ModifierFlag::COMMAND_R,' :
				'__SimultaneousKeyPresses__ KeyCode::'+KeyCode(hots[0])+', KeyCode::'+KeyCode(hots[1])+','
		var right = typeof(out)==='string'? kc_hex(out) : 'KeyCode::VK_OPEN_URL_'+shell(out[0])+','
		autogens.push(left + right) })
	return builder.create({root: {
		'#list': shells.map(function(v){return {vkopenurldef: {name:'KeyCode::VK_OPEN_URL_'+v[0], url:{'@type':'shell', '#cdata':v[1]}}}}),
		item: {name:'lackey', identifier:'lackey', '#list':autogens.map(function(v){return {autogen:v}}) }
		}}).end({pretty:true}) }

//===---------------------===// choose from argv //===---------------------===//

if (argv._.length===1) print(make_xml(read_lines(argv._[0])))
else print('usage: make_krmb.js <.lackey file>')