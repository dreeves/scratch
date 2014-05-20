import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.BigInteger;

public class A
{
	public static void main(String[] args) {
		List<List<Integer>> l = primeFactorize(73682);
		for (int i = 0; i < l.get(0).size(); i++)
			pln(l.get(0).get(i) + "^" + l.get(1).get(i));
	}
	
	static String reverse(String s) {
		if (s.length() == 1)
			return s;
		return s.substring(1) + s.charAt(0);
	}
	
	static long factorial(int num) {
		long ret = 1;
		for (int i = 1; i <= num; i++)
			ret *= i;
		return ret;
	}
	
	static long summarize(List<List<Integer>> primeFactors) {
		if (primeFactors.size() != 2 || primeFactors.get(0).size() != primeFactors.get(1).size())
			return -1;
		long prod = 1;
		for (int i = 0; i < primeFactors.get(0).size(); i++) {
			long sum = 0;
			for (int j = 0; j <= primeFactors.get(1).get(i); j++)
				sum += intpow(primeFactors.get(0).get(i), j);
			prod *= sum;
		}
		return prod;
	}

	static List<List<Integer>> primeFactorize(long num) {
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		ret.add(new ArrayList<Integer>());
		ret.add(new ArrayList<Integer>());
		for (int i = 2; i <= num; i++)
		if (num % i == 0 && isPrime(i)) {
			num /= i;
			if (ret.get(0).size() == 0 || i != ret.get(0).get(ret.get(0).size() - 1))
				{ret.get(0).add(i); ret.get(1).add(0);}
			ret.get(1).set(ret.get(0).size() - 1, ret.get(1).get(ret.get(0).size() - 1) + 1);
			i--;
		}
		return ret;
	}
	
	static boolean isPrime(int num) {
		if (num < 2) return false;
		for (int i = 2; i <= Math.sqrt(num); i++)
		if (num % i == 0)
			return false;
		return true;
	}
	
	static boolean isPalindrome(String s) {
		for (int i = 0; i < (s.length() + 1) / 2; i++)
		if (s.charAt(i) != s.charAt(s.length() - i - 1))
			return false;
		return true;
	}
	
	static int intpow(int base, int exponent) {
		if (exponent == 2) return base * base;
		if (exponent == 3) return base * base * base;
		if (exponent > 3) {
			int b = base;
			for (int i = 1; i < exponent; i++)
				base *= b;
			return base;
		}
		if (exponent == 1) return base;
		if (exponent == 0) return 1;
		return 0;
	}
	
	static int valueize(String s) {
		char[] arr = s.toCharArray();
		int ret = 0;
		for (int i = 0; i < arr.length; i++)
			ret += arr[i] - 64;
		return ret;
	}

	static void pln(Object o) {System.out.println(o.toString());}
}

/*class Prime
{
	private static List<Integer> primes = new ArrayList<Integer>();
	static {primes.add(2); primes.add(3); primes.add(5);}
	
	public static int getPrime(int nthPrime)
	{
		if (primes.size() <= nthPrime)
		{
			
		}
		return primes.get(nthPrime);
	}
}*/