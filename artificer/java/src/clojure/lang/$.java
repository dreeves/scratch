////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
/////  THE FOLLOWING MATERIAL IS DUPLICATED ELSEWHERE  /////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
package clojure.lang;

import java.io.PushbackReader;
import java.util.*;
import static clojure.lang.LispReader.*;

public class $ {

static public Object atom_get_set(Atom ref, Object v) {return ref.state.getAndSet(v);}

// a near copy of clojure.lang.LispReader$FnReader
// removes two lines to not disallow nested #()s
// uses lparen instead of '(' to allow forms like #‹% + 9›
static public class FnReader extends AFn {
	public Object invoke(Object reader, Object lparen) {
		PushbackReader r = (PushbackReader) reader;
		//if(ARG_ENV.deref() != null)
		//	throw new IllegalStateException("Nested #()s are not allowed");
		try
			{
			Var.pushThreadBindings(
					RT.map(ARG_ENV, PersistentTreeMap.EMPTY));
			unread(r, (char)(int)(Integer)lparen);
			Object form = read(r, true, null, true);

			PersistentVector args = PersistentVector.EMPTY;
			PersistentTreeMap argsyms = (PersistentTreeMap) ARG_ENV.deref();
			ISeq rargs = argsyms.rseq();
			if(rargs != null)
				{
				int higharg = (Integer) ((Map.Entry) rargs.first()).getKey();
				if(higharg > 0)
					{
					for(int i = 1; i <= higharg; ++i)
						{
						Object sym = argsyms.valAt(i);
						if(sym == null)
							sym = garg(i);
						args = args.cons(sym);
						}
					}
				Object restsym = argsyms.valAt(-1);
				if(restsym != null)
					{
					args = args.cons(Compiler._AMP_);
					args = args.cons(restsym);
					}
				}
			return RT.list(Compiler.FN, args, form);
			}
		finally
			{
			Var.popThreadBindings();
			}
	}
}

static private IFn getMacro(int ch){
	if(ch < macros.length)
		return macros[ch];
	return null;
}
// altered specifically for implementing ⌊⌋/⌊⌉
public static List readDelimitedList_2(PushbackReader r) {
	boolean isRecursive = true; // altered

	final int firstline =
			(r instanceof LineNumberingPushbackReader) ?
			((LineNumberingPushbackReader) r).getLineNumber() : -1;

	ArrayList a = new ArrayList();
	a.add(null);

	for(; ;)
		{
		int ch = read1(r);

		while(isWhitespace(ch))
			ch = read1(r);

		if(ch == -1)
			{
			if(firstline < 0)
				throw Util.runtimeException("EOF while reading");
			else
				throw Util.runtimeException("EOF while reading, starting at line " + firstline);
			}

		if(ch == 0x230B) {a.set(0,Symbol.intern(null,"floor")); break;} // altered
		if(ch == 0x2309) {a.set(0,Symbol.intern(null,"round")); break;} // altered

		IFn macroFn = getMacro(ch);
		if(macroFn != null)
			{
			Object mret = macroFn.invoke(r, (char) ch);
			//no op macros return the reader
			if(mret != r)
				a.add(mret);
			}
		else
			{
			unread(r, ch);

			Object o = read(r, true, null, isRecursive);
			if(o != r)
				a.add(o);
			}
		}


	return a;
}

}