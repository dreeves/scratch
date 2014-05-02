from math import *
from time import time
import sys

def sumsqs(n): return n*(n+1)*(2*n+1)//6

def digits(v): return [] if v == 0 else digits(v//10) + [v%10]

def undigits(v):
	r = 0
	while v: r *= 10; r += v[0]; v.pop(0)
	return r

def palindromes(v): t = digits(v); tr = t[::-1]; return (undigits(t[:-1]+tr), undigits(t+tr))

MEMO_LEN = 10000000
memo = [None for _ in range(MEMO_LEN)]
memo[0] = memo[1] = True

def exists_special(v):
	if v < MEMO_LEN and memo[v] is not None: return memo[v]
	b = 1
	while True:
		t = sumsqs(b)
		if t > v: break
		b *= 2
	a = 0
	while True:
		t = sumsqs((a+b)//2 +1)
		if t > v: b = (a+b)//2
		elif t < v: a = (a+b)//2
		else:
			if v < MEMO_LEN: memo[v] = True
			return True
		if b == a+1:
			if v < MEMO_LEN: memo[v] = False
			return False

def can_sumsqs(n):
	for b in range(1,int(sqrt(n) + (-.5 if int(sqrt(n)+0.5)**2 == n else 1)))[::-1]:
		t = sumsqs(b)
		if t < n: break
		if exists_special(t-n): return True
	return False

#2aaa + 3aa + a = b*(b+1)*(2*b+1) - n*6

r = 0
tm = time()
for v in range(2,10000):
	o,e = palindromes(v)
	O,E = (can_sumsqs(o),can_sumsqs(e))
	if O: r += o
	if E: r += e
	if O or E or time()-tm > 0.05:
		print(v,o,e,O,E,r,int((time()-tm)*1000))
		tm = time()
print(r)