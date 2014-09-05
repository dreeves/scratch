// ==UserScript==
// @name        padm tweaks
// @namespace   comely-naiad
// @version     1.0.0
// @match       http://*.padm.us/*
// @require     https://code.jquery.com/jquery-latest.js
// ==/UserScript==

$.css = function(v){$('head').append('<style>'+v+'</style>')}

$.css('.floaty_button {font-size:large; font-family:monospace; background-color:#333; padding:3px; position:fixed; top:6px; border-radius:5px; text-decoration:none; color:#fff}')

if (!location.href.match(/expost.padm.us/)) {
	if (!location.href.match(/useMonospaceFont/)) window.location = location.href+'?useMonospaceFont=true'
	$('body').append('<a class="floaty_button" style="left:30%;" href="'+location.href.replace('padm.us','expost.padm.us').replace('?useMonospaceFont=true','')+'">expost</a>')
} else {
	$('body').append('<a class="floaty_button" style="left:10%;" href="'+location.href.replace('expost.padm.us','padm.us')+'?useMonospaceFont=true">edit</a>')
}