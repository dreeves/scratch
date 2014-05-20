package jfront;

import java.util.HashMap;

public class Opers
{
	private static final IntStr dummy = new IntStr(0, "");
	private static final HashMap<String, IntStr> ops = new HashMap<String, IntStr>(67);
	private static IntStr pnrtn;
	private static String last = "";

	static {
		ops.put("!", new IntStr(0, "not"));
		ops.put("~", new IntStr(0, "tilde"));
		ops.put("++", new IntStr(0, "incr"));
		ops.put("--", new IntStr(0, "decr"));

		ops.put("*", new IntStr(1, "mult"));
		ops.put("/", new IntStr(1, "div"));
		ops.put("%", new IntStr(1, "pct"));

		ops.put("+", new IntStr(2, "plus"));
		ops.put("-", new IntStr(2, "minus"));

		ops.put("<<", new IntStr(3, "2lt"));
		ops.put(">>", new IntStr(3, "2gt"));
		ops.put(">>>", new IntStr(3, "3gt"));

		ops.put("<", new IntStr(4, "lt"));
		ops.put("<=", new IntStr(4, "lteq"));
		ops.put(">", new IntStr(4, "gt"));
		ops.put(">=", new IntStr(4, "gteq"));

		ops.put("==", new IntStr(5, "eqeq"));
		ops.put("!=", new IntStr(5, "noteq"));

		ops.put("&", new IntStr(6, "and"));
		ops.put("^", new IntStr(7, "caret"));
		ops.put("|", new IntStr(8, "or"));
		ops.put("&&", new IntStr(9, "2and"));
		ops.put("||", new IntStr(10, "2or"));

		ops.put("=", new IntStr(11, "assign"));
		ops.put("*=", new IntStr(11, "stareq"));
		ops.put("/=", new IntStr(11, "slaseq"));
		ops.put("+=", new IntStr(11, "pluseq"));
		ops.put("-=", new IntStr(11, "minueq"));
		ops.put("%=", new IntStr(11, "pcteq"));
		ops.put("<<=", new IntStr(11, "2lteq"));
		ops.put(">>=", new IntStr(11, "2gteq"));
		ops.put(">>>=", new IntStr(11, "3gteq"));
		ops.put("&=", new IntStr(11, "andeq"));
		ops.put("^=", new IntStr(11, "careq"));
		ops.put("|=", new IntStr(11, "oreq"));
	}

	public static boolean look(String s)
	{
		IntStr is = ops.get(s);
		pnrtn = (is == null? dummy : is);
		last = s;
		return is != null;
	}

	public static int priority(String s)
	{
		if (s.equals(last))
			return pnrtn.level;
		look(s);
		return pnrtn.level;
	}

	public static String name(String s)
	{
		if (s.equals(last))
			return pnrtn.name;
		look(s);
		return pnrtn.name;
	}
	
	private static class IntStr
	{
		public final int level;
		public final String name;
		public IntStr(int level, String name) {this.level = level; this.name = name;}
	}
}