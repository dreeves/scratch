#include <cstdio>
#include <iostream>
#include <iomanip> // for setprecision
#include <algorithm> // for copy
#include <iterator> // for ostream_iterator
#include <cmath>
#include <map>
#include <string>
#include <vector>
#include <stdexcept>
#include <bitset>

using namespace std;

//===--------------------------------------------===// utils //===--------------------------------------------===//

#define PASTE2(a,b) a ## b
#define PASTE5(a,b,c,d,e) a ## b ## c ## d ## e
#define PASTE2_2(a,b) PASTE2(a,b)
#define PASTE_2(...) PASTE2_2(PASTE,__VA_LEN__(__VA_ARGS__))(__VA_ARGS__)
#define ARG_16(_0,_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11,_12,_13,_14,n,...) n
#define HAS_NO_COMMA(...) ARG_16(__VA_ARGS__,     0, 0, 0, 0, 0,0,0,0,0,0,0,0,0,0,1)
#define HAS_COMMA(...)    ARG_16(__VA_ARGS__,     1, 1, 1, 1, 1,1,1,1,1,1,1,1,1,1,0)
#define __VA_LEN__(...)   ARG_16(0,##__VA_ARGS__,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0)
#define IS_NOT_EMPTY(...) IS_NOT_EMPTY_(HAS_COMMA(__VA_ARGS__), HAS_COMMA(TRIGGER __VA_ARGS__), HAS_COMMA(__VA_ARGS__()), HAS_COMMA(TRIGGER __VA_ARGS__ ()))
#define IS_NOT_EMPTY_(a,b,c,d) HAS_NO_COMMA(PASTE5(IS_NOT_EMPTY__,a,b,c,d))
#define IS_NOT_EMPTY__0001 ,
#define TRIGGER(...) ,

#define MACRO_DISPATCH(fn,...) PASTE2_2(fn,__VA_LEN__(__VA_ARGS__))(__VA_ARGS__)
#define MACRO_DISPATCH_OPT3(fn,a,b,c) PASTE_2(fn,_,IS_NOT_EMPTY(a),IS_NOT_EMPTY(b),IS_NOT_EMPTY(c))(a,b,c)

#define to(...) MACRO_DISPATCH(to,##__VA_ARGS__)
#define to3(...) MACRO_DISPATCH_OPT3(to3,__VA_ARGS__)
#define to0(           ) for (                                     ;           ;            )
#define to1(    n      ) for (i64 n= 0                             ;           ;++n         )
#define to2(    n,  l  ) for (i64 n= 0 ,to__l_##n=(l)              ;n<to__l_##n;++n         )
#define to3_111(n,f,l  ) for (i64 n=(f),to__l_##n=(l)              ;n<to__l_##n;++n         )
#define to3_110(n,f,l  ) for (i64 n=(f)                            ;           ;++n         )
#define to4(    n,f,l,u) for (i64 n=(f),to__l_##n=(l),to__u_##n=(u);n<to__l_##n;n+=to__u_##n)

exception err(const string& v) {return runtime_error(v);}
#define pr(v) cout << v << "\n"
#define pr_(v) cout << v
#define S <<" "<<
#define B <<"\t"<<
#define D(v) #v <<":"<< v
typedef long long i64;
namespace std {
template <typename T> void sort(vector<T> &v) {sort(v.begin(),v.end());}
template <typename T> void reverse(vector<T> &v) {reverse(v.begin(),v.end());}
}

//===--------------------------------------------===// common //===--------------------------------------------===//

#include "bigint.h"
InfInt ipow(InfInt a, int b) {InfInt r=1; to(_,b) r*=a; return r;}

#define SQ(v) ((v)*(v))
int gcd(int a, int b) {while (b != 0) {int t = a % b; a = b; b = t;} return a;}
bool coprime(int a, int b) {
	to(){
		if (!(a %= b)) return b == 1;
		if (!(b %= a)) return a == 1;
		}}
