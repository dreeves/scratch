def C(n,k):
	r = 1
	for i in range(n,n-k,-1): r*=i
	for i in range(1,k+1): r//=i
	return r

def q(f):
	a = 1-f; b = 1+2*f
	for i in range(1001):
		if a**(1000-i) * b**i >= 1000000000:
			return i
	return 2000

idx = min([q(i/100) for i in range(34)])
ways = [C(1000,i) for i in range(1001)]
print(sum(ways[idx:])/sum(ways))