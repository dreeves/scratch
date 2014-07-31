// ==UserScript==
// @name        ebg13
// @namespace   comely-naiad
// @version     1.0.0
// @match       *://*/*
// @require     https://code.jquery.com/jquery-latest.js
// ==/UserScript==

var rep = {'a':'n','A':'N','n':'a','N':'A'}

$(document).find(':not(iframe)').addBack().contents().filter(function(){return this.nodeType == 3}).map(function(i,v){v.data = v.data.replace(/[anAN]/g,function(v){return rep[v]||v})})