package flea;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.PrintStream;

public class a extends Applet
	implements Runnable
{
	int a = 1;
	int b = 512;
	int c = 384;
	Thread d = null;
	int e = 20;
	int f_f = 1000;
	long[] g = new long[10];
	b h = null;
	boolean i;
	int j = 0;
	int k = 0;
	public int l = 0;
	boolean m = false;
	int n = 0;
	Font o = new Font("TimesRoman", 0, 15);
	Font p = new Font("Helvetica", 1, 13);
	Font q = new Font("Helvetica", 0, 12);
	Image r;
	Graphics s;
	public boolean t = false;
	public boolean u = false;
	public boolean v = false;
	public boolean w = false;
	public boolean x = false;
	public boolean y = false;
	public boolean z = false;
	public boolean A = false;
	public int B = 1;
	public int C = 0;
	public int D = 0;
	public int E = 0;
	public int F = 0;
	public int G = 0;
	public int H = 0;
	public boolean I = false;
	public String J = "";
	public String K = "";
	public String L = "";
	public String M = "";
	public int N = 0;

	public final void stop()
	{
		if (this.j >= 0)
			this.j = (4000 / this.e);
	}

	public final Image createImage(int paramInt1, int paramInt2)
	{
		if (this.h == null)
			return super.createImage(paramInt1, paramInt2);
		return this.h.createImage(paramInt1, paramInt2);
	}

	public void a()
	{
	}

	public void a(int paramInt1, int paramInt2)
	{
		b(paramInt1, paramInt2);
	}

	public void b()
	{
	}

	public final void update(Graphics paramGraphics)
	{
		paint(paramGraphics);
	}

	public void a(int paramInt)
	{
		int i1 = (this.b - 281) / 2;
		int i2 = (this.c - 148) / 2;
		this.s.setColor(Color.black);
		this.s.fillRect(0, 0, this.b, this.c);
		if (!this.m)
			this.s.drawImage(this.r, i1, i2, this);
		i1 += 2;
		i2 += 90;
		this.n = paramInt;
		this.s.setColor(new Color(132, 132, 132));
		if (this.m)
			this.s.setColor(new Color(220, 0, 0));
		this.s.drawRect(i1 - 2, i2 - 2, 280, 23);
		this.s.fillRect(i1, i2, 277 * paramInt / 100, 20);
		this.s.setColor(new Color(198, 198, 198));
		if (this.m)
			this.s.setColor(new Color(255, 255, 255));
		a(this.s, "Now Loading - " + paramInt + "%", this.o, i1 + 138, i2 + 10);
		if (!this.m)
		{
			a(this.s, "Created by JAGeX - visit www.jagex.com", this.p, i1 + 138, i2 + 30);
			a(this.s, "Copyright ©2000 Andrew Gower", this.p, i1 + 138, i2 + 44);
		}
		else
		{
			this.s.setColor(new Color(132, 132, 152));
			a(this.s, "Copyright ©2000 Andrew Gower", this.q, i1 + 138, this.c - 20);
		}
	}

	public final void start()
	{
		if (this.j >= 0)
		{
			this.j = 0;
			if ((!this.d.isAlive()) || (this.d == null))
			{
				this.d = new Thread(this);
				this.d.start();
				System.out.println("Ressurect!");
			}
		}
	}

	public final void fromMain(int paramInt1, int paramInt2, String paramString, boolean paramBoolean)
	{
		this.i = false;
		System.out.println("Started application");
		this.b = paramInt1;
		this.c = paramInt2;
		this.h = new b(this, paramInt1, paramInt2, paramString, paramBoolean, false);
		this.a = 1;
		f.a(this.h, false);
		this.d = new Thread(this);
		this.d.start();
		this.d.setPriority(1);
	}

	public synchronized void c()
	{
	}

	public synchronized boolean keyDown(Event paramEvent, int paramInt)
	{
		b(paramInt);
		this.G = paramInt;
		this.H = paramInt;
		if (paramInt == 1006)
			this.v = true;
		if (paramInt == 1007)
			this.w = true;
		if (paramInt == 1004)
			this.x = true;
		if (paramInt == 1005)
			this.y = true;
		if ((char)paramInt == ' ')
			this.z = true;
		if (((char)paramInt == 'n') || ((char)paramInt == 'm'))
			this.A = true;
		if (((char)paramInt == 'N') || ((char)paramInt == 'M'))
			this.A = true;
		if ((char)paramInt == '{')
			this.t = true;
		if ((char)paramInt == '}')
			this.u = true;
		//if ((char)paramInt == 'ϰ')
		if ((char)paramInt == '\u03ba')
			this.I = (!this.I);
		if (((paramInt >= 97) && (paramInt <= 122)) || ((paramInt >= 65) && (paramInt <= 90)) || ((paramInt >= 48) && (paramInt <= 57)) || ((paramInt == 32) && (this.J.length() < 16)))
			this.J += (char)paramInt;
		if ((paramInt >= 32) && (paramInt <= 122) && (this.L.length() < 80))
			this.L += (char)paramInt;
		if ((paramInt == 8) && (this.J.length() > 0))
			this.J = this.J.substring(0, this.J.length() - 1);
		if ((paramInt == 8) && (this.L.length() > 0))
			this.L = this.L.substring(0, this.L.length() - 1);
		if ((paramInt == 10) || (paramInt == 13))
		{
			this.K = this.J;
			this.M = this.L;
		}
		return true;
	}

	public void b(int paramInt)
	{
	}

	public void d()
	{
	}

	public final void paint(Graphics paramGraphics)
	{
		if (this.a == 2)
			a(this.n);
		else if (this.a == 0)
			a();
	}

	public synchronized boolean mouseUp(Event paramEvent, int paramInt1, int paramInt2)
	{
		this.C = paramInt1;
		this.D = (paramInt2 + this.l);
		this.E = 0;
		return true;
	}

	public final void destroy()
	{
		System.out.println("Closing program");
		this.j = -1;
		d();
		if (this.d != null)
		{
			this.d.stop();
			this.d = null;
		}
		if (this.h != null)
			this.h.dispose();
		if (!this.i)
			System.exit(0);
	}

	public synchronized void e()
	{
	}

	public synchronized boolean keyUp(Event paramEvent, int paramInt)
	{
		this.G = 0;
		if (paramInt == 1006)
			this.v = false;
		if (paramInt == 1007)
			this.w = false;
		if (paramInt == 1004)
			this.x = false;
		if (paramInt == 1005)
			this.y = false;
		if ((char)paramInt == ' ')
			this.z = false;
		if (((char)paramInt == 'n') || ((char)paramInt == 'm'))
			this.A = false;
		if (((char)paramInt == 'N') || ((char)paramInt == 'M'))
			this.A = false;
		if ((char)paramInt == '{')
			this.t = false;
		if ((char)paramInt == '}')
			this.u = false;
		return true;
	}

	public final Graphics getGraphics()
	{
		if (this.h == null)
			return super.getGraphics();
		return this.h.getGraphics();
	}

	public void m_f()
	{
		try
		{
			byte[] arrayOfByte1 = f.b("jagex/jagex.dat");
			byte[] arrayOfByte2 = f.a("logo.tga", 0, arrayOfByte1);
			this.r = a(arrayOfByte2);
		}
		catch (IOException localIOException)
		{
			System.out.println("Error loading jagex.dat");
			return;
		}
	}

	public synchronized boolean mouseDown(Event paramEvent, int paramInt1, int paramInt2)
	{
		this.C = paramInt1;
		this.D = (paramInt2 + this.l);
		if (paramEvent.metaDown())
			this.E = 2;
		else
			this.E = 1;
		this.F = this.E;
		return true;
	}

	public Image a(byte[] paramArrayOfByte)
	{
		int i1 = paramArrayOfByte[13] * 256 + paramArrayOfByte[12];
		int i2 = paramArrayOfByte[15] * 256 + paramArrayOfByte[14];
		byte[] arrayOfByte1 = new byte[256];
		byte[] arrayOfByte2 = new byte[256];
		byte[] arrayOfByte3 = new byte[256];
		for (int i3 = 0; i3 < 256; i3++)
		{
			arrayOfByte1[i3] = paramArrayOfByte[(20 + i3 * 3)];
			arrayOfByte2[i3] = paramArrayOfByte[(19 + i3 * 3)];
			arrayOfByte3[i3] = paramArrayOfByte[(18 + i3 * 3)];
		}
		IndexColorModel localIndexColorModel = new IndexColorModel(8, 256, arrayOfByte1, arrayOfByte2, arrayOfByte3);
		byte[] arrayOfByte4 = new byte[i1 * i2];
		int i4 = 0;
		for (int i5 = i2 - 1; i5 >= 0; i5--)
			for (int i6 = 0; i6 < i1; i6++)
				arrayOfByte4[(i4++)] = paramArrayOfByte[(786 + i6 + i5 * i1)];
		MemoryImageSource localMemoryImageSource = new MemoryImageSource(i1, i2, localIndexColorModel, arrayOfByte4, 0, i1);
		Image localImage = createImage(localMemoryImageSource);
		return localImage;
	}

	public final void b(int paramInt1, int paramInt2)
	{
		if (this.h == null)
			return;
		this.h.resize(paramInt1, paramInt2);
		this.b = paramInt1;
		this.c = paramInt2;
	}

	public final void run()
	{
		if (this.a == 1)
		{
			this.a = 2;
			this.s = getGraphics();
			m_f();
			a(0);
			b();
			this.a = 0;
		}
		int i1 = 0;
		int i2 = 256;
		int i3 = 1;
		int i4 = 0;
	int i5 = 0;
		for (; i5 < 10; i5++)
			this.g[i5] = System.currentTimeMillis();
		long l1 = System.currentTimeMillis();
		while (this.j >= 0)
		{
			if (this.j > 0)
			{
				this.j += -1;
				if (this.j == 0)
					destroy();
				return;
			}
			i5 = i2;
			int i6 = i3;
			i2 = 300;
			i3 = 1;
			l1 = System.currentTimeMillis();
			if (this.g[i1] == 0L)
			{
				i2 = i5;
				i3 = i6;
			}
			else if (l1 > this.g[i1])
			{
				i2 = (int)(2560 * this.e / (l1 - this.g[i1]));
			}
			if (i2 < 25)
				i2 = 25;
			if (i2 > 256)
			{
				i2 = 256;
				i3 = (int)(this.e - (l1 - this.g[i1]) / 10L);
				if (i3 < this.B)
					i3 = this.B;
			}
			try
			{
				Thread.sleep(i3);
			}
			catch (InterruptedException localInterruptedException)
			{
			}
			this.g[i1] = l1;
			i1 = (i1 + 1) % 10;
			int i7 = 0;
			if (i3 > 1)
				for (i7 = 0; i7 < 10; i7++)
				{
					if (this.g[i7] == 0L)
						continue;
					this.g[i7] += i3;
				}
		 i7 = 0;
		 while (i4 < 256)
			{
				e();
				i4 += i2;
				i7++;
				if (i7 <= this.f_f)
					continue;
				i4 = 0;
				this.k += 6;
				if (this.k > 25)
				{
					this.k = 0;
					this.I = true;
				}
				break;
			}
			this.k += -1;
			i4 &= 255;
			c();
			this.N = (1000 * i2 / (this.e * 256));
			if (((this.i == true) && (i1 == 0)) && ((this.h == null) || ((this.h.a() == this.b) && (this.h.b() == this.c))))
				continue;
			a(this.h.a(), this.h.b());
		}
	}

	public final void c(int paramInt)
	{
		this.e = (1000 / paramInt);
	}

	public final void init()
	{
		this.i = true;
		System.out.println("Started applet");
		this.b = size().width;
		this.c = size().height;
		this.a = 1;
		f.a(this, true);
		this.d = new Thread(this);
		this.d.start();
	}

	public synchronized boolean mouseDrag(Event paramEvent, int paramInt1, int paramInt2)
	{
		this.C = paramInt1;
		this.D = (paramInt2 + this.l);
		if (paramEvent.metaDown())
			this.E = 2;
		else
			this.E = 1;
		return true;
	}

	public void a(Graphics paramGraphics, String paramString, Font paramFont, int paramInt1, int paramInt2)
	{
		FontMetrics localFontMetrics = f.a.getFontMetrics(paramFont);
		int i1 = localFontMetrics.stringWidth(paramString);
		paramGraphics.setFont(paramFont);
		paramGraphics.drawString(paramString, paramInt1 - localFontMetrics.stringWidth(paramString) / 2, paramInt2 + localFontMetrics.getHeight() / 4);
	}

	public synchronized boolean mouseMove(Event paramEvent, int paramInt1, int paramInt2)
	{
		this.C = paramInt1;
		this.D = (paramInt2 + this.l);
		this.E = 0;
		return true;
	}
}