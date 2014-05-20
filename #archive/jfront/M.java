package jfront;

import java.awt.TextArea;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Hashtable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static jfront.StreamToken.*;

public /*static*/ class M
{
	public static void main(String[] args)
	{
		String fileData = readFile("Complex.jf");
		TextFile out = new TextFile("jfront/Complex.java");
		List opdefs = new ArrayList();
		M.pass1(fileData, out, opdefs);
		M.pass2(fileData, out, opdefs);
	}
	
	private static String readFile(String filename)
	{
		StringBuilder ret = new StringBuilder();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(M.class.getResourceAsStream(filename), "UTF-8"));
			//specifically, the file is expected to be encoded in UTF-8 with or without BOM
			
			while (true)
			{
				String line = reader.readLine();
				if (line == null)
					break;
				ret.append(line);
				ret.append("\r\n");
			}
			if (ret.length() >= 2)
				ret.setLength(ret.length() - 2);

			reader.close();
		}
		catch (java.net.MalformedURLException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		return ret.toString();
	}

	public static void pass1(String file, TextFile out, List opdefs)
	{
		StreamToken st = new StreamToken(new StringStream(file));
		st.nextToken();
		String className = "";
		String innerClassName = "";
		int j = 0, l = 0;
		while (st.ttype != TT_EOF)
		{
			if (st.ttype == TT_EOL || st.ttype == 4)
				{st.nextToken(); continue;}
			else if (st.ttype == TT_NUMBER)
				{S.p(st.nval); S.claim(false, "We're not supposed to ever get here...");}
			else if (st.ttype == TT_WORD)
			{
				if (st.sval.equals("class"))
				{
					st.nextToken();
					if (st.ttype != TT_WORD)
						continue;
					if (l <= j)
					{
						className = st.sval;
						j = l;
						S.claim(j <= 0, "j can't be " + j + ", can it?");
						innerClassName = "";
					}
					else if (l == j + 1)
						innerClassName = st.sval;
				}

				if (st.sval.equals("operator"))
				{
					st.nextToken();
					if (st.ttype != TT_OPER)
						continue;

					Opdef opd = new Opdef();
					opd.op = st.sval;
					opd.classname = className;
					opd.innername = innerClassName;
					opdefs.add(opd);
				}
			}
			else if (st.ttype == '{')
				++l;
			else if (st.ttype == '}')
			{
				--l;
				if (l <= j + 1)
					innerClassName = "";
			}
			st.nextToken();
		}
	}

	public static void pass2(String file, TextFile out, List opdefs)
	{
		Hashtable<String, String> class_ops = new Hashtable<String, String>();
		Hashtable<String, String> class_wops = new Hashtable<String, String>();

		for (Object o : opdefs)
		{
			Opdef opdef = (Opdef)o;

			String className = opdef.classname;
			String str5 = opdef.classname + ":" + opdef.op;
			if (!opdef.innername.equals(""))
			{
				str5 = opdef.innername + ":" + opdef.op;
				className = opdef.innername;
			}
			class_ops.put(str5, "operator" + Opers.name(opdef.op));
			
			if (class_wops.containsKey(className))
				continue;
			class_wops.put(className, className);
		}

		StreamToken st = new StreamToken(new StringStream(file));
		st.nextToken();
		List<Lntoken> tokenList = new ArrayList<Lntoken>();
		Hashtable<String, String> class_vars = new Hashtable<String, String>();
		int i = 0, k = 0;
		boolean writeAndPrependSpace = false;
		while (st.ttype != TT_EOF)
		{
			boolean cont = false;
			if (st.ttype == TT_EOL || st.ttype == TT_NUMBER || st.ttype == TT_COMMENT || st.ttype == TT_OPER)
			{
				String token;
				if (st.ttype == TT_EOL) token = "\n";
				else if (st.ttype == TT_NUMBER) token = " " + st.nval;
				else token = st.sval;
				tokenList.add(new Lntoken(st.ttype, token));
				writeAndPrependSpace = false; //j = st.ttype;
			}
			else if (st.ttype == TT_WORD)
			{
				if (st.sval.equals("import") || st.sval.equals("class"))
				{
					String sval = st.sval;
					tokenList.add(new Lntoken(TT_WORD, " " + st.sval));
					writeAndPrependSpace = true; //j = TT_WORD;
					st.nextToken();
					if (st.ttype != TT_WORD) continue;
					if (sval.equals("class") && k <= i)
						i = k;
				}

				if (st.sval.equals("operator"))
				{
					st.nextToken();
					if (st.ttype != TT_OPER)
						continue;
					st.sval = "operator" + Opers.name(st.sval);
				}

				if (class_wops.containsKey(st.sval))
				{
					tokenList.add(new Lntoken(TT_WORD, " " + st.sval));
					writeAndPrependSpace = true; //j = TT_WORD;

					String sval = st.sval;

					st.nextToken();
					while (true)
					{
						if (st.ttype != TT_WORD || st.sval.equals("operator"))
							{cont = true; break;}
						class_vars.put(st.sval, sval);
						tokenList.add(new Lntoken(TT_WORD, " " + st.sval));

						writeAndPrependSpace = true; //j = TT_WORD;
						st.nextToken();
						if (st.ttype != ',')
							{cont = true; break;}
						tokenList.add(new Lntoken(TT_NOTHING, new Character((char)st.ttype).toString()));
						writeAndPrependSpace = false; //j = TT_NOTHING;
						st.nextToken();
					}
					if (cont)
						if (st.ttype != TT_EOF)
							continue;
						else
							break;
				}

				if (writeAndPrependSpace)
				{
					out.write(" ");
					st.sval = " " + st.sval;
				}
				tokenList.add(new Lntoken(TT_WORD, st.sval));
				writeAndPrependSpace = true; //j = TT_WORD;
			}
			else
			{
				char c = (char)st.ttype;
				if (c == '"' || c == '\'')
					tokenList.add(new Lntoken(TT_NOTHING, c + st.sval + c));
				else
				{
					tokenList.add(new Lntoken(TT_NOTHING, String.valueOf(c)));
					if (c == '{') ++k;
					if (c == '}') --k;
					if (c == ';')
					{
						analyze(out, tokenList, class_vars, class_ops);
						while (tokenList.size() > 0) tokenList.remove(0);
					}
				}
				writeAndPrependSpace = false; //j = TT_NOTHING;
			}

			st.nextToken();
		}

		analyze(out, tokenList, class_vars, class_ops);

		while (tokenList.size() > 0) tokenList.remove(0);
		
		out.close();
	}

	private static void analyze(TextFile out, List<Lntoken> tokenList, Hashtable<String, String> class_vars, Hashtable<String, String> class_ops)
	{
		String str1 = "";
		String localstr1 = " ";
		while (true)
		{
			List<Paren> parenList = new ArrayList<Paren>();
			int parenCount = 0;
			int l = 0;
			for (Lntoken lntoken : tokenList)
			{
				if (lntoken.token.equals("("))
				{
					l = tokenList.indexOf(lntoken);
					parenList.add(new Paren(l, TT_EOF, "", parenCount));
					++parenCount;
				}
				
				if (l != 0 && tokenList.indexOf(lntoken) == l + 1 && lntoken.type == TT_WORD)
					parenList.get(parenList.size() - 1).token = lntoken.token;

				if (lntoken.token.equals(")"))
				{
					--parenCount;
					if (parenCount < 0)
					{
						out.write("\n// JFront Error: The following statement has too many close Paren')'s \n");
						break;
					}
					for (int k = parenList.size() - 1; k >= 0; --k)
					{
						Paren paren = parenList.get(k);
						if (paren.token.length() != 0)
							localstr1 = paren.token;
						if (paren.after < 0)
						{
							paren.after = tokenList.indexOf(lntoken);
							if (paren.token.length() == 0)
								paren.token = localstr1;
							parenList.set(k, paren);
							break;
						}
					}
				}
			}
			
			if (parenCount > 0)
				out.write("\n// JFront Error: The following statement has unbalenced Paren'()'s \n");
			if (parenCount != 0)
				break;
				
			localstr1 = " ";
			boolean j = true;
			int i2 = 0;
			int i5 = 0;
			int i6 = 0;
			boolean k = false;
			int priority = 99;
			int parenCount2 = 0;
			String str8 = " ";
			for (Lntoken lntoken : tokenList)
			{
				if (lntoken.token.equals("("))
					{++parenCount2; ++i2;}
				if (lntoken.token.equals(")"))
					--parenCount2;
				if (k && lntoken.type == TT_OPER)
				{
					localstr1 = class_vars.get(str1) + ":" + lntoken.token; // ?
					String str9 = class_vars.get(str1) + ":" + lntoken.token;
					if (class_ops.containsKey(str9) && (parenCount2 > i5 || (parenCount2 == i5 && Opers.priority(lntoken.token) < priority)))
					{
						priority = Opers.priority(lntoken.token);
						i5 = parenCount2;
						i6 = tokenList.indexOf(lntoken);
						str8 = str9;
						j = false;
					}
				}

				k = false;
				str1 = "";

				if (lntoken.type == TT_WORD)
				{
					if (class_vars.containsKey(lntoken.token))
					{
						k = true;
						str1 = lntoken.token;
					}
				}
				else if (lntoken.token.equals(")"))
				{
					if (class_vars.containsKey(parenList.get(i2 - 1).token))
					{
						k = true;
						str1 = parenList.get(i2 - 1).token;
					}
				}
			}

			if (j)
				break;
			localstr1 = str8; // ?

			int i1 = i6 + 2;
			l = i6 - 1;
			if (tokenList.size() > i6 + 1)
			{
				Lntoken lntoken = tokenList.get(i6 + 1);

				if (lntoken.token.equals("("))
					i1 = parenList.get(i2 - 1).after;

				lntoken = tokenList.get(i6 - 1);

				if (lntoken.token.equals(")"))
				for (int i8 = 0; i8 < i2 - 1; --i8)
				if (parenList.get(i8).after == i6 - 1)
				{
					l = parenList.get(i8).before;
					break;
				}
			}

			tokenList.add(i1, new Lntoken(TT_NOTHING, ")"));
			tokenList.add(i1, new Lntoken(TT_NOTHING, ")"));
			tokenList.add(i6 + 1, new Lntoken(TT_NOTHING, "("));
			tokenList.set(i6, new Lntoken(TT_OPER, class_ops.get(str8)));
			tokenList.add(i6, new Lntoken(TT_NOTHING, "."));
			tokenList.add(l, new Lntoken(TT_NOTHING, "("));
				
			if (j)
				break;
		}

		int bracelev = 0;
		for (Lntoken token : tokenList)
		{
			String tokenO3 = token.token;
			if (tokenO3.equals("{")) bracelev++;
			if (tokenO3.equals("}")) bracelev--;

			out.write(tokenO3);
			if (tokenO3.substring(tokenO3.length() - 1).equals("\n"))
			for (int m = 0; m < bracelev; ++m)
				out.write(" ");
		}
	}
}

class Lntoken
{
	public final int type;
	public final String token;

	public Lntoken(int type, String token)
	{
		this.type = type;
		this.token = token;
	}
}

class Paren
{
	public final int before;
	public int after;
	public String token;

	public Paren(int before, int after, String token, int level)
	{
		this.before = before;
		this.after = after;
		this.token = token;
	}
}

class Opdef
{
	public String op;
	public String classname;
	public String innername;
}

class StringStream
{
	private final char[] buf;
	private int ptr = -1;

	public StringStream(String ins) {buf = ins.toCharArray();}

	public int read()
	{
		ptr++;
		return ptr < buf.length? buf[ptr] : -1;
	}
}