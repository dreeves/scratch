from itertools import *

s = [
	lambda n: n*(n+1)//2,
	lambda n: n*n,
	lambda n: n*(3*n-1)//2,
	lambda n: n*(2*n-1),
	lambda n: n*(5*n-3)//2,
	lambda n: n*(3*n-2)]
graph = [[[(T[0],v),[[],[],[],[],[],[]]] for v in takewhile(lambda v: v < 10000, dropwhile(lambda v: v < 1000, map(T[1], count())))] for T in enumerate(s)]
for si,s in enumerate(graph):
	for [_,v],nodes in s:
		for i in range(6):
			if i != si:
				for node in graph[i]:
					[_,w],_ = node
					if w//100 == v%100:
						nodes[i].append(node)

N = 6

def search(start,node,d,l):
	if d == 0:
		if start is node and len(dict(l)) == N: print('MATCH',start[0],dict(l),sum(dict(l).values()))
	else:
		for v in node[1]:
			for v in v:
				search(start,v,d-1,l+[v[0]])

for s in graph:
	for node in s:
		search(node,node,N,[node[0]])