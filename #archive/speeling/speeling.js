// ==UserScript==
// @name	Gmail-chat Speeling Insulter
// @include	https://mail.google.com/mail/*
// @require http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js
// ==/UserScript==

var lastCL = []
var speaker
var phailWords = ['wierd', 'cloath', 'suisid', 'test']
/*var insWords = [
		[
			'tossing', 'bloody', 'shitting', 'stinky', 'raging', 'dementing', 'dumb', 'dipping', 'fucking', 'dipping',
			'holy', 'maiming', 'cocking', 'ranting', 'twunting', 'hairy', 'spunking', 'flipping', 'slapping', 'sodding',
			'blooming', 'frigging', 'sponglicking', 'guzzling', 'cock wielding', 'failed', 'artist formally known as',
			'unborn', 'pulsating', 'naked', 'throbbing', 'stale', 'spastic', 'senile', 'strangely shaped', 'bottled',
			'twin-headed', 'fat', 'gigantic', 'sticky', 'bald', 'spotty', 'spitting', 'son of a', 'dandy', 'fritzl-admiring',
			'friend of a', 'indeterminable', 'overrated', 'fingerlicking', 'diaper-wearing', 'leg-humping', 'mong loving',
			'trout-faced', 'cunt rotting', 'flip-flopping', 'rotting', 'inbred', 'badly drawn', 'undead', 'annoying', 'whoring',
			'leaking', 'dripping', 'slutty', 'cross-eyed', 'irrelevant', 'mental', 'rotating', 'scurvy looking', 'rambling',
			'gag sacking', 'cunting', 'wrinkled old', 'dried out', 'sodding', 'funky', 'unhuman', 'bloated'
		],
		[
			'cock', 'tit', 'cunt', 'wank', 'piss', 'crap', 'shit', 'arse', 'sperm', 'colon', 'shaft', 'dick', 'toss',
			'poop', 'semen', 'slut', 'suck', 'earwax', 'fart', 'toe', 'scrotum', 'cock-tip', 'slap', 'tea-bag', 'jizz', 'cockstorm',
			'bunghole', 'shitface', 'llama', 'tramp', 'fudge', 'vomit', 'puke', 'creamy baby batter'
		],
		[
			'force', 'bottom', 'hole', 'goatse', 'testicle', 'balls', 'bucket', 'biscuit', 'stain', 'flaps', 'mange',
			'twat', 'twunt', 'mong', 'spack', 'diarrhea', 'fuck', 'sod', 'excrement', 'faggot', 'pirate', 'asswipe', 'minge',
			'candle', 'torch', 'pipe', 'bint', 'udder', 'cockroach', 'worm', 'MILF', 'spunk-bubble', 'tranny'
		],
		[
			'licker', 'raper', 'wanker', 'sucker', 'felcher', 'experiment', 'wiper', 'bender', 'dictator', 'basher', 'piper', 'slapper', 'fondler', 'plonker'
		]
	]*/
/*
function randInArray(v) {v[Math.floor(Math.random() * v.length)]}
function randCurse(word) {
	word = '"'+word+'"'
	var t = Math.random()
	return t > .1? word + ' reminds me of a ' + pX(0) + ' ' + p2() + ' ' + p3() + ' ' + p4():
		randInArray([word+', srsly?', '...'+word+'.'])
}
function pX(i) {return 'eerp'}//randInArray(insWords[i])}
function p2() {return 'derp'}
function p3() {return 'herp'}
function p4() {return 'jerp'}*/
function getChatArea() {
	var elems = document.getElementsByClassName('ad3')
	if (elems.length > 1) alert("you appear to have "+elems.length+" chat windows open! I can't handle that.")
	return elems[0]
}
function handleChange(newText) {
	var addToArea = ''
	if (speaker == 'me') {
		var text = newText.match(/[a-zA-Z]+/g)
		for (var i = 0; i < text.length; i++) {
			var word = text[i]
			for (var j = 0; j < phailWords.length; j++) {
				if (word.indexOf(phailWords[j]) != -1) {
				//	addToArea += randCurse(word) + '\n.\n'
					addToArea += word + '\n.\n'
				}
			}
		}
	}
	if (addToArea) {
		var area = getChatArea()
		if (area.value == '') addToArea = addToArea.substring(0, addToArea.length - 3)
		area.value = addToArea + area.value
	}
}
function checkForChanges(cl, last, isNewSpeaker) {
	if (cl.length > 0 && last !== cl[cl.length - 1].textContent) {
		//! !== doesn't work (I guess strings are interned) so a repeat msg won't trigger this, but that's okay
		var r = cl[cl.length - 1].textContent
		if (isNewSpeaker) speaker = r.split(':')[0].trim()
		if (last) handleChange(r)
		return r
	} else return last
}
function main() {
	lastCL[0] = checkForChanges(document.getElementsByClassName('kk'), lastCL[0], true)
	lastCL[1] = checkForChanges(document.getElementsByClassName('kl'), lastCL[1], false)
	//! change to event-based instead of poll-based if possible
	window.setTimeout(main, 100)
}

main()