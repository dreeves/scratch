package jfront;

import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;

public class StreamToken
{
	private static final byte CT_WHITESPACE = 1;
	private static final byte CT_DIGIT = 2;
	private static final byte CT_ALPHA = 4;
	private static final byte CT_QUOTE = 8;
	private static final byte CT_COMMENT = 16;
	public static final int TT_EOF = -1;
	public static final int TT_EOL = 10;
	public static final int TT_NUMBER = -2;
	public static final int TT_WORD = -3;
	public static final int TT_COMMENT = -4;
	public static final int TT_OPER = -5;
	public static final int TT_NOTHING = -6;
	
	private static final byte[] ctype = new byte[256];
	static {
		//set all to ordinaryChars, then:
		//set [0, 33) to whitespaceChar
		//set 34 & 39 to quoteChar
		//set 47 to commentChar
		//set 46 & [48, 58) & [65, 91) & 92 & 95 & [97, 123) & [160, 256) to wordChar
		int[] ct =
		{
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 4,16, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0,
			0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 4, 0, 0, 4,
			0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
			4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
			4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
		};
		for (int i = 0; i < ctype.length; i++)
			ctype[i] = (byte)ct[i];
	}
	
	private StringStream input;
	private int peekc;
	private int debugLINENO = 1;
	private boolean iscomment;
	public int ttype = TT_NOTHING;
	public String sval;
	public double nval;
	
	public StreamToken(StringStream input)
	{
		this.input = (input != null? input : new StringStream(""));
	}
	
