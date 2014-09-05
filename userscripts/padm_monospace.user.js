// ==UserScript==
// @name        padm monospace
// @namespace   comely-naiad
// @version     1.0.0
// @match       http://padm.us/*
// ==/UserScript==

if (!location.href.match(/useMonospaceFont/)) window.location = location.href+'?useMonospaceFont=true'