bool fast_square_verify(i64 x) {
	static int start[1024] = {
		1,3,1769,5,1937,1741,7,1451,479,157,9,91,945,659,1817,11,1983,707,1321,1211,1071,13,1479,405,415,1501,1609,741,15,339,1703,203,
		129,1411,873,1669,17,1715,1145,1835,351,1251,887,1573,975,19,1127,395,1855,1981,425,453,1105,653,327,21,287,93,713,1691,1935,301,551,587,
		257,1277,23,763,1903,1075,1799,1877,223,1437,1783,859,1201,621,25,779,1727,573,471,1979,815,1293,825,363,159,1315,183,27,241,941,601,971,
		385,131,919,901,273,435,647,1493,95,29,1417,805,719,1261,1177,1163,1599,835,1367,315,1361,1933,1977,747,31,1373,1079,1637,1679,1581,1753,1355,
		513,1539,1815,1531,1647,205,505,1109,33,1379,521,1627,1457,1901,1767,1547,1471,1853,1833,1349,559,1523,967,1131,97,35,1975,795,497,1875,1191,1739,
		641,1149,1385,133,529,845,1657,725,161,1309,375,37,463,1555,615,1931,1343,445,937,1083,1617,883,185,1515,225,1443,1225,869,1423,1235,39,1973,
		769,259,489,1797,1391,1485,1287,341,289,99,1271,1701,1713,915,537,1781,1215,963,41,581,303,243,1337,1899,353,1245,329,1563,753,595,1113,1589,
		897,1667,407,635,785,1971,135,43,417,1507,1929,731,207,275,1689,1397,1087,1725,855,1851,1873,397,1607,1813,481,163,567,101,1167,45,1831,1205,
		1025,1021,1303,1029,1135,1331,1017,427,545,1181,1033,933,1969,365,1255,1013,959,317,1751,187,47,1037,455,1429,609,1571,1463,1765,1009,685,679,821,
		1153,387,1897,1403,1041,691,1927,811,673,227,137,1499,49,1005,103,629,831,1091,1449,1477,1967,1677,697,1045,737,1117,1737,667,911,1325,473,437,
		1281,1795,1001,261,879,51,775,1195,801,1635,759,165,1871,1645,1049,245,703,1597,553,955,209,1779,1849,661,865,291,841,997,1265,1965,1625,53,
		1409,893,105,1925,1297,589,377,1579,929,1053,1655,1829,305,1811,1895,139,575,189,343,709,1711,1139,1095,277,993,1699,55,1435,655,1491,1319,331,
		1537,515,791,507,623,1229,1529,1963,1057,355,1545,603,1615,1171,743,523,447,1219,1239,1723,465,499,57,107,1121,989,951,229,1521,851,167,715,
		1665,1923,1687,1157,1553,1869,1415,1749,1185,1763,649,1061,561,531,409,907,319,1469,1961,59,1455,141,1209,491,1249,419,1847,1893,399,211,985,1099,
		1793,765,1513,1275,367,1587,263,1365,1313,925,247,1371,1359,109,1561,1291,191,61,1065,1605,721,781,1735,875,1377,1827,1353,539,1777,429,1959,1483,
		1921,643,617,389,1809,947,889,981,1441,483,1143,293,817,749,1383,1675,63,1347,169,827,1199,1421,583,1259,1505,861,457,1125,143,1069,807,1867,
		2047,2045,279,2043,111,307,2041,597,1569,1891,2039,1957,1103,1389,231,2037,65,1341,727,837,977,2035,569,1643,1633,547,439,1307,2033,1709,345,1845,
		1919,637,1175,379,2031,333,903,213,1697,797,1161,475,1073,2029,921,1653,193,67,1623,1595,943,1395,1721,2027,1761,1955,1335,357,113,1747,1497,1461,
		1791,771,2025,1285,145,973,249,171,1825,611,265,1189,847,1427,2023,1269,321,1475,1577,69,1233,755,1223,1685,1889,733,1865,2021,1807,1107,1447,1077,
		1663,1917,1129,1147,1775,1613,1401,555,1953,2019,631,1243,1329,787,871,885,449,1213,681,1733,687,115,71,1301,2017,675,969,411,369,467,295,693,
		1535,509,233,517,401,1843,1543,939,2015,669,1527,421,591,147,281,501,577,195,215,699,1489,525,1081,917,1951,2013,73,1253,1551,173,857,309,
		1407,899,663,1915,1519,1203,391,1323,1887,739,1673,2011,1585,493,1433,117,705,1603,1111,965,431,1165,1863,533,1823,605,823,1179,625,813,2009,75,
		1279,1789,1559,251,657,563,761,1707,1759,1949,777,347,335,1133,1511,267,833,1085,2007,1467,1745,1805,711,149,1695,803,1719,485,1295,1453,935,459,
		1151,381,1641,1413,1263,77,1913,2005,1631,541,119,1317,1841,1773,359,651,961,323,1193,197,175,1651,441,235,1567,1885,1481,1947,881,2003,217,843,
		1023,1027,745,1019,913,717,1031,1621,1503,867,1015,1115,79,1683,793,1035,1089,1731,297,1861,2001,1011,1593,619,1439,477,585,283,1039,1363,1369,1227,
		895,1661,151,645,1007,1357,121,1237,1375,1821,1911,549,1999,1043,1945,1419,1217,957,599,571,81,371,1351,1003,1311,931,311,1381,1137,723,1575,1611,
		767,253,1047,1787,1169,1997,1273,853,1247,413,1289,1883,177,403,999,1803,1345,451,1495,1093,1839,269,199,1387,1183,1757,1207,1051,783,83,423,1995,
		639,1155,1943,123,751,1459,1671,469,1119,995,393,219,1743,237,153,1909,1473,1859,1705,1339,337,909,953,1771,1055,349,1993,613,1393,557,729,1717,
		511,1533,1257,1541,1425,819,519,85,991,1693,503,1445,433,877,1305,1525,1601,829,809,325,1583,1549,1991,1941,927,1059,1097,1819,527,1197,1881,1333,
		383,125,361,891,495,179,633,299,863,285,1399,987,1487,1517,1639,1141,1729,579,87,1989,593,1907,839,1557,799,1629,201,155,1649,1837,1063,949,
		255,1283,535,773,1681,461,1785,683,735,1123,1801,677,689,1939,487,757,1857,1987,983,443,1327,1267,313,1173,671,221,695,1509,271,1619,89,565,
		127,1405,1431,1659,239,1101,1159,1067,607,1565,905,1755,1231,1299,665,373,1985,701,1879,1221,849,627,1465,789,543,1187,1591,923,1905,979,1241,181};

	static bool bad255[512] = {
		0,0,1,1,0,1,1,1,1,0,1,1,1,1,1,0,0,1,1,0,1,0,1,1,1,0,1,1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,0,1,1,1,1,0,1,1,1,
		0,1,0,1,1,0,0,1,1,1,1,1,0,1,1,1,1,0,1,1,0,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1,1,1,0,1,1,1,1,0,0,1,1,1,1,1,1,
		1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,0,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,0,1,1,
		1,1,1,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,0,1,1,1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,
		0,0,1,1,0,1,1,1,1,0,1,1,1,1,1,0,0,1,1,0,1,0,1,1,1,0,1,1,1,1,0,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,0,1,1,1,1,0,1,1,1,
		0,1,0,1,1,0,0,1,1,1,1,1,0,1,1,1,1,0,1,1,0,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1,1,1,0,1,1,1,1,0,0,1,1,1,1,1,1,
		1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,0,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,0,1,1,
		1,1,1,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,0,1,1,1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,0};

	// Quickfail
	if (x < 0 || (x&2) || ((x & 7) == 5) || ((x & 11) == 8)) return false;
	if (x == 0) return true;

	// Check mod 255 = 3 * 5 * 17, for fun
	i64 y = x;
	y = (y & 4294967295LL) + (y >> 32);
	y = (y & 65535) + (y >> 16);
	y = (y & 255) + ((y >> 8) & 255) + (y >> 16);
	if (bad255[y]) return false;

	// Divide out powers of 4 using binary search
	if ((x & 4294967295LL) == 0) x >>= 32;
	if ((x & 65535) == 0) x >>= 16;
	if ((x & 255) == 0) x >>= 8;
	if ((x & 15) == 0) x >>= 4;
	if ((x & 3) == 0) x >>= 2;

	if ((x & 7) != 1) return false;

	// Compute sqrt using something like Hensel's lemma
	i64 r, t, z;
	r = start[(x >> 3) & 1023];
	do {
		z = x - r * r;
		if (z == 0) return true;
		if (z < 0) return false;
		t = z & (-z);
		r += (z & t) >> 1;
		if (r > (t >> 1)) r = t - r;
	} while (t <= (1LL << 33));

	return false;}
