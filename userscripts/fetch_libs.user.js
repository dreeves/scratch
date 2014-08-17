// ==UserScript==
// @name        fetch libs
// @namespace   comely-naiad
// @version     1.0.0
// @match       http://transparent-favicon.info/favicon.ico
// @require     https://code.jquery.com/jquery-latest.js
// @require     https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min.js
// ==/UserScript==

$.css = function(v){$('head').append('<style media="screen" type="text/css">'+v+'</style>')}

unsafeWindow.$ = $
unsafeWindow._ = _

var beep = function(){new Audio('https://raw.githubusercontent.com/dreeves/TagTime/master/sound/loud-ding.wav').play()}

$('body').removeAttr('style').html($('<table>').html(_.range(10).map(function(v){return $('<tr>').html(_.range(36).map(function(v,i){return i%6===0? $('<td class="spacing">') : $('<td class="ready">')}))})))

$('table').on('click','td',function(){$(this).toggleClass('checked').text($(this).hasClass('checked')? '✓' : '')})

var ft = setInterval(function(){var t=$('td.ready').first(); t.removeClass('ready').addClass('done'); if (t.hasClass('checked')) beep()},3600/300*1000)

$.css(
	'body {background-color:#222;}'+
	'td {width:32px; height:32px; text-align:center; cursor:pointer;}'+
	'.spacing {width:0;}'+
	'.ready {background-color:#888;}'+
	'.done {background-color:#090;}'+
	'')