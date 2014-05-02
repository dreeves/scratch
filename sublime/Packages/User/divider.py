import sublime, sublime_plugin
import re
class DividerCommand(sublime_plugin.TextCommand):
	def run(self,edit):
		for region in reversed([s for s in self.view.sel()]):
			#s = self.view.substr(region)
			#self.view.replace(edit,region,hex(int(s.strip())))
			line = self.view.line(region)
			s = self.view.substr(line)
			x = re.match("^(---+)\s*(.+?)\s*$",s)
			if x:
				t = re.match("^(---+)\s*(.+?)\s*\\1$",s)
				if t:
					return
					#begin =     ('-'*((len(s)-len(t.group(2))-2 + 1)//2))+' '
					#end   = ' '+('-'*((len(s)-len(t.group(2))-2    )//2))
					#self.view.replace(edit,line,begin+t.group(2)+end)
				else:
					begin =     ('-'*((len(x.group(1))-len(x.group(2))-2 + 1)//2))+' '
					end   = ' '+('-'*((len(x.group(1))-len(x.group(2))-2    )//2))
					self.view.replace(edit,line,begin+x.group(2)+end)
			else:
				if re.match("^//=+-+.*-+=+//$",s):
					s = re.match("^//=+-+=+//\s*(.+?)\s*//=+-+=+//$",s).group(1)
				else:
					s = s.strip()
				begin =     '//==='+('-'*((80-len(s)-2 + 1)//2-10))+'===//'+' '
				end   = ' '+'//==='+('-'*((80-len(s)-2    )//2-10))+'===//'
				self.view.replace(edit,line,begin+s+end)