#define sqv fast_square_verify
i64 ipow(i64 a,i64 b) {i64 r=1; to(_,b) r*=a; return r;}
//i64 ipow(i64 a,i64 b) {return (i64)(pow(a,b)+0.5);}
int ipow(int a,int b) {return (int)ipow((i64)a,(i64)b);}
void ceilE(int &l, int base) {l = (l-1)/base*base+base;}
template<int l> string bin(int v) {bitset<l> t(v); string r = t.to_string(); to(i,l) if (r[i] == '0') r[i] = '.'; return r;}
template<typename T> T    bit      (T &l, int i) {return (l >> i) & T(1);}
template<typename T> void set_bit  (T &l, int i) {l |=   T(1) << i ;}
template<typename T> void clear_bit(T &l, int i) {l &= ~(T(1) << i);}

vector<i64> is_primes;
vector<int> primes;
vector<int> primes_inv;
void sieve(); int prime(int n);
bool is_prime(int n) {while (!(n/64 < is_primes.size())) sieve(); return (is_primes[n/64] >> (n%64)) & 1;}
int prime_inv(int n) {
	while (!(n < primes_inv.size())) {to(i,primes_inv.size(),primes_inv.size()*2) {primes_inv.push_back(primes_inv.at(i-1)+(is_prime(i)?1:0));}}
	return primes_inv[n];}
bool is_prime_mod(i64 n) {if (n<1000000000) return is_prime(n); to(i,1,prime_inv((int)sqrt(n))) if (n%prime(i)==0) return false; return true;}
bool is_prime_slow(i64 n) {
	if(n<4) return n>=2; if (n%2==0||n%3==0) return false;
	i64 lim = (i64)sqrt(n); for (i64 i=5;i<=lim;i+=6) if (n%i==0 || n%(i+2)==0) return false; return true;}
