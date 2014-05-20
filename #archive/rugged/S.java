package rugged;

//version: Oct 14 2010

/**Provides a variety of succinctly named functions for general use.*/
public /*static*/ class S
{
	public static void sleep(long millis) {try {Thread.sleep(millis);} catch (InterruptedException ignore) {}}
	//public static void claim(boolean claim) {if (!claim) throw new AssertionError();}

	public static String clamp(String s, int maxLen) {return s.length() <= maxLen? s : s.substring(0, maxLen);}
//}
//============================================================// maths //============================================================//
//{
	public static int wrap(int low, int val, int high) {int rng = high - low + 1; return val % rng + low + (val < 0? rng : 0);}
	public static int clamp(int low, int val, int high) {return val < low? low : (val > high? high : val);}
	public static double clamp(double low, double val, double high) {return val < low? low : (val > high? high : val);}
	public static int floor(double d) {return d < 0? (int)d - 1 : (int)d;}
	public static int ceil(double d) {return d < 0? (int)d : (int)d + 1;}
	
	public static    int abs(   int v) {return v < 0? -v : v;}
	public static   long abs(  long v) {return v < 0? -v : v;}
	public static  float abs( float v) {return v < 0? -v : v;}
	public static double abs(double v) {return v < 0? -v : v;}
	
	// Halley's comment

	public static    int min(   int a,    int b) {return a < b? a : b;}
	public static   long min(  long a,   long b) {return a < b? a : b;}
	public static  float min( float a,  float b) {return a < b? a : b;}
	public static double min(double a, double b) {return a < b? a : b;}
	public static    int max(   int a,    int b) {return a > b? a : b;}
	public static   long max(  long a,   long b) {return a > b? a : b;}
	public static  float max( float a,  float b) {return a > b? a : b;}
	public static double max(double a, double b) {return a > b? a : b;}

	public static    int min(   int a,    int b,    int c) {return a < b? min(a, c) : min(b, c);}
	public static   long min(  long a,   long b,   long c) {return a < b? min(a, c) : min(b, c);}
	public static  float min( float a,  float b,  float c) {return a < b? min(a, c) : min(b, c);}
	public static double min(double a, double b, double c) {return a < b? min(a, c) : min(b, c);}
	public static    int max(   int a,    int b,    int c) {return a > b? max(a, c) : max(b, c);}
	public static   long max(  long a,   long b,   long c) {return a > b? max(a, c) : max(b, c);}
	public static  float max( float a,  float b,  float c) {return a > b? max(a, c) : max(b, c);}
	public static double max(double a, double b, double c) {return a > b? max(a, c) : max(b, c);}

	public static int min(int[] a) {if (a.length == 0) return 0; if (a.length == 1) return a[0];
		int min = a[0]; for (int i = 1; i < a.length; i++) if (a[i] < min) min = a[i]; return min;}
	public static long min(long[] a) {if (a.length == 0) return 0; if (a.length == 1) return a[0];
		long min = a[0]; for (int i = 1; i < a.length; i++) if (a[i] < min) min = a[i]; return min;}
	public static float min(float[] a) {if (a.length == 0) return 0; if (a.length == 1) return a[0];
		float min = a[0]; for (int i = 1; i < a.length; i++) if (a[i] < min) min = a[i]; return min;}
	public static double min(double[] a) {if (a.length == 0) return 0; if (a.length == 1) return a[0];
		double min = a[0]; for (int i = 1; i < a.length; i++) if (a[i] < min) min = a[i]; return min;}
	public static int max(int[] a) {if (a.length == 0) return 0; if (a.length == 1) return a[0];
		int max = a[0]; for (int i = 1; i < a.length; i++) if (a[i] > max) max = a[i]; return max;}
	public static long max(long[] a) {if (a.length == 0) return 0; if (a.length == 1) return a[0];
		long max = a[0]; for (int i = 1; i < a.length; i++) if (a[i] > max) max = a[i]; return max;}
	public static float max(float[] a) {if (a.length == 0) return 0; if (a.length == 1) return a[0];
		float max = a[0]; for (int i = 1; i < a.length; i++) if (a[i] > max) max = a[i]; return max;}
	public static double max(double[] a) {if (a.length == 0) return 0; if (a.length == 1) return a[0];
		double max = a[0]; for (int i = 1; i < a.length; i++) if (a[i] > max) max = a[i]; return max;}

	public static int pow(int base, int exp) {
		switch (base) {
			case 0: return 0;
			case 1: return 1;
			case -1: return (exp & 2) == 0? 1 : -1;
		}
		switch (exp) {
			case 0: return 1;
			case 1: return base;
			case 2: return base * base;
			case 3: return base * base * base;
		}
		if (exp > 7) {
			int result = 1;
			while (exp != 0) {
				if ((exp & 1) == 1)
					result *= base;
				exp >>= 1;
				base *= base;
			}
			return result;
		} else if (exp > 0) {//or exp > 3, same thing
			int b0 = base;
			int b1 = b0 * b0;
			base = b1 * b1;
			switch (exp) {
				default: return base;//case 4
				case 5: return base * b0;
				case 6: return base * b1;
				case 7: return base * b1 * b0;
			}
		}
		return 0;
	}
//}
//============================================================// timing //============================================================//
//{
	/**System.currentTimeMillis()*/ public static long time() {return System.currentTimeMillis();}
	/**System.nanoTime() / 1000000*/ public static long mTime() {return System.nanoTime() / 1000000;}
	/**System.nanoTime()*/ public static long nTime() {return System.nanoTime();}

	public static void countDown(int totalTime, int intervalTime) {
		p("Sleeping ");
		for (int interval = 0; interval < totalTime; interval += intervalTime) {
			pln((totalTime - interval) + "ms...");
			sleep(intervalTime);
		}
		pln("Awake.");
	}

	/*private static sun.misc.Perf perf = sun.misc.Perf.getPerf();
	private static long perfFreq = perf.highResFrequency();
	public static long pTime()
		{S.pln(Long.MAX_VALUE + " " + (perf.highResCounter() * 1000000000L));
		return (perf.highResCounter() * 1000000000L) / perfFreq;}*/
//}
//============================================================// random //============================================================//
//{
	public static double rand() {return Rand.rand.nextDouble();}
	public static float randJU() {return Rand.randJU.nextFloat();}
	public static int rand(int i) {return Rand.rand.nextInt(i);}
	public static int randJU(int i) {return Rand.randJU.nextInt(i);}
	/**a and b are inclusive*/ public static int rand(int a, int b) {return Rand.rand.nextInt(b - a + 1) + a;}
	/**a and b are inclusive*/ public static double rand(double a, double b) {return Rand.rand.nextDouble() * (b - a) + a;}

	public static int getRandomNumber()
	{
		return 4;	// chosen by fair dice roll.
					// guaranteed to be random.
	}
	
	public static  <T>  T rand(     T... a) {return a[Rand.rand.nextInt(a.length)];}
	public static     int rand(    int[] a) {return a[Rand.rand.nextInt(a.length)];}
	public static    byte rand(   byte[] a) {return a[Rand.rand.nextInt(a.length)];}
	public static    char rand(   char[] a) {return a[Rand.rand.nextInt(a.length)];}
	public static    long rand(   long[] a) {return a[Rand.rand.nextInt(a.length)];}
	public static   float rand(  float[] a) {return a[Rand.rand.nextInt(a.length)];}
	public static   short rand(  short[] a) {return a[Rand.rand.nextInt(a.length)];}
	public static  double rand( double[] a) {return a[Rand.rand.nextInt(a.length)];}
	public static boolean rand(boolean[] a) {return a[Rand.rand.nextInt(a.length)];}
//}
//============================================================// strings & print //============================================================//
//{
	/**System.out.print*/ public static void p(int eger) {System.out.print(eger);}
	/**System.out.print*/ public static void p(byte code) {System.out.print(code);}
	/**System.out.print*/ public static void p(char coal) {System.out.print(coal);}
	/**System.out.print*/ public static void p(long boat) {System.out.print(boat);}
	/**System.out.print*/ public static void p(float ation) {System.out.print(ation);}
	/**System.out.print*/ public static void p(short cut) {System.out.print(cut);}
	/**System.out.print*/ public static void p(char[] grilled) {System.out.print(grilled);}
	/**System.out.print*/ public static void p(double penetration) {System.out.print(penetration);}
	/**System.out.print*/ public static void p(Object Oriented) {System.out.print(Oriented);}
	/**System.out.print*/ public static void p(String theory) {System.out.print(theory);}
	/**System.out.print*/ public static void p(boolean logic) {System.out.print(logic);}
	/**System.out.println*/ public static void pln() {System.out.println();}
	/**System.out.println*/ public static void pln(char[] a) {System.out.println(a);}
	/**System.out.println*/ public static void pln(String s) {System.out.println(s);}

	/**.toString()'s stuff with a separator*/
	public static String sep(String separator, Object... args) {
		StringBuilder ret = new StringBuilder();
		for (Object o : args) {
			String s = o.toString();
			if (s.length() != 0) ret.append(s).append(separator);
		}
		if (args.length > 0) ret.setLength(ret.length() - separator.length());
		return ret.toString();
	}

	/**prints .toString()'d stuff with a separator of ' + " " + '*/
	public static void p(Object... args) {S.p(S.sep(" ", args));}

	/**prints .toString()'d stuff with a separator of ' + " " + '*/
	public static void pln(Object... args) {S.pln(S.sep(" ", args));}

	/**pln with .toHex(...)ing on Numbers*/
	public static void plnhex(Object... args) {
		StringBuilder print = new StringBuilder();
		for (Object o : args)
			if (o instanceof Number) {
					 if (o instanceof Integer) print.append(toHex((Integer)o)).append(' ');
				else if (o instanceof Double ) print.append(toHex((Double )o)).append(' ');
				else if (o instanceof Float  ) print.append(toHex((Float  )o)).append(' ');
				else if (o instanceof Byte   ) print.append(toHex((Byte   )o)).append(' ');
				else if (o instanceof Short  ) print.append(toHex((Short  )o)).append(' ');
				else if (o instanceof Long   ) print.append(toHex((Long   )o)).append(' ');
			} else print.append(o).append(' ');
		S.pln(print.length() > 0? print.substring(0, print.length() - 1) : print.toString());
	}

	/**Double.toHexString(hex)*/ public static String toHex(double hex) {return Double.toHexString(hex);}
	/**Float.toHexString(hex)*/ public static String toHex(float hex) {return Float.toHexString(hex);}
	/**efficiently converts to hex string*/ public static String toHex(byte hex) {return new String (new char[]{hexchar[hex >>> 4 & 0xf], hexchar[hex & 0xf]});}
	/**efficiently converts to hex string*/ public static String toHex(short hex)
		{return new String(new char[]{hexchar[hex >>> 12], hexchar[hex >>> 8 & 0xf], hexchar[hex >>> 4 & 0xf], hexchar[hex & 0xf]});}
	/**efficiently converts to hex string*/ public static String toHex(int hex)
		{return new String(new char[]{hexchar[hex >>> 28      ], hexchar[hex >>> 24 & 0xf], hexchar[hex >>> 20 & 0xf], hexchar[hex >>> 16 & 0xf],
		                              hexchar[hex >>> 12 & 0xf], hexchar[hex >>>  8 & 0xf], hexchar[hex >>>  4 & 0xf], hexchar[hex        & 0xf]});}
	/**efficiently converts to hex string*/ public static String toHex(long hex)
		{int hex1 = (int)(hex >>> 32); int hex2 = (int)hex;
		return new String(new char[]{hexchar[hex1 >>> 28      ], hexchar[hex1 >>> 24 & 0xf], hexchar[hex1 >>> 20 & 0xf], hexchar[hex1 >>> 16 & 0xf],
		                             hexchar[hex1 >>> 12 & 0xf], hexchar[hex1 >>>  8 & 0xf], hexchar[hex1 >>>  4 & 0xf], hexchar[hex1        & 0xf],
		                             hexchar[hex2 >>> 28      ], hexchar[hex2 >>> 24 & 0xf], hexchar[hex2 >>> 20 & 0xf], hexchar[hex2 >>> 16 & 0xf],
		                             hexchar[hex2 >>> 12 & 0xf], hexchar[hex2 >>>  8 & 0xf], hexchar[hex2 >>>  4 & 0xf], hexchar[hex2        & 0xf]});}
	private static final char[] hexchar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}