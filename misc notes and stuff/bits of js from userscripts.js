function scroll_to(v) {
	// this may be rather hacky and bad, unsure
	var t = location.href
	location.replace(typeof v === 'string'? v.replace(/^#?/,'#') : '#'+v.id)
	window.history.replaceState(null,null,t)
}

$.css(
	'body > div.content { min-height:100% }'+
	'div.header,'+
	'#goal-user > div.content-container > div.user-profile > a > img,'+
	'.dark-theme, body > div.content { -webkit-filter: invert(100%) hue-rotate(180deg) }')

// on this specific goal, display most recent datapoint
if (this_goal()==='/alice0meta/goals/team') {
	//'< no request :c >'
	var t = $.map($('.recent-data > ol > li > span'),function(v){var t = $(v).attr('data-comment').match(/\| (.*)/); return [t?[t[1],parseInt($(v).text().match(/^(\d+)/)[1])]:undefined]}).filter(function(v){return v}).slice(-1)[0]; var data = t[0]; var day = t[1]
	var msg; $('body').append(msg = $('<div style="top:20px; left:20px; position:fixed; max-width:400px; font-family:monospace; text-align:left; padding:0px 3px; border: 1px solid #000; background-color:rgba(255,255,255,.7); white-space:pre-wrap">'))
	;(function t(){
		msg.text('alice-'+(s(new Date(),'.setDate(.getDate()-1)').getDate() === day? 'yesterday' : day)+' asks:\n'+data)
		run.tomorrow(t)})()
}