int prime(int n) {if (!(n < primes.size())) sieve(); return primes[n-1];}
void sieve(int lim) {
	ceilE(lim,64);
	int prev_lim = is_primes.size()*64;
	is_primes.resize(lim/64,-1LL);
	clear_bit(is_primes[0],0);
	clear_bit(is_primes[0],1);
	for (int i=1;i<=(int)sqrt(lim);i++)
		if (is_prime(i))
			to(j,i*i,lim,i)
				clear_bit(is_primes[j/64],j%64);
	to(i,prev_lim+1,lim)
		if (is_prime(i))
			primes.push_back(i);
	}
void sieve() {sieve(max((int)is_primes.size()*2,16)*64);}
vector<i64> prime_factors(i64 n) { // list of prime factors
	vector<i64> r;
	to(i,1,) {
		i64 p = prime(i);
		to(){
			if (n < p*p) {if (n != 1) r.push_back(n); return r;}
			else if (n%p == 0) {r.push_back(p); n/=p;}
			else break;}
	}}
vector<int> prime_factors_group(i64 n) { // {(index by prime's index in primes[i]): count}
	vector<int> r;
	to(i,1,) {
		r.push_back(0);
		i64 p = prime(i);
		to(){
			if (n == 1) return r;
			//if (n < p*p) {if (n != 1) {while (r.size() < n) r.push_back(0); r.push_back(1);} return r;}
			else if (n%p == 0) {r[r.size()-1]++; n/=p;}
			else break;}
	}}
vector<int> factors(int n) {vector<int> r; to(i,1,n+1) if (n%i==0) r.push_back(i); return r;}
int count_factors(i64 n) {
	vector<i64> npf = prime_factors(n);
	int r=1,t=0; to(i,1,npf.size()) {t++; if (npf[i-1] != npf[i]) {r *= t+1; t = 0;}} r *= t+1+1;
	return r;}
i64 sum_factors(int n) {
	//sumDivisors = product . map (\(a,b) -> sum $ map ((^) a) [0..b]) . map (\xs -> (head xs, len xs)) . group . primeFactors
	if (n <= 1) return 0;
	i64 r=0,sq=(int)sqrt(n); if (sq*sq==n) r-=sq;
	to(y,1,(int)sqrt(n)+1) if (n%y==0) {r += y; r += n/y;}
	return r;}
int euler_totient(int n) {
	//eulerTotient = product . map (\(a,b) -> a^(b-1) * (a-1)) . map (\xs -> (head xs, len xs)) . group . primeFactors
	//vector<int> f = prime_factors_group(n);
	//int r=1; to(i,f.size()) r *= prime(i)
	if (n<2) return 0;
	else if (is_prime(n)) return n-1;
	else if (n%2==0) {int m=n/2; return euler_totient(m) * (m%2==0? 2 : 1);}
	else to(i,1,) {int p = prime(i); if (!(p<=n)) pr("OH DEAR");
		if (n%p==0) {
			int d = gcd(p,n/p);
			int r = euler_totient(p)*euler_totient(n/p);
			return d==1? r : r*d/euler_totient(d);
		}
	}}

//int fast_pow(int v, int e, int m) { // return v**e % m;
	//int r = 1;
	//while (e > 0) {
	//	if (e % 2 == 1)
	//		r = (r*v) % m;
	//	e >>= 1;
	//	v = (v*v) % m;
	//}
	//return r;}
// uses binomial expansion to cheaply calculate pow(a-1,n,m) and pow(a+1,n,m) where m == a**2
i64 mod_pow_sq_p1(i64 a, i64 n, i64 m) {return (a*n + 1) % m;}
i64 mod_pow_sq_m1(i64 a, i64 n, i64 m) {return ((n%2==0? (-a*n + 1) : (a*n - 1)) + m) % m;}

//===--------------------------------------------===// solutions //===--------------------------------------------===//

/*===---  14 ---===*/ namespace collatz {/*
	i64 next(i64 v) {return v % 2 == 0? v/2 : 3*v+1;}
	i64 solve(i64 len) {
		i64 max=0,maxv;
		for (i64 i=1;i<len;i++) {
			i64 v=i,t=1; while (v != 1) {t++; v = next(v);}
			if (t > max) {max = t; maxv = i;}
			//if (i % (len/20) == 0) pr(i S t);
		}
		return maxv;}
	i64 solve() {return solve(1000000);}
	*/}
/*===---  44 ---===*/ namespace third_diagonal {/*
	int P(int n) {return n*(3*n-1)/2;}
	bool is_p(int n) {int t = 1 + 24*n; return sqv(t) && (int)(sqrt(t)+0.5) % 6 == 5;}
	i64 solve() {
		for (int l=1;;l++) {
			for (int z=1;z<=l;z++) {int L=l-z+1;
				for (int i=1;i<=L;i++) {
					int d=z,j=i,k=L-i+1;
					if (P(k)-P(j)==P(d) && is_p(P(k)+P(j)))
						pr("yay" S d S j S k S D(P(d)));
				}
			}
		}
		return 0;}
	*/}
