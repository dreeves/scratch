// ==UserScript==
// @name       lesswrong unread controls
// @namespace  alice0meta.lw_unread
// @version    0.1
// @match      http*://*.lesswrong.com/*
// ==/UserScript==

// may break on dynamically loaded new comments

function scroll_to(v) {
	// this may be rather hacky and bad, unsure
	var t = location.href
	location.replace(typeof v === 'string'? v.replace(/^#?/,'#') : '#'+v.id)
	window.history.replaceState(null,null,t)
}

(function($){
	function char(v) {return typeof v === 'number'? v : v.charCodeAt(0)}
	function on_key(key,f) {$(window).keydown(function(e){if (e.which === char(key)) f(e)})}
	function mod(a,b) {return ((a%b)+b) % b}

	var new_comments = $('.new-comment')
	if (new_comments.length === 0) return

	var button = $('<input id="next-unread" type="button" style="top:20px; left:20px; position:fixed; font-family:monospace; background-color:white; text-align:left">')

	var at = -1
	function update_button() {button[0].value = 'unread('+(at+1)+'/'+new_comments.length+')\nkeys: [←] [→]'}
	function update(prev_at) {
		scroll_to(new_comments[at])
		// lw uses !important, which is hard to override :c
		$(new_comments[at]).addClass('at-comment')
		$(new_comments[prev_at]).addClass('new-comment')
		$(new_comments[prev_at]).removeClass('at-comment')
		$(new_comments[at]).removeClass('new-comment')
		update_button()
	}

	update_button()

	on_key(37,                     function(){var t = at; at=mod(at-1, new_comments.length); update(t)}) // previous
	on_key(39, button[0].onclick = function(){var t = at; at=mod(at+1, new_comments.length); update(t)}) // next

	$('head').append($('<style type="text/css"> .at-comment {'+
		'border: 5px solid #ee90ee !important;'+
		'padding: 0 0 0 10px !important;'+ // needed only because we have to remove new-comment
		'margin-right: 0 !important;'+
		'} </style>'))
	$('body').append(button)
})(jQuery)