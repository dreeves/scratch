package clojure.lang;

import java.io.PushbackReader;
import java.util.Map;
import static clojure.lang.LispReader.*;

public class $ {
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

}