/*===---  62 ---===*/ namespace cubic_permutations {/*
	bool cmp_0_last(int a,int b) {return a == 0? false : b == 0? true : a<b;}
	i64 cube(i64 v) {return v*v*v;}
	double cbrt(i64 v) {return pow((double)v,1.0/3.0);}
	i64 sort_digits(i64 v) {
		vector<int> t;
		if (v == 0) t.push_back(0); else {while (v != 0) {t.push_back(v%10); v = v/10;}}
		sort(t.begin(),t.end(),cmp_0_last);
		reverse(t);
		i64 r=0; while (!t.empty()) {r = r*10 + t.back(); t.pop_back();} return r;}
	i64 solve(int perm_cnt,int limit) {
		for (int l=1;l<=limit;l++) {
			i64 start = (i64)ceil (cbrt(ipow(10,l-1)));
			i64 end   = (i64)floor(cbrt(ipow(10,l  )));
			map<i64,i64> hist; // {perm: cnt}
			for (i64 cbrt=start;cbrt<=end;cbrt++)
				hist[sort_digits(cube(cbrt))]++;
			for(map<i64,i64>::iterator i = hist.begin(); i != hist.end(); i++) {
				if (i->second >= perm_cnt) {
					pr(i->second S i->first);
					for (i64 cbrt=start;cbrt<=end;cbrt++)
						if (sort_digits(cube(cbrt)) == i->first)
							pr(cube(cbrt));
				}
			}
		}
		return 0;}
	i64 solve() {return solve(5,12);}
	*/}
/*===---  73 ---===*/ namespace ranging_fractions {/*
	int solve(int D) {
		int r = 0;
		for (int d=2;d<=D;d++)
			for (int n=d/3+1;n<d/2+(d%2==0?0:1);n++)
				if (coprime(n,d))
					r++;
		return r;}
	int solve() {return solve(12000);}
	*/}
/*===---  75 ---===*/ namespace pythagorean_rapture {/*
	int solve(i64 lim) {
		int r=0;
		for (i64 l=2;l<=lim;l+=2) {
			// b = (L-A - AA/(L-A))/2
			#define SOLVE_BC(test) {i64 x=l-a, y=a*a, z=y/x; if (z*x==y) {test {if (cnt == 1) goto main; cnt=1;}}}
			i64 start = (i64)(l/(2+sqrt(2)))+1;
			i64 se = start + (start%2==0? 0 : 1);
			i64 so = start + (start%2==0? 1 : 0);
			int cnt=0;
			for (i64 a=se;a<l/2;a+=2) SOLVE_BC(i64 w=x-z; i64 b=w/2; if (b*2==w))
			for (i64 a=so;a<l/2;a+=2) SOLVE_BC(;)
			if (cnt == 1) {
				r++;
				if (r%10==0) pr("mrr~" S r S l);
			}
		main:;}
		return r;}
	int solve() {return solve(1500000);}
	*/}
/*===---  78 ---===*/ namespace coin_partitions {/*
	i64 solve(int factor, int buf_size) {
		int memo[buf_size];
		int init_buf[buf_size];

		for (int i=0;i<buf_size;i++) init_buf[i] = 1;
		for (int lim=2;lim<buf_size;lim++) {
			for (int n=lim;n<buf_size;n++)
				{init_buf[n] += init_buf[n-lim]; init_buf[n] %= factor;}
			memo[lim] = init_buf[lim];
			if (lim % (buf_size/20) == 0) pr("|" S lim S memo[lim]);
			if (memo[lim] == 0) return lim;
		}

		return -1;}
	i64 solve() {return solve(1000000,100000);}
	*/}
/*===---  94 ---===*/ namespace pythagorean_isosceles {/*
	i64 solve(int lim) {
		i64 r=0;
		for (i64 i=3;i<=lim/3;i+=2) {
			i64 m = i*i - (i-1)*(i-1)/4;
			i64 p = i*i - (i+1)*(i+1)/4;
			if (sqv(m)) {r += i*3-1; pr("woo-" S i S r);}
			if (sqv(p)) {r += i*3+1; pr("woo+" S i S r);}
		}
		return r;}
	i64 solve() {return solve(1000000000);}
	*/}
