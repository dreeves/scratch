public class X
{
	public static void main(String[] args)
	{
		N n = new N();
		//e = false;

		long t = t();
		for (int i = 0; i < 100*1000*1000; i++)
			n.a();
		pln((t() - t) / 1000000 + " ");

		t = t();
		for (int i = 0; i < 100*1000*1000; i++)
			n.b();
		pln((t() - t) / 1000000 + " ");

		t = t();
		for (int i = 0; i < 100*1000*1000; i++)
			n.c();
		pln((t() - t) / 1000000 + " ");

		t = t();
		for (int i = 0; i < 100*1000*1000; i++)
			n.d();
		pln((t() - t) / 1000000 + " ");

		t = t();
		for (int i = 0; i < 100*1000*1000; i++)
			n.f();
		pln((t() - t) / 1000000 + " ");

		t = t();
		for (int i = 0; i < 1000*1000*1000; i++)
			;
		pln((t() - t) / 1000000 + " ");

		t = t();
		t = t() - t();
		pln(t + " ");
	}

	public final static boolean e = false;

	public static long t()
		{return System.nanoTime();}
	public static void pln(String plny)
		{System.out.println(plny);}
}

class N
{
	public int a()
		{return 0;}
	public int b()
		{if (X.e) X.pln("b()"); return 1;}
	public void c()
		{}
	public void d()
		{if (X.e) X.pln("b()");}
	private int i = 0;
	private int j = 5;
	public int f()
		{return i++ * j++;}
}