// ==UserScript==
// @name       fb avatar shuffler
// @namespace  alice0meta.fb_shuffle
// @version    0.1
// @match      https://www.facebook.com/?profilerotation=do
// @match      https://www.facebook.com/zii.prime?success=1
// @match      https://www.facebook.com/photo.php?fbid=*&makeprofile=1
// ==/UserScript==

// never got around to writing code to grab the personal info automatically, so some things are hardcoded

// fb changed something that broke the not-spamming-all-your-friends functionality; this can be fixed but it requires more work

var rand = Math.random
function rand_nth(vs){return vs.length === 0? undefined : vs[Math.floor(rand()*vs.length)]}
 
var pictures = ['474226756030465','319778661475276','358527790933696','473348376118303','473348462784961','473348539451620','473348586118282','473348689451605','473348736118267','473348779451596','473348826118258','473348892784918','473348936118247','473348976118243','473349052784902','473295929456881']
function set_random_picture(){location.href = 'https://www.facebook.com/photo.php?fbid=' + rand_nth(pictures) + '&makeprofile=1'}

if (document.URL === 'https://www.facebook.com/?profilerotation=do') {
	set_random_picture()
} else if (document.URL === 'https://www.facebook.com/zii.prime?success=1') {
	document.title = '! avatar shuffling app !'
	setTimeout(set_random_picture, 10*60*1000)
} else {
	window.addEventListener('load', function confirm(){
		var buttons = document.getElementsByName('confirm')
		if (buttons.length === 0) setTimeout(confirm,20)
		else if (buttons.length === 1) buttons[0].click()
		else alert('wow. such error. much buttons. many confused.')
		})
}