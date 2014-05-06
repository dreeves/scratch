package jutil;

/** Everything in the package in just one file! */
public class $ {

/** can't make subclasses of non-interface classes in clojure >.< */
static public class Break extends Throwable {
	//@Override Throwable fillInStackTrace() {this}
}

/** currently used only by runesmith */
static public long   unchecked_l_add(long   a, long   b){return a + b;}
static public long   unchecked_l_sub(long   a, long   b){return a - b;}
static public long   unchecked_l_neg(long   a          ){return -a;}
static public long   unchecked_l_mul(long   a, long   b){return a * b;}
static public long   unchecked_l_div(long   a, long   b){return a / b;}
static public long   unchecked_l_rem(long   a, long   b){return a % b;}
static public double unchecked_d_add(double a, double b){return a + b;}
static public double unchecked_d_sub(double a, double b){return a - b;}
static public double unchecked_d_neg(double a          ){return -a;}
static public double unchecked_d_mul(double a, double b){return a * b;}
static public double unchecked_d_div(double a, double b){return a / b;}
static public double unchecked_d_rem(double a, double b){return a % b;}
static public float  unchecked_f_add(float  a, float  b){return a + b;}
static public float  unchecked_f_sub(float  a, float  b){return a - b;}
static public float  unchecked_f_neg(float  a          ){return -a;}
static public float  unchecked_f_mul(float  a, float  b){return a * b;}
static public float  unchecked_f_div(float  a, float  b){return a / b;}
static public float  unchecked_f_rem(float  a, float  b){return a % b;}

}