// ==UserScript==
// @name       scrollbar hider
// @namespace  alice0meta.scrollbar_hide
// @version    0.1
// @match      http*://*.jayisgames.com/*
// @match      http*://*.kongregate.com/*
// @match      http*://*.fimfiction.net/*
// @match      http*://*.newgrounds.com/*
// ==/UserScript==

// these are websites that i wanted to not have a horizontal scrollbar
// yay, simple apis

function hide_scrollbar_horizontal() {document.documentElement.style.overflowX = 'hidden'}

hide_scrollbar_horizontal()