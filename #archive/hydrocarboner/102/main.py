from math import *

def dot_prod(a,b): x1,y1 = a; x2,y2 = b; return x1*x2 + y1*y2
def len_v(v): x,y = v; return sqrt(x*x+y*y)
def angle(v,w): return acos(dot_prod(v,w)/len_v(v)/len_v(w))

r = 0
for line in open('triangles.txt'):
	a,b,c = zip(*(map(float,line[:-1].split(',')),) * 2)
	if (angle(a,b)+angle(b,c)+angle(c,a))/(2*pi) > 0.999999:
		r += 1
print(r)