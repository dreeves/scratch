def sort(v): v.sort(); return v
def cat(v): return [v for v in v for v in v]
class O: pass
def o(**kwargs): r = O(); r.__dict__.update(kwargs); return r

def simple(horiz): return any(simple_(v,x,y,horiz) for y,t in enumerate(horiz) for x,v in enumerate(t))
def simple_(v,x,y,horiz):
	if len(v.v) == 1: return False
	old = v.v.copy()
	for group in v.groups:
		v.v -= set().union(*[w.v for w in group if w is not v and len(w.v) == 1])
		t = set(cat([w.v for w in group if w is not v]))
		for e in v.v:
			if e not in t:
				v.v.clear(); v.v.add(e); break
		if group is not v.sq:
			for sq in [w.sq for w in [group[0],group[3],group[6]] if w.sq is not v.sq]:
				v.v -= set().union(*[w.v for w in sq if w in group]) - set().union(*[w.v for w in sq if w not in group])
	return v.v != old

def solve(grid):
	horiz = [[o(v=set([int(v)]),row=None,col=None,sq=None,groups=None) for v in v] for v in grid]
	vert = [[horiz[y][x] for y in range(9)] for x in range(9)]
	sq = [[horiz[y][x] for y in range(Y*3,(Y+1)*3) for x in range(X*3,(X+1)*3)] for Y in range(3) for X in range(3)]
	for y,row in enumerate(horiz):
		for x,v in enumerate(row):
			if v.v == set([0]):
				v.v.clear(); v.v |= set([1,2,3,4,5,6,7,8,9])
			v.row = row
			v.col = vert[x]
			v.sq = sq[x//3+y//3*3]
			v.groups = [v.row,v.col,v.sq]

	while simple(horiz): pass

	r = horiz[0][:3]
	for v in r:
		#if len(v.v) != 1: return [v.v for v in r]
		if len(v.v) != 1: return [[v.v for v in t] for t in sq]
	r = [v.v.pop() for v in r]
	return r[0]*100 + r[1]*10 + r[2]

grids = [v[1:] for v in zip(*(iter([line[:-1] for line in open('sudoku.txt')]),) * 10)]
def solve_all(): return [solve(v) for v in grids]

#t = solve_all()
#t[6] = 143
#print(t)

for i in range(9,50):
	try:
		for t in solve(grids[i]): print(t)
		break
	except TypeError:
		print(i,solve(grids[i]))