import mechanize
import cookielib
import urllib
import re

def simple_mechanize_browser():
	# Browser
	r = mechanize.Browser()

	# Cookie Jar
	cj = cookielib.LWPCookieJar()
	r.set_cookiejar(cj)

	# Browser options
	r.set_handle_equiv(True)
	r.set_handle_gzip(True)
	r.set_handle_redirect(True)
	r.set_handle_referer(True)
	r.set_handle_robots(False)

	# Follows refresh 0 but not hangs on refresh > 0
	r.set_handle_refresh(mechanize._http.HTTPRefreshProcessor(), max_time=1)

	return r

def words_read_in(page):
	r = 0
	while True:
		tick = '//www.fimfiction-static.net/images/icons/tick.png'
		i = page.find(tick)
		if i == -1: return r
		page = page[i+len(tick):]
		r += int(re.compile('\( ([\d,]+) words \)').search(page).group(1).replace(',',''))

all_bytes = 1510*1024*1024
for th in range(21,90):
	ul = urllib.FancyURLopener()
	br = simple_mechanize_browser()
	br.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071615 Fedora/3.0.1-1.fc9 Firefox/3.0.1'),
					('Cookie', 'PHPSESSID=7805cahnk35i5r3q3locd7h2s0; selected_theme=noben_celestia; theme=undefined; username=zii-prime; auto_login=bd66f0959b0f7847581bef2c2ff17776; __utma=229015819.1232395759.1357354881.1361979157.1361982786.244; __utmb=229015819.32.10.1361982786; __utmc=229015819; __utmz=229015819.1361549275.214.10.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); view_mature=true')]
	dump = open('dump/'+str(th)+'.txt','w')
	for i in range(0,1000):
		story_id = th*1000 + i
		api_page = ul.open('http://www.fimfiction.net/api/story.php?story='+str(story_id)).read()
		if api_page != '{"error":"Invalid story id"}':
			full_page = br.open('http://www.fimfiction.net/story/'+str(story_id)).read()
			words = words_read_in(full_page)
			dump.write('%i %i\n' % (story_id, words))
			all_bytes += len(full_page)
			print th, i, words, all_bytes // (1024*1024)
	dump.close()