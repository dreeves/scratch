// ==UserScript==
// @name        padm tweaks
// @namespace   comely-naiad
// @version     1.0.2
// @downloadURL https://github.com/alice0meta/scratch/raw/master/userscripts/padm_tweaks.user.js
// @match       http://*.padm.us/*
// @require     https://code.jquery.com/jquery-latest.js
// @require     https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min.js
// ==/UserScript==

$.css = function(v){$('head').append('<style>'+v+'</style>')}
$.on_view = function(f){document.addEventListener('visibilitychange',function(){if (!document.hidden) f()})}

$.css('.floaty_button {font-size:large; font-family:monospace; background-color:#333; padding:3px; position:fixed; top:6px; border-radius:5px; text-decoration:none; color:#fff}')

if (location.href.match(/expost.padm.us/)) {
	$('body').append('<a class="floaty_button" style="left:10%;" href="'+location.href.replace('expost.padm.us','padm.us')+'?useMonospaceFont=true">edit</a>')
} else {
	if (!location.href.match(/useMonospaceFont/)) window.location = location.href+'?useMonospaceFont=true'
	else {
		$.on_view(function(){if ($('#connectionbox').is(":visible")) location.reload()})
		$('body').append('<a class="floaty_button" style="left:30%;" href="'+location.href.replace('padm.us','expost.padm.us').replace('?useMonospaceFont=true','')+'">expost</a>')
	}
}