/*===---  95 ---===*/ namespace friendly_bdsm {/*
	int memo_nxt[1000000];
	int memo_cl[1000000]={};
	int cl(int n) {
		if (memo_cl[n]) return memo_cl[n];
		#define NXT(v) v = memo_nxt[v]; if (v > 1000000) goto no;
		int a=n,b=n; to(){NXT(a); NXT(b); NXT(b); if (a == n) goto yes; if (a == b) goto no;}
		yes: {int r=0; do {NXT(b); r++;} while (b != n); return (memo_cl[n] = r);}
		no: return (memo_cl[n] = -1);}
	int solve() {
		to(i,1000000) memo_nxt[i] = (int)min(sum_factors(i)-i,1000001LL);
		pr("built memo_nxt");
		int k=0,v=0; to(i,1000000) {if (cl(i) > k) {k = cl(i); v = i;} if (cl(i) != -1) pr("fna" S i S cl(i) S k S v);}
		return v;}
	*/}
/*===--- 100 ---===*/ namespace consecutive_factorization {/*
	bool mul_eq(i64 a0, i64 a1, i64 b0, i64 b1) { // return a0*a1 == b0*b1;
		#define M32 0xffffffff
		#define DE_(v) i64 v##0 = v & M32; i64 v##1 = (v>>32) & M32;
		#define DE(v) DE_(v##0) DE_(v##1)
		#define DE2(v) DE(v##0) DE(v##1)
		#define MUL(p) i64 p##m00 = p##00*p##10; i64 p##m01 = p##00*p##11; i64 p##m10 = p##01*p##10; i64 p##m11 = p##01*p##11;
		DE(a) DE(b)
		MUL(a) MUL(b)
		DE2(am) DE2(bm)
		return (am000 == bm000 && am001+am010+am100 == bm001+bm010+bm100 && am011+am101+am110 == bm011+bm101+bm110 && am111==bm111);}
	bool match(i64 n) {double t = (int)(n/sqrt(2)+0.5); return mul_eq(t*2,t-1,n,n-1);}
	i64 solve(i64 start) {
		for (i64 i=start;;i++)
			if (match(i))
				pr("max" S i);
		return 0;}
	i64 solve() {return 0;}
	// finished solving in python
	// from math import *
	// v = 4
	// while v < 1e12: v = v * (sqrt(2)*2+3) - 2
	// print 'answer:',v
	*/}
/*===--- 110 ---===*/ namespace bielephantine {/*
	int count_factors_special(i64 n) {
		vector<int> npf = prime_factors_group(n);
		if (npf.size() < 10) npf.resize(10,0); static int extra[] = {2,2,1,1,1,1,1,1,1,1}; // (2^2 3^2 5 7 11 13 17 19 23 29)^2
		int r=1; for (int i=0;i<npf.size();i++) r *= npf[i]+(i<10?extra[i]*2:0)+1; return r;}
	int solve() {
		sieve(1000000);
		int top=0;
		for (i64 n=1;;n++) if (!is_prime(n)) {int t = (count_factors_special(n*n)+1)/2; if (t > top) {pr("new top!" S n B t); top = t;}}
		return 0;}
	*/}
/*===--- 119 ---===*/ namespace digit_powah {/*
	InfInt sum_digits(InfInt v) {InfInt r=0; while (v > 0) {r += v%10; v /= 10;} return r;}
	i64 sum_digits(i64 v) {i64 r=0; while (v > 0) {r += v%10; v /= 10;} return r;}
	bool is_powah(i64 v) {i64 s = sum_digits(v), t=1; if (s == 1) return false; while (t < v) {t *= s; if (t == v) return true;} return false;}
	int solve(int n) {
		for (i64 i=1;;i++) {
			for (i64 j=0;j<i;j++) {
				for (i64 k=0;k<10;k++) {
					InfInt a = j*10+k + 2, b = i-j-1 + 2;
					InfInt p = ipow(a,b);
					InfInt s = sum_digits(p);
					if (s == a) pr("solution" S a S b S p);
				}
			}
		}
	}
	int solve() {return solve(30);}
	*/}
/*===--- 120 ---===*/ namespace modular_exponentiation {/*
	int rmax(int a) {i64 r = 0; for (int n=0;n<a*a*2;n++) r = max(r,mod_pow_special(a,n)); return (int)r;}
	int solve(int from, int to) {int r = 0; for (int i=from;i<=to;i++) {r += rmax(i); pr(i);} return r;}
	int solve() {return solve(3,1000);}
	*/}
/*=-- 129 & 130 --=*/ namespace serial_punist {/*
	int A(int n) {int r=1; for (int k=1;;k++) {if (r%n==0) return k; r=(r*10+1)%n;}}
	int solve_129(int lim) {
		for (int n=1;;n++) {
			if (coprime(n,10) && A(n) > lim) return n;
			if (n%100==0) pr("bluh" S n);
		}
	}
	int solve_129() {return solve_129(1000000);}
	int solve_130() {int r=0, cnt=0; for (int i=2;;i++) if (!is_prime(i) && gcd(i,10)==1) if ((i-1)%A(i)==0) {pr("~" S i); r += i; cnt++; if (cnt >= 25) return r;}}
	*/}
