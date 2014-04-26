package misc;

public class $ {
	static public Runnable _c_ex_wrap_(final Runnable f) {
		class _c_ex_wrap_ implements Runnable {public void run() {f.run();}}
		return new _c_ex_wrap_();}

	static public class Break extends Throwable {}

	//static public int endian_int(int v) {return (v >> 24) | ((v & 0xff0000) >> 8) | ((v & 0xff00) << 8) | (v << 24);}
	//static public int[] endian_int_array(int[] v) {for (int i=0;i<v.length;i++) v[i] = endian_int(v[i]); return v;}
}