import sublime, sublime_plugin, time
from datetime import datetime
class _(sublime_plugin.EventListener):
	def on_query_completions(self, view, prefix, locations):
		if   prefix == 'now'   : r = datetime.now()   .strftime('%Y-%m-%d/%H:%M')
		elif prefix == 'isonow': r = datetime.now()   .strftime('%Y-%m-%dT%H:%M:%S')
		elif prefix == 'utcnow': r = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S')
		elif prefix == 'day'   : r = datetime.now().strftime('%Y-%m-%d')
		elif prefix == 'time'  : r = datetime.now().strftime('%H:%M')
		else                   : r = None
		return [(prefix, prefix, r)] if r else []