import sublime, sublime_plugin
import re

def make_divider(head,mid,s,length):
	a = head
	b = head[::-1]+' '+s+' '+head
	c = head[::-1]
	t = length-len(a)-len(b)-len(c)
	return a + (mid*((t+1)//2)) + b + (mid*(t//2)) + c

class DividerCommand(sublime_plugin.TextCommand):
	def run(self,edit,style,length):
		# ext = re.sub("^.*\.([^.]+)$","\\1",self.view.file_name())
		for region in reversed([s for s in self.view.sel()]):
			line = self.view.line(region)
			s = self.view.substr(line)
			if style == "big":
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
					begin =     '//==='+('-'*((length-len(s)-2 + 1)//2-10))+'===//'+' '
					end   = ' '+'//==='+('-'*((length-len(s)-2    )//2-10))+'===//'
					self.view.replace(edit,line,begin+s+end)
			elif style == "small":
				t = re.match("^---+\s*(.+?)\s*---+$",s)
				self.view.replace(edit,line,make_divider('','-',t.group(1) if t else s.strip(),length))