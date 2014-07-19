// ==UserScript==
// @name        Beeminder Tweak :: Delete Button
// @namespace   comely-naiad
// @version     1.0.0
// @match       http*://*.beeminder.com/*
// ==/UserScript==

$('.delete_datapoint').css({padding:'0px 1em'}).removeAttr('data-confirm')