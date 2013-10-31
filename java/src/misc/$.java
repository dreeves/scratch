////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
/////  THE FOLLOWING MATERIAL IS DUPLICATED ELSEWHERE  /////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
package misc;

public class $ {
	static public Runnable _c_ex_wrap_(final clojure.lang.IFn f, final clojure.lang.IFn err) {
		class _c_ex_wrap_ implements Runnable {public void run() {try {f.invoke();} catch (Throwable e) {err.invoke(e);}}}
		return new _c_ex_wrap_();}

	//static public int endian_int(int v) {return (v >> 24) | ((v & 0xff0000) >> 8) | ((v & 0xff00) << 8) | (v << 24);}
	//static public int[] endian_int_array(int[] v) {for (int i=0;i<v.length;i++) v[i] = endian_int(v[i]); return v;}
}