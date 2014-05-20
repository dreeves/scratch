package rugged;

import java.util.Random;

//SimpleRNG:
	// SimpleRNG is a simple random number generator based on George Marsaglia's MWC (multiply with carry) generator.
	// Although it is very simple, it passes Marsaglia's DIEHARD series of random number generator tests.
	// Written by John D. Cook http://www.johndcook.com
//Acquired from http://www.codeproject.com/KB/recipes/SimpleRNG.aspx, this
//is an adaptation of SimpleRNG from C# to Java with some minor changes.

//log:
//aug 15 2010: created
//oct 11 2010: added static section

//kryo-happy

public class Rand
{
	//These values are not magical, just the default values Marsaglia used. Any pair of unsigned integers should be fine.
	public int m_w = 0x1f123bb5; //private
	public int m_z = 0x159a55e5; //private

    private static volatile long seedUniquifier = 8582522807148012L;
	public Rand() {long time = ++seedUniquifier + System.nanoTime(); m_w = (int)(time >> 16); m_z = (int)time;}
	public Rand(int _m_w) {m_w = _m_w;}
	public Rand(int _m_w, int _m_z) {m_w = _m_w; m_z = _m_z;}
	public Rand(long l) {m_w = (int)(l >>> 32); m_z = (int)(l & 0xffffffff);}
	
	public long getSeed() {return (((long)m_w) << 32) | ((long)m_z);}
	
	//the heart of the generator, this uses George Marsaglia's MWC algorithm to produce an unsigned integer.
	//see http://www.bobwheeler.com/statistics/Password/MarsagliaPost.txt
	public int nextInt() {
		m_z = 36969 * (m_z & 0xffff) + (m_z >>> 16);
		m_w = 18000 * (m_w & 0xffff) + (m_w >>> 16);
		return (m_z << 16) + (m_w & 0xffff);
	}

	/**uniform random sample from the interval [0,1)*/
	public double nextDouble() {return nextInt() * 0x1.0p-32 + 0.5;}
	
	public long nextLong() {return ((long)(nextInt()) << 32) + nextInt();}
	
	public boolean nextBool() {return (nextInt() & 0x800) > 0;}
	
	public int nextInt(int n) {
        if (n <= 0) return -1;
        if ((n & -n) == n) // i.e., n is a power of 2
            return (int)((n * (long)(nextInt() & 0x7fffffff)) >> 31);
        int bits, val;
        do {bits = nextInt() & 0x7fffffff; val = bits % n;}
        while (bits - val + (n-1) < 0);
        return val;
    }
	
	public double rand() {return nextDouble();}
	public double rand(double d) {return nextDouble() * d;}
	public int rand(int i) {return nextInt(i);}
	/**a and b are inclusive*/ public int rand(int a, int b) {return nextInt(b - a + 1) + a;}
	/**a and b are inclusive*/ public double rand(double a, double b) {return nextDouble() * (b - a) + a;}
//}
//============================================================// static //============================================================//
//{
	public static final Random randJU = new Random();
	public static final Rand rand = new Rand();

	public static boolean roll(int perc) {return rand.nextInt(100) < perc;}
	public static boolean roll(double perc) {return randJU.nextFloat() * 100 < perc;}
	public static boolean bool() {return rand.nextBool();}
//}
//============================================================// distributions //============================================================//
//{
	/**uniform random sample from the interval (0,1). probably inefficiently calculated.*/
	private double nextOpenDouble() {double ret = nextDouble(); if (ret == 0) ret = nextOpenDouble(); return ret;}
	/**Gaussian random sample with mean 0.0 and standard deviation 1.0*/
	public double nextGaussian()//uses Box-Muller algorithm      it's r * Math.sin(theta) with r and theta inlined
		{return Math.sqrt(-2.0 * Math.log(nextOpenDouble())) * Math.sin(2.0 * Math.PI * nextDouble());}
	public double nextGaussian(double mean, double stdDev) {return mean + stdDev * nextGaussian();}
	/**Exponential random sample with mean 1.0*/
	public double nextExponential() {return -Math.log(nextOpenDouble());}
	public double nextExponential(double mean) {return mean * nextExponential();}
	public double nextGamma(double shape, double scale) {
		//Implementation based on "A Simple Method for Generating Gamma Variables" by George Marsaglia and Wai Wan Tsang.
		//ACM Transactions on Mathematical Software Vol 26, No 3, September 2000, pages 363-372.
		if (shape >= 1) {
			double d = shape - 1.0 / 3.0;
			double c = 1 / Math.sqrt(9 * d);
			while (true) {
				double x, xsquared, v, u;
				do
					{x = nextGaussian(); v = 1 + c * x;}
				while (v <= 0);
				v = v * v * v;
				u = nextOpenDouble();
				xsquared = x*x;
				if (u < 1.0 - 0.0331 * xsquared * xsquared || Math.log(u) < 0.5 * xsquared + d * (1.0 - v + Math.log(v)))
					return scale * d * v;
			}
		}
		else if (shape <= 0)
			return Double.NaN;
		else
			return scale * nextGamma(shape + 1, 1) * Math.pow(nextDouble(), 1 / shape);
	}
	//a chi squared distribution with n degrees of freedom is a gamma distribution with shape n/2 and scale 2.
	public double nextChiSquare(double degreesOfFreedom) {return nextGamma(0.5 * degreesOfFreedom, 2.0);}
	//if X is gamma(shape, scale) then 1/Y is inverse gamma(shape, 1/scale)
	public double nextInverseGamma(double shape, double scale) {return 1.0 / nextGamma(shape, 1.0 / scale);}
	public double nextWeibull(double shape, double scale) {return scale * Math.pow(-Math.log(nextOpenDouble()), 1.0 / shape);}
	//apply inverse of the Cauchy distribution function to a uniform
	public double nextCauchy(double median, double scale) {return median + scale * Math.tan(Math.PI * (nextDouble() - 0.5));}
	//see Seminumerical Algorithms by Knuth
	public double nextStudentT(double degreesOfFreedom) {return nextGaussian() / Math.sqrt(nextChiSquare(degreesOfFreedom) / degreesOfFreedom);}
	/**the Laplace distribution is also known as the double exponential distribution*/
	public double nextLaplace(double mean, double scale) {double u = nextOpenDouble(); return u < 0.5? mean + scale * Math.log(2 * u) : mean - scale * Math.log(2 - 2*u);}
	public double nextLogNormal(double mu, double sigma) {return Math.exp(nextGaussian(mu, sigma));}
	//There are more efficient methods for generating beta samples. However, such methods are a little more efficient and a lot more complicated.
	//For an explanation of why the following method works, see http://www.johndcook.com/distribution_chart.html#gamma_beta
	public double nextBeta(double a, double b) {double u = nextGamma(a, 1.0); return u / (u + nextGamma(b, 1.0));}
}

    /*synchronized public double nextGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if (haveNextNextGaussian) {
    	    haveNextNextGaussian = false;
    	    return nextNextGaussian;
    	} else {
            double v1, v2, s;
    	    do {
                v1 = 2 * nextDouble() - 1; // between -1 and 1
            	v2 = 2 * nextDouble() - 1; // between -1 and 1
                s = v1 * v1 + v2 * v2;
    	    } while (s >= 1 || s == 0);
    	    double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
    	    nextNextGaussian = v2 * multiplier;
    	    haveNextNextGaussian = true;
    	    return v1 * multiplier;
        }
    }*/