/*===--- 131 ---===*/ namespace defiant_fucking {/*
	i64 winN=1;
	bool fucks(i64 p) {
		for (i64 N=winN;N<=winN+30;N++) {
			i64 af = N*N;
			i64 n = N*N*N;
			for (i64 a=n+af;a<n+p;a+=af) {i64 t = a*a+a*n+n*n; if (t%p==0 && t/p*(a-n) == n*n) {winN = N; return true;}}
		}
		return false;}
	int solve() {
		int r=0;
		for (int i=1;;i++) {int p = prime(i); if (p >= 1000000) break; if (fucks(p)) {r++; pr("!" S r S i S p);}}
		return r;}
	*/}
/*===--- 132 ---===*/ namespace p132 {/*
	bool R(int k,int mod) {
		vector<int> cycle; int r=1;
		to(i){cycle.push_back(r); r *= 10; r++; r %= mod; if (cycle[0] == r) return cycle[(k-1)%(i+1)] == 0;}
		return r==0;}
	i64 solve(int v) {
		int cnt=0,r=0;
		to(i,1,1000000000) if (R(1000000000,prime(i))) {pr("WIN" S cnt+1 S i S prime(i)); r += prime(i); cnt++; if (cnt == 40) break;}
		return r;}
	*/}
/*=-- 135 & 136 --=*/ namespace psh {/*
	int solve(int c_lim, int lim) {
		int r=0;
		to(n,1,lim) {
			int cnt=0;
			if (sqv(n) && n%4==0) cnt--;
			to(y,1,(int)sqrt(n)+1) if (n%y==0) {
				{int t = y*3 - n/y; if (t>0 && t%4==0) {cnt++; if (cnt > c_lim) break;}}
				{int t = n/y*3 - y; if (t>0 && t%4==0) {cnt++; if (cnt > c_lim) break;}}
			}
			if (cnt == c_lim) {r++; if (r%1000==0) pr("yay" S n S r);}
		}
		return r;}
	int solve135(int _) {return solve(10,1000000);}
	int solve136(int _) {return solve(1,50000000);}
	*/}
/*===--- 142 ---===*/ namespace square_orgy {/*
	int solve() {
		for (int x=3;;x++)
		for (int Y=1;;Y++) {
			int y = x - Y*Y;
			if (!(y>1)) break;
			if (sqv(x+y))
			for (int Z=1;;Z++) {
				int z = y - Z*Z;
				if (!(z>0)) break;
				if (sqv(x+z) && sqv(x-z) && sqv(y+z)) pr("SOLUTION" S (x+y+z) S "|" S x S y S z);
			}
		}
	}
	*/}
/*=-- 173 & 174 --=*/ namespace laminae {/*
	int l(int n) {
		int r=0;
		for (int w=1;w<=(int)(sqrt(n)/2-0.00000001);w++)
			if (n/(4*w)*(4*w) == n)
				r++;
		return r;}
	int solve_173() {
		int r=0;
		for (int i=1;i<=1000000;i++) {
			r += l(i);
			if (i%10000==0) pr(i S r);
		}
		return r;}
	int solve_174() {
		int r=0;
		for (int i=1;i<=1000000;i++) {
			int n = l(i);
			if (1 <= n && n <= 10) r++;
			if (i%10000==0) pr(i S r);
		}
		return r;}
	*/}
/*===--- 193 ---===*/ namespace libertarian_geometry {/* unfinished
	i64 count_sf(i64 a, i64 b) {
		i64 r=0;
		to(i,a,b) to(p,1,) {i64 t = prime(p)*prime(p); if (i%t==0) break; if (t > i) {r++; break;}}
		return r;}
	i64 solve() {
		i64 r=0;
		to(i) to(p,1,) {i64 t = prime(p)*prime(p); if (i%t==0) break; if (t > i) {r++; if ((int)(1.644934066848226436472415166646*r+0.5) == i) pr("hey!" S i S r); break;}}
		//to(i,1073741824) {i64 t=count_sf(1048576*i,1048576*(i+1)); r+=t; pr(i B t B t-637458 B r);}
		return r;}
	*/}
/*===--- 196 ---===*/ namespace incest {/*
	i64 tri(i64 v) {return v*(v+1)/2;}
	i64 s(i64 row) {
		i64 start=tri(row-1)+1;
		i64 st[5]; i64 end[5];
		for (i64 r=-2;r<=2;r++) {st[r+2] = tri(row+r-1)+1; end[r+2] = tri(row+r);}
		
		i64 result=0;
		for (i64 col=0;col<row-1;col++) {
			#define WIN {result+=i; goto end;}
			i64 i = col+start;
			if (col%1000==0) pr("~" S row S col S i S result);
			if (is_prime_mod(i)) {
				bool near[5][5];
				#define near_ near[r+2][c+2]
				#define nearE near_ = (0 <= c+col && c+col < end[r+2]-st[r+2])? is_prime_mod(st[r+2]+c+col) : 0
				int cnt=0;
				for (i64 r=-1;r<=1;r++)
					for (i64 c=-1;c<=1;c++)
						if (!(r==0&&c==0))
							{nearE; if (near_) {cnt++; if (cnt == 2) WIN}}
				if (cnt == 1)
					for (i64 r1=-1;r1<=1;r1++)
						for (i64 c1=-1;c1<=1;c1++)
							if (near[r1+2][c1+2])
								for (i64 r2=-1;r2<=1;r2++)
									for (i64 c2=-1;c2<=1;c2++)
										if (!(r2==0&&c2==0) && (abs(r1+r2)==2 || abs(c1+c2)==2))
											{i64 r=r1+r2,c=c1+c2; nearE; if (near_) WIN}
			}
		end:;}
		return result;}
	i64 solve(int v) {return v == 0? s(5678027) + s(7208785) : s(v);}
	*/}
