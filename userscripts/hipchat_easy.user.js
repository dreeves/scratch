// ==UserScript==
// @name        Hipchat Tweaks
// @namespace   comely-naiad
// @version     1.0.0
// @match       https://beeminder.hipchat.com/chat
// ==/UserScript==

//===--------------------------===// copied //===--------------------------===//

$.prototype.on_key = function(key,cb0){
	var t = {'⇥':[9,'↓'],'↩':[13],'⎋':[27,'↑'],'←':[37,'↓'],'↑':[38,'↓'],'→':[39,'↓'],'↓':[40,'↓']}
	var keyc = t[key]? t[key][0] : (typeof(key)==='number'? key : key.charCodeAt(0))
	this[(t[key]?{'↑':'keyup','↓':'keydown'}[t[key][1]]:0)||'keypress'](function(e){if (e.which===keyc) return cb0(e)}) }

//===---------------------------===// main //===---------------------------===//

$(window).on_key('⇥',function(e){var t; e.preventDefault()
	;((t=$('#tabs > .alert').first()).length? t :
		(t=$('#tabs > .selected').next()).length? t :
		$('#tabs > :eq(1)')
	).find('.open').click()})