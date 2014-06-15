import sublime, sublime_plugin, time
from datetime import datetime
class _(sublime_plugin.EventListener):
	def on_query_completions(self, view, prefix, locations):
		if False: pass
		elif prefix == 'u'   : r = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
		elif prefix == 'utc' : r = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
		elif prefix == 'unow': r = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
		elif prefix == 'now' : r = datetime.now().strftime('%Y-%m-%d/%H:%M')
		elif prefix == 'day' : r = datetime.now().strftime('%Y-%m-%d')
		elif prefix == 'time': r = datetime.now().strftime('%H:%M')
		else                 : r = None
		return [(prefix,prefix,r)] if r else []