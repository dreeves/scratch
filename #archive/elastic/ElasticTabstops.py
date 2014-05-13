MONOSPACED_FONT_SIZE = 8
MIN_COL_WIDTH = MONOSPACED_FONT_SIZE * 4
COL_PADDING = MONOSPACED_FONT_SIZE * 1

def column_width(text_width): max(text_width//MONOSPACED_FONT_SIZE*MONOSPACED_FONT_SIZE + COL_PADDING, MIN_COL_WIDTH)

def stretchTabstops(lines):
	# get width of text in cells
	lltabs = []
	for line in lines:
		txtX = 0 # text width in tab
		for c in line:
			if c == '\t':
				lltabs[-1].add([true, [column_width(txtX)]])
				txtX = 0
			else txtX += charWidth(c)
		lltabs.add([false, nil])
	
	colMax = []
	# find columns blocks and stretch to fit the widest cell
	for v in lltabs: for i, l@[endsInTab, [cwidth]] in v:
		# all tabstops in column block point to same number
		while colMax.len <= i: colMax.add([])
		if endsInTab:
			colMax[i][0] max= cwidth
			l[1] = colMax[i]
		else colMax[i] = [] # end column block