import sys
import codecs
import re

t = [re.sub("\{UNICODE (.+?)\}",lambda v: ''.join('{U+%04x}'%ord(v) for v in v.group(1)), line)
	for line in codecs.open(sys.argv[1], "r", "utf-8")]
f = codecs.open(sys.argv[1], "w", "utf-8")
f.write('\ufeff') # ahk requires bom
for v in t: f.write(v)