/*===--- 214 ---===*/ namespace fifty_shades_of_totient {/*
	// misses 357800039 ??
	bool yes(int n) {for (int i=0;i<25-1;i++) {n = euler_totient(n); if (n == 0) return false;} return n == 1;}
	i64 solve() {
		i64 r=0;
		for (int i=1;;i++) {int p = prime(i); if (!(p<40000000)) break; if (yes(p)) {r+=p; if (i%100==0) pr("hi!" S i S p S r);}}
		return r;}
	*/}
/*===--- 243 ---===*/ namespace gcd_search {/*
	double q(int v) {
		int r = 0;
		for (int i=1;i<v;i++) if (coprime(i,v)) r++;
		return r / (double)(v-1);}
	int solve(double target) {
		double low = 1;
		for (int i=1;;i++) {
			double t = q(i*2*3*5*7*11*13*17*19*23);
			if (t < low) {
				pr("new low!" S i S setprecision(15) S t);
				low = t;
				if (t < target) return i;
			}
		}
	}
	int solve() {return solve(15499.0/94744.0);} // 0.16358819555855779785527315713924
	*/}
/*===--- 324 ---===*/ namespace evil_dildo {/*
	int tr[512][512]={}; // transition table {#{state,state}: ways}
	int tr_n[512][512]; // tr taken to the nth power
	// special, mods out by 100000007 and prints shit
	void matrix_multiplyE(int (*l)[512][512], int (*v)[512][512]) {
		static int r[512][512]={};
		to(i,512) to(j,512) {i64 t=0; to(k,512) t += (i64)(*l)[i][k] * (*v)[k][j]; r[i][j] = (int)(t % 100000007);}
		to(i,512) to(j,512) (*l)[i][j] = r[i][j];
		}
	void matrix_exponential(int (*v)[512][512], InfInt pow, int (*r)[512][512]) {
		to(i,512) to(j,512) (*r)[i][j] = 0; to(i,512) (*r)[i][i] = 1;
		static int t[512][512]; to(i,512) to(j,512) t[i][j] = (*v)[i][j];
		int i=0,ps=0,ps0=pow.size()/4;
		to(){
			if (pow.size() != ps) {i=1; ps=pow.size();}
			pr("^" S ps0-pow.size()/4+1 << "/" << ps0 S i++ <<"/30");
			if (pow%2!=0) matrix_multiplyE(r,&t);
			pow/=2;
			if (pow == 0) break;
			matrix_multiplyE(&t,&t);
		}
		}
	void build_tr_y(int state, int up, int st1) {
		to(i,8) to(y,8) if (!(~i & y)) {
			int st2=0; to(x,3) if (bit(i,x)) {int t=x+bit(y,x)*3; set_bit(st2,t); set_bit(st2,t+3);}
			if (!((st1 & st2) || ((st1|st2) != 0x1ff))) tr[state][up]++;
			}
		}
	void build_tr_x(int state, int up, int st1) {
		to(i,8) to(x,8) if (!(~i & x)) {
			int st2=0; to(y,3) if (bit(i,y)) {int t=bit(x,y)+y*3; set_bit(st2,t); set_bit(st2,t+1);}
			if (!(st1 & st2)) build_tr_y(state,up,st1|st2);
			}
		}
	void build_tr_z(int st) {to(up,512) if (!(st & up)) build_tr_x(st,up,st|up);}
	int f(InfInt n) {matrix_exponential(&tr,n,&tr_n); return tr_n[0][0];}
	i64 solve(int e) {to(state,512) build_tr_z(state); return f(ipow(InfInt(10),e));}
	*/}
/*===-----------===*/ namespace scratch_space {
	i64 solve(int v) {
		int r=0;
		return r;}
	}

//===--------------------------------------------===// main //===--------------------------------------------===//

void main2(int v) {pr(libertarian_geometry::solve());}
int main(int argc, char* argv[]) {
	primes_inv.push_back(0);
	pr("start");
	main2(argc<2?0:atoi(argv[1]));
	return 0;}
