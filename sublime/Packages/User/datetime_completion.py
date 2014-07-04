import sublime, sublime_plugin, time
import datetime

def round_time(dt, round_to):
	seconds = (dt - dt.min).seconds
	rounding = (seconds+round_to/2) // round_to * round_to
	return dt + datetime.timedelta(0,rounding-seconds,-dt.microsecond)

class _(sublime_plugin.EventListener):
	def on_query_completions(self, view, prefix, locations):
		if False: pass
		elif prefix == 'u'   : r = datetime.datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
		elif prefix == 'utc' : r = datetime.datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
		elif prefix == 'unow': r = datetime.datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
		elif prefix == 'now' : r = datetime.datetime.now().strftime('%Y-%m-%d/%H:%M')
		elif prefix == 'day' : r = datetime.datetime.now().strftime('%Y-%m-%d')
		#elif prefix == 'time': r = datetime.datetime.now().strftime('%H:%M')
		elif prefix == 'time': r = round_time(datetime.datetime.now(),15*60).strftime('%H:%M')
		else                 : r = None
		return [(prefix,prefix,r)] if r else []