	public void nextToken()
	{
		int i;
		if (ttype == TT_NOTHING)
		{
			i = input.read();
			if (i >= 0)
				ttype = i;
		}
		else
			i = peekc;

		sval = null;
		
		if (i < 0)
			{ttype = i; return;}

		int j = i < 256? ctype[i] : 4;

		while ((j & CT_WHITESPACE) != 0)
			if (i == '\r')
			{
				debugLINENO++;
				i = input.read();
				if (i == TT_EOL)
					i = input.read();
				peekc = i;
				ttype = TT_EOL; return;
			}
			else if (i == '\n')
			{
				debugLINENO++;
				peekc = input.read();
				ttype = i; return;
			}
			else
			{
				i = input.read();
				if (i == TT_EOF)
					{ttype = i; return;}
				j = i < 256? ctype[i] : 4;
			}
		
		if (iscomment || (j & CT_COMMENT) != 0)
		{
			peekc = input.read();
			if (iscomment || peekc == '*' || peekc == '/')
			{
				int k = 1;
				char[] buf = new char[20];
				buf[0] = (char)i;
				i = peekc;
				while (i != '\n' && i != '\r' && i >= 0)
				{
					if (k == 1 && i == '*')
						iscomment = true;

					if (k >= buf.length)
						buf = Arrays.copyOf(buf, buf.length * 2);
					buf[k++] = (char)i;

					if (iscomment && i == '/' && buf[k - 2] == '*')
					{
						iscomment = false;
						i = input.read();
						break;
					}
					i = input.read();
				}
				sval = new String(buf).substring(0, k);
				peekc = i;
				ttype = TT_COMMENT; return;
			}
		}

		if ((j & CT_DIGIT) != 0)
		{
			int signFactor = 1;
			if (i == '-')
			{
				i = input.read();
				if (i != '.' && (i < '0' || i > '9'))
				{
					ttype = '-'; return;
					peekc = i;
				}
				signFactor = -1;
			}
			double d = 0.0;
			int exp = 0;
			boolean passedDecimalPoint = false;
			while (true)
			{
				if (i == '.')
					passedDecimalPoint = true;
				else if (passedDecimalPoint)
				{
					if (i < '0' || i > '9')
						break;
					d = d * 10 + (i - '0');
					exp++;
				}

				i = input.read();
			}
			peekc = i;
			nval = d / S.intpow(10, exp) * signFactor;
			ttype = TT_NUMBER; return;
		}
		
		if ((j & CT_ALPHA) != 0)
		{
			int k = 0;
			char[] buf = new char[20];
			do
			{
				if (k >= buf.length)
					buf = Arrays.copyOf(buf, buf.length * 2);
				buf[k++] = (char)i;
				i = input.read();
				j = (i < 256) ? ctype[i] : (i < 0) ? 1 : 4;
			} while ((j & (CT_DIGIT | CT_ALPHA)) != 0);
			peekc = i;
			sval = new String(buf).substring(0, k);
			ttype = TT_WORD; return;
		}

		if ((j & CT_QUOTE) != 0)
		{
			ttype = i;
			int k = 0;
			char[] buf = new char[20];

			peekc = input.read();
			while (peekc >= 0 && peekc != ttype && peekc != '\n' && peekc != '\r')
			{
				i = peekc;
				peekc = input.read();

				if (k >= buf.length)
					buf = Arrays.copyOf(buf, buf.length * 2);
				buf[k++] = (char)i;
			}
			if (peekc == ttype)
				peekc = input.read();
			sval = new String(buf).substring(0, k);
			return;
		}

		if (i != '/')
			peekc = input.read();

		String opstr = String.valueOf((char)i);

		if (!Opers.look(opstr))
			{ttype = i; return;}

		for (int l = 0; l < 3; ++l)
		{
			opstr += String.valueOf((char)peekc);
			if (!Opers.look(opstr))
			{
				sval = opstr.substring(0, opstr.length() - 1);
				ttype = TT_OPER; return;
			}
			peekc = input.read();
		}
		sval = opstr;

		ttype = TT_OPER; return;
	}
}
	
	/*private void resetSyntax()
	{
		for (int i = ctype.length; --i >= 0; )
			ctype[i] = 0;
	}

	private void wordChars(int i1, int i2)
	{
		if (i1 < 0)
			i1 = 0;
		if (i2 >= ctype.length)
			i2 = ctype.length - 1;
		int i3 = i1;
		while (i3 <= i2)
		{
			int i = i3++;
			ctype[i] = (byte)(ctype[i] | 0x4);
		}
		
	}
	private void wordChar(int i) {if (i >= 0 && i < ctype.length) ctype[i] = (byte)(ctype[i] | 0x4);}
	private void whitespaceChars(int i1, int i2)
	{
		if (i1 < 0)
			i1 = 0;
		if (i2 >= ctype.length)
			i2 = ctype.length - 1;
		while (i1 <= i2)
			ctype[i1++] = 1;
	}
	private void ordinaryChars(int i1, int i2)
	{
		if (i1 < 0)
			i1 = 0;
		if (i2 >= ctype.length)
			i2 = ctype.length - 1;
		while (i1 <= i2)
			ctype[i1++] = 0;
	}
	//private void ordinaryChar(int i) {if (i >= 0 && i < ctype.length) ctype[i] = 0;}
	private void commentChar(int i) {if (i >= 0 && i < ctype.length) ctype[i] = 16;}
	private void quoteChar(int i) {if (i >= 0 && i < ctype.length) ctype[i] = 8;}

	private void parseNumbers()
	{
		for (int i = 48; i <= 57; ++i)
			ctype[i] = (byte)(ctype[i] | 0x2);
		ctype['.'] = (byte)(ctype['.'] | 0x2);
		ctype[45] = (byte)(ctype[45] | 0x2);
	}

	public String toString()
	{
		String str;
		switch (ttype)
		{
			case -1: str = "EOF"; break;
			case 10: str = "EOL"; break;
			case TT_WORD: str = sval; break;
			case -2: str = "n=" + nval; break;
			case TT_NOTHING: str = "NOTHING"; break;
			default:
				char[] arr = new char[3];
				arr[2] = 39; arr[0] = 39;
				arr[1] = (char)ttype;
				str = new String(arr);
		}

		return "Token[" + str + "], line " + debugLINENO;
	}*/