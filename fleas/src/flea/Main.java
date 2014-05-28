package flea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.PrintStream;

public class Main extends a
{
	c O;
	int P = 0;
	int Q = 180;
	Image[] R;
	Image S;
	Image[] T = new Image[4];
	Image U;
	Image V;
	int W = 1;
	int X = -1;
	int Y = 0;
	Graphics Z;
	Graphics ab;
	Font bb;
	Font cb;
	Font db;
	Font eb;
	Font fb;
	Font gb;
	int hb = 28;
	int[] ib = new int[40];
	int[] jb = new int[40];
	int[] kb = new int[40];
	int[] lb = new int[40];
	int[] mb = new int[40];
	int[] nb = new int[40];
	int ob;
	int pb;
	int qb = 0;
	int rb = 0;
	int sb;
	int tb;
	int ub = 40;
	int vb;
	int wb = 40;
	int xb = 0;
	int yb = 1;
	int zb = 32;
	char[][] Ab = new char[40][21];
	int[][] Bb = new int[500][100]; //500 is probably more than needed
	int Cb = 0;
	int[] Db = new int[50];
	int[] Eb = new int[50];
	int[] Fb = new int[50];
	int[] Gb = new int[50];
	int[] Hb = new int[50];
	int Ib = 0;
	int Jb;
	int[] Kb = new int[10];
	int[] Lb = new int[10];
	int[] Mb = new int[10];
	int[] Nb = new int[10];
	int Ob;
	int[] Pb = new int[5];
	int[] Qb = new int[4];
	int[] Rb = new int[4];
	int[] Sb = new int[5];
	int[] Tb = new int[5];
	int[] Ub = new int[5];
	int[] Vb = new int[5];
	int Wb;
	int Xb;
	int Yb;
	int Zb;
	int ac = 0;
	int bc = 1;
	int cc = 4;
	int dc = 16;
	int ec = 0;
	int fc = 0;
	int gc;
	int hc = 0;
	int ic = 0;
	int[] jc = new int[1000];
	int[] kc = new int[1000];
	Image[] lc = new Image[50];
	Image[] mc = new Image[50];
	int nc;
	int[] oc = new int[50];
	int[] pc = new int[50];
	String[] qc = new String[50];
	char[] rc = new char[40000];
	Color sc = new Color(150, 150, 150);
	Color tc = new Color(200, 200, 200);
	Color uc = new Color(50, 50, 50);

	public void g()
	{
		int j = 357;
		int i = 114 + this.zb;
		this.Z.setColor(Color.black);
		this.Z.drawRect(i, j, 26, 26);
		this.Z.drawRect(i + 1, j + 1, 24, 24);
		i = 232 + this.zb;
		this.Z.setColor(Color.black);
		this.Z.drawRect(i, j, 26, 26);
		this.Z.drawRect(i + 1, j + 1, 24, 24);
		i = 349 + this.zb;
		this.Z.setColor(Color.black);
		this.Z.drawRect(i, j, 26, 26);
		this.Z.drawRect(i + 1, j + 1, 24, 24);
	}

	public void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
	{
		paramGraphics.setColor(this.uc);
		paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, paramInt3, paramInt4);
		paramGraphics.setColor(this.tc);
		paramGraphics.fillRect(paramInt1 - 1, paramInt2 - 1, paramInt3, paramInt4);
		paramGraphics.setColor(this.sc);
		paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
		this.Z.setColor(Color.black);
		a(paramGraphics, paramString, this.cb, paramInt1 + paramInt3 / 2, paramInt2 + paramInt4 / 2);
	}

	public void c(int paramInt1, int paramInt2)
	{
		if ((paramInt2 < 336) && (paramInt2 > 0) && (paramInt1 > 0) && (paramInt1 < 640))
		{
			int i = paramInt1 / 16;
			int j = paramInt2 / 16;
			int k = i * 4;
			int m = j * 4;
			for (paramInt1 = k; paramInt1 < k + 4; paramInt1++)
				for (paramInt2 = m; paramInt2 < m + 4; paramInt2++)
					if (this.Bb[paramInt1][paramInt2] != 0)
						return;
			d(5);
			this.Ab[i][j] = (char)(this.yb + 1);
			g(i, j);
			for (paramInt1 = k; paramInt1 < k + 4; paramInt1++)
				for (paramInt2 = m; paramInt2 < m + 4; paramInt2++)
				{
					if (this.yb == 1)
						this.Bb[paramInt1][paramInt2] = 1;
					if ((this.yb == 2) && (paramInt1 - k == 3 - (paramInt2 - m)))
						this.Bb[paramInt1][paramInt2] = 1;
					if ((this.yb != 3) || (paramInt1 - k != paramInt2 - m))
						continue;
					this.Bb[paramInt1][paramInt2] = 1;
				}
		}
	}

	public void d(int paramInt1, int paramInt2)
	{
		f(paramInt1, paramInt2);
		for (int i = 0; i < this.Cb; i++)
		{
			int i1 = this.Ab[this.Db[i]][this.Eb[i]];
			if ((i1 != this.Gb[i]) && (i1 != this.Hb[i]))
				continue;
			int j = this.Db[i] - paramInt1;
			if (j < 0)
				j = -j;
			int k = this.Eb[i] - paramInt2;
			if (k < 0)
				k = -k;
			if ((j >= 2) || (k >= 2))
				continue;
			int m = this.Db[i];
			int n = this.Eb[i];
			i1 = this.Gb[i];
			int i2 = this.Hb[i];
			this.Ab[m][n] = (char)i1;
			f(m, n);
			this.Z = this.lc[i].getGraphics();
			this.Z.drawImage(this.U, 0, 0, this);
			this.Ab[m][n] = (char)i2;
			f(m, n);
			this.Z = this.mc[i].getGraphics();
			this.Z.drawImage(this.U, 0, 0, this);
			this.Z = this.S.getGraphics();
		}
	}

	public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
	{
		int n = 0;
		int i;
		int j;
		int k;
		int m;
		if (this.Ab[paramInt1][paramInt2] == '\n')
		{
			d(2);
			for (i = paramInt1 - 1; i <= paramInt1 + 1; i++)
				for (j = paramInt2 - 1; j <= paramInt2 + 1; j++)
				{
					this.Ab[i][j] = 0;
					g(i, j);
					for (k = 0; k < 4; k++)
						for (m = 0; m < 4; m++)
							this.Bb[(i * 4 + k)][(j * 4 + m)] = 0;
				}
		}
		if (this.Ab[paramInt1][paramInt2] == '\005')
		{
			d(1);
			this.Ab[paramInt1][paramInt2] = 0;
			g(paramInt1, paramInt2);
			this.mb[paramInt4] = 1;
			for (k = 0; k < 4; k++)
				for (m = 0; m < 4; m++)
					this.Bb[(paramInt1 * 4 + k)][(paramInt2 * 4 + m)] = 0;
		}
		if ((this.Ab[paramInt1][paramInt2] == '\006') && (paramInt3 == 2))
		{
			this.mb[paramInt4] = 2;
			d(9);
		}
		if ((this.Ab[paramInt1][paramInt2] == '\007') && (paramInt3 == 2) && (this.ec == 0))
		{
			j = this.Jb;
			for (i = 0; i < this.Jb; i++)
			{
				if ((this.Kb[i] != paramInt1) || (this.Lb[i] != paramInt2))
					continue;
				j = i;
			}
			this.Nb[j] = 4;
			if (j == this.Jb)
			{
				this.Kb[j] = paramInt1;
				this.Lb[j] = paramInt2;
				this.Mb[j] = 0;
				this.Ab[paramInt1][(paramInt2 - 2)] = 30;
				g(paramInt1, paramInt2 - 2);
				this.Jb += 1;
				this.Z = this.S.getGraphics();
				this.Z.copyArea(paramInt1 * 16, (paramInt2 - 2) * 16 + this.Mb[j], 20, 20, j * 20 - paramInt1 * 16, 412 - (paramInt2 - 2) * 16 - this.Mb[j]);
			}
		}
		if ((this.Ab[paramInt1][paramInt2] == '\r') && (paramInt3 != 3))
		{
			this.lb[paramInt4] = 1;
			i();
		}
		if ((this.Ab[paramInt1][paramInt2] == '\033') && (paramInt3 != 3))
		{
			this.lb[paramInt4] = 1;
			i();
		}
		if ((this.Ab[paramInt1][paramInt2] == '\020') || (this.Ab[paramInt1][paramInt2] == '\035'))
		{
			int i1;
			int i2;
			if ((paramInt1 == this.Wb) && (paramInt2 == this.Xb))
			{
				i1 = this.Yb;
				i2 = this.Zb;
			}
			else
			{
				i1 = this.Wb;
				i2 = this.Xb;
			}
			if ((this.Ab[i1][i2] == '\020') || (this.Ab[i1][i2] == '\035'))
			{
				if ((this.kb[paramInt4] > 0) && (this.Bb[(i1 * 4 + 4)][(i2 * 4)] <= 0))
				{
					this.ib[paramInt4] = (i1 * 16 + 16);
					this.jb[paramInt4] = (i2 * 16);
					d(10);
				}
				if ((this.kb[paramInt4] < 0) && (this.Bb[(i1 * 4 - 1)][(i2 * 4)] <= 0))
				{
					this.ib[paramInt4] = (i1 * 16 - 4);
					this.jb[paramInt4] = (i2 * 16);
					d(10);
				}
			}
		}
		if (this.Ab[paramInt1][paramInt2] == '\021')
		{
			this.lb[paramInt4] = 1;
			this.xb += 1;
			d(8);
		}
		if ((this.Ab[paramInt1][paramInt2] == '\023') && (this.hc == 0))
		{
			d(0);
			this.bc = (-this.bc);
			this.cc = (-this.cc);
			this.dc = (-this.dc);
			this.ec = (4 - this.ec);
			this.hc = 3;
			if (paramInt3 < 2)
				this.kb[paramInt4] = (-this.kb[paramInt4]);
		}
		if ((this.Ab[paramInt1][paramInt2] == '\025') && (paramInt3 < 2))
		{
			d(3);
			this.Ab[paramInt1][paramInt2] = 0;
			g(paramInt1, paramInt2);
			this.kb[paramInt4] = (-this.kb[paramInt4]);
			for (k = 0; k < 4; k++)
				for (m = 0; m < 4; m++)
					this.Bb[(paramInt1 * 4 + k)][(paramInt2 * 4 + m)] = 0;
		}
		n = 0;
		if (this.Ab[paramInt1][paramInt2] == '\026')
			n = 1;
		if (this.Ab[paramInt1][paramInt2] == '\032')
			n = 1;
		if ((n == 1) && (paramInt3 == 2))
		{
			i = paramInt1 - this.kb[paramInt4] / 4;
			if (this.Ab[i][paramInt2] == '\026')
			{
				d(3);
				this.Ab[i][paramInt2] = 0;
				g(i, paramInt2);
				for (k = 0; k < 4; k++)
					for (m = 0; m < 4; m++)
						this.Bb[(i * 4 + k)][(paramInt2 * 4 + m)] = 0;
			}
		}
		if (this.Ab[paramInt1][paramInt2] == '\027')
		{
			d(4);
			for (i = 0; i < 40; i++)
				for (j = 0; j < 21; j++)
				{
					if ((this.Ab[i][j] != '\027') && (this.Ab[i][j] != '\024'))
						continue;
					this.Ab[i][j] = 0;
					g(i, j);
					for (k = 0; k < 4; k++)
						for (m = 0; m < 4; m++)
							this.Bb[(i * 4 + k)][(j * 4 + m)] = 0;
				}
		}
		if (this.Ab[paramInt1][paramInt2] == '\030')
		{
			d(4);
			for (i = 0; i < 40; i++)
				for (j = 0; j < 21; j++)
				{
					if (this.Ab[i][j] == '\030')
					{
						this.Ab[i][j] = 0;
						g(i, j);
						for (k = 0; k < 4; k++)
							for (m = 0; m < 4; m++)
								this.Bb[(i * 4 + k)][(j * 4 + m)] = 0;
					}
					if (this.Ab[i][j] != '\022')
						continue;
					this.Ab[i][j] = 2;
					g(i, j);
					for (k = 0; k < 4; k++)
						for (m = 0; m < 4; m++)
							this.Bb[(i * 4 + k)][(j * 4 + m)] = 1;
				}
		}
		if ((this.Ab[paramInt1][paramInt2] == '\031') && (paramInt3 != 3))
		{
			this.lb[paramInt4] = 1;
			i();
		}
	}

	public Image[] a(String paramString, int paramInt1, int paramInt2)
	{
		Image localImage = f.a(paramString);
		int i = localImage.getWidth(this);
		int j = localImage.getHeight(this);
		int[] arrayOfInt1 = new int[i * j];
		PixelGrabber localPixelGrabber = new PixelGrabber(localImage, 0, 0, i, j, arrayOfInt1, 0, i);
		try
		{
			localPixelGrabber.grabPixels();
		}
		catch (InterruptedException localInterruptedException)
		{
			System.out.println("Error!");
		}
		int k = 0;
		int m = 0;
		int n = i / paramInt1 * (j / paramInt2);
		Image[] arrayOfImage = new Image[n];
		for (int i1 = 0; i1 < n; i1++)
		{
			int i2 = 0;
			int[] arrayOfInt2 = new int[paramInt1 * paramInt2];
			for (int i3 = m; i3 < m + paramInt2; i3++)
				for (int i4 = k; i4 < k + paramInt1; i4++)
					arrayOfInt2[(i2++)] = arrayOfInt1[(i4 + i3 * i)];
			arrayOfImage[i1] = createImage(new MemoryImageSource(paramInt1, paramInt2, arrayOfInt2, 0, paramInt1));
			while (!prepareImage(arrayOfImage[i1], this));
			k += paramInt1;
			if (k < i)
				continue;
			k = 0;
			m += paramInt2;
		}
		return arrayOfImage;
	}

	public boolean mouseUp(Event paramEvent, int paramInt1, int paramInt2)
	{
		this.ob = paramInt1;
		this.pb = paramInt2;
		this.qb = 0;
		return true;
	}

	public void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
	{
		this.Db[this.Cb] = paramInt1;
		this.Eb[this.Cb] = paramInt2;
		this.Gb[this.Cb] = paramInt3;
		this.Hb[this.Cb] = paramInt4;
		this.Fb[this.Cb] = 0;
		this.Ab[paramInt1][paramInt2] = (char)paramInt3;
		f(paramInt1, paramInt2);
		this.Z = this.lc[this.Cb].getGraphics();
		this.Z.drawImage(this.U, 0, 0, this);
		this.Ab[paramInt1][paramInt2] = (char)paramInt4;
		f(paramInt1, paramInt2);
		this.Z = this.mc[this.Cb].getGraphics();
		this.Z.drawImage(this.U, 0, 0, this);
		this.Cb += 1;
	}

	public void e()
	{
		this.Z = this.S.getGraphics();
		Graphics localGraphics = getGraphics();
		if (this.W == 10)
		{
			if (this.W != this.X)
			{
				this.Z.setColor(Color.black);
				this.Z.fillRect(0, 0, 650, 400);
				this.Z.drawImage(this.V, 64, 63, this);
				a(this.Z, "Congratulations", this.fb, 325, 130);
				a(this.Z, "You have completed", this.fb, 325, 200);
				a(this.Z, "Flea Circus", this.fb, 325, 270);
				this.X = this.W;
			}
			localGraphics.drawImage(this.S, 0, 0, this);
			return;
		}
		if (this.W > 0)
		{
			if (this.W != this.X)
			{
				this.Z.setColor(Color.black);
				this.Z.fillRect(0, 0, 650, 400);
				this.Z.drawImage(this.V, 64, 63, this);
				a(this.Z, "Flea Circus", this.db, 325, 150);
				a(this.Z, "Programming: Andrew Gower", this.gb, 325, 210);
				a(this.Z, "Level Design: Paul Gower", this.gb, 325, 230);
				this.X = this.W;
			}
			if (this.W == 1)
			{
				a(this.Z, 202, 279, 100, 30, "Start New Game");
				a(this.Z, 345, 279, 100, 30, "Continue Game");
				if (this.rb == 1)
				{
					if ((this.ob > 202) && (this.pb > 279) && (this.ob < 302) && (this.pb < 309))
					{
						this.gc = 0;
						k();
					}
					if ((this.ob > 345) && (this.pb > 279) && (this.ob < 445) && (this.pb < 309))
					{
						this.W = 2;
						this.J = "";
						this.K = "";
					}
					this.rb = 0;
				}
			}
			if (this.W == 2)
			{
				a(this.Z, 175, 279, 300, 30, "Enter password here: " + this.J + "*");
				if (this.K.length() > 0)
				{
					this.gc = 0;
					for (int i = 0; i < this.nc; i++)
					{
						if (!this.K.equalsIgnoreCase(this.qc[i]))
							continue;
						this.gc = i;
					}
					k();
				}
			}
			localGraphics.drawImage(this.S, 0, 0, this);
			return;
		}
		this.X = this.W;
		if (this.rb == 1)
		{
			if ((this.ob > 480) && (this.pb > 355) && (this.ob < 530) && (this.pb < 380))
			{
				this.Y = (1 - this.Y);
				this.qb = 0;
			}
			if ((this.ob > 540) && (this.pb > 355) && (this.ob < 580) && (this.pb < 380) && (this.Y == 0))
				this.W = 1;
			if ((this.ob > 590) && (this.pb > 355) && (this.ob < 630) && (this.pb < 380) && (this.Y == 0))
				k();
			this.rb = 0;
		}
		if (this.Y == 0)
			a(this.Z, 480, 355, 50, 25, "Pause");
		else
			a(this.Z, 480, 355, 50, 25, "Resume");
		a(this.Z, 540, 355, 40, 25, "Quit");
		a(this.Z, 590, 355, 40, 25, "Restart");
		if (this.Y == 1)
		{
			localGraphics.drawImage(this.S, 0, 0, this);
			return;
		}
		this.Ib = ((this.Ib + 1) % 8);
		int j;
		int n;
		int i1;
		for (int i = 0; i < this.Cb; i++)
		{
			j = this.Ab[this.Db[i]][this.Eb[i]];
			if ((j != this.Gb[i]) && (j != this.Hb[i]))
				continue;
			if (this.Fb[i] == 0)
			{
				if (this.Ib == 3)
					this.Z.drawImage(this.lc[i], this.Db[i] * 16, this.Eb[i] * 16, this);
				if (this.Ib == 7)
					this.Z.drawImage(this.mc[i], this.Db[i] * 16, this.Eb[i] * 16, this);
			}
			else
			{
				if (this.Gb[i] != 9)
					continue;
				n = this.Db[i] * 16;
				i1 = this.Eb[i] * 16;
				int i6 = 0;
				for (j = 0; j < this.ub; j++)
				{
					if ((this.lb[j] != 0) || (this.ib[j] < n) || (this.jb[j] < i1) || (this.ib[j] >= n + 16) || (this.jb[j] >= i1 + 16))
						continue;
					i6 = 1;
				}
				if (i6 == 1)
					this.Z.drawImage(this.lc[i], n, i1, this);
				else
					this.Z.drawImage(this.mc[i], n, i1, this);
			}
		}
		if (this.ub < this.wb)
		{
			this.vb += -1;
			if (this.vb < 1)
			{
				this.vb = 6;
				this.ub += 1;
			}
		}
		int k;
		int m;
		for (int i = 0; i < this.ub; i++)
		{
			if ((this.lb[i] == 0) && (this.mb[i] != 1) && (this.Ab[(this.ib[i] / 16)][(this.jb[i] / 16)] == '\002'))
			{
				this.lb[i] = 1;
				i();
				this.Z.copyArea(i * 16, 396, 4, 4, this.ib[i] + 4 - i * 16, this.jb[i] + this.fc - 396);
			}
			if (this.lb[i] != 0)
				continue;
			n = this.ib[i];
			i1 = this.jb[i];
			int i2 = this.kb[i];
			int i3 = n / 4;
			int i4 = i1 / 4;
			int i5 = i2 / 4;
			if (this.mb[i] != 1)
				this.Z.copyArea(i * 16, 396, 4, 4, n + 4 - i * 16, i1 + this.fc - 396);
			if (i2 < 0)
				this.nb[i] = (this.Ib % 2);
			else
				this.nb[i] = (2 + this.Ib % 2);
			int i7 = 0;
			for (j = 0; j < this.Ob; j++)
			{
				if ((n < this.Sb[j]) || (n >= this.Ub[j]) || (i1 < this.Tb[j]) || (i1 >= this.Vb[j]) || (this.Rb[j] != 0))
					continue;
				if (n / 16 > this.Pb[j])
				{
					this.kb[i] = 4;
					i2 = 4;
					i5 = 1;
				}
				else
				{
					this.kb[i] = -4;
					i2 = -4;
					i5 = -1;
				}
				i7 = 1;
			}
			if (this.mb[i] != 0)
			{
				if (this.mb[i] == 1)
				{
					this.Z.copyArea(i * 16, 396, 16, 20, n - 2 - i * 16, i1 - 16 - 396);
					i1 -= this.cc;
					k = n / 16;
					m = i1 / 16;
					if ((this.Ab[k][m] == '\013') && (i1 % 16 == 4))
					{
						this.mb[i] = 0;
						this.kb[i] = -4;
						i2 = -4;
					}
				}
				if (this.mb[i] == 2)
				{
					this.Bb[i3][i4] = 0;
					if (this.Bb[i3][(i4 - this.bc)] == 2)
						a(i3 / 4, (i4 - 1) / 4, 3, i);
					n = this.ib[i];
					i1 = this.jb[i];
					i2 = this.kb[i];
					i3 = n / 4;
					i4 = i1 / 4;
					i5 = i2 / 4;
					i1 -= this.cc;
					i4 -= this.bc;
					if ((this.Bb[i3][i4] > 0) || (i7 != 0))
					{
						i1 += this.cc;
						i4 += this.bc;
						this.mb[i] = 0;
					}
					this.Bb[i3][i4] = 1;
				}
			}
			else
			{
				this.Bb[i3][i4] = 0;
				if ((this.Bb[i3][(i4 + this.bc)] == 2) && (i7 == 0))
					a(i3 / 4, (i4 + this.bc) / 4, 2, i);
				if ((this.Bb[i3][(i4 - this.bc)] == 2) && (i7 == 0))
					a(i3 / 4, (i4 - this.bc) / 4, 3, i);
				if (((this.Bb[i3][(i4 + this.bc)] > 0) || (i7 != 0)) && (this.Bb[(i3 + i5)][i4] == 2))
					a((i3 + i5) / 4, i4 / 4, i5, i);
				n = this.ib[i];
				i1 = this.jb[i];
				i2 = this.kb[i];
				i3 = n / 4;
				i4 = i1 / 4;
				i5 = i2 / 4;
				if (this.mb[i] != 2)
					if ((this.Bb[i3][(i4 + this.bc)] <= 0) && (i7 == 0))
					{
						i1 += this.cc;
					}
					else if ((this.Bb[(i3 + i5)][i4] > 0) && (this.Bb[i3][(i4 - this.bc)] <= 0) && (this.Bb[(i3 + i5)][(i4 - this.bc)] <= 0))
					{
						n += i2;
						i1 -= this.cc;
					}
					else if (this.Bb[(i3 + i5)][i4] <= 0)
					{
						n += i2;
					}
					else
					{
						i2 = -i2;
					}
				i3 = n / 4;
				i4 = i1 / 4;
				if ((this.mb[i] != 1) && (this.lb[i] == 0))
					this.Bb[i3][i4] = 1;
			}
			this.ib[i] = n;
			this.jb[i] = i1;
			this.kb[i] = i2;
		}
		for (int i = 0; i < this.Jb; i++)
		{
			k = this.Kb[i];
			m = this.Lb[i];
			n = k * 16;
			i1 = (m - 2) * 16 + this.Mb[i];
			for (j = 0; j < this.ub; j++)
			{
				if ((this.ib[j] < n - 4) || (this.ib[j] >= n + 20) || (this.jb[j] < i1) || (this.jb[j] >= i1 + 20))
					continue;
				if (this.lb[j] == 0)
				{
					this.lb[j] = 1;
					i();
				}
				this.Z.copyArea(j * 16, 396, 4, 4, this.ib[j] + 4 - j * 16, this.jb[j] - 396);
				this.Bb[(this.ib[j] / 4)][(this.jb[j] / 4)] = 0;
			}
			this.Z.copyArea(i * 20, 412, 20, 20, k * 16 - i * 20, (m - 2) * 16 + this.Mb[i] - 412);
			this.Mb[i] += this.Nb[i];
			if (this.Mb[i] >= 16)
			{
				this.Mb[i] = 16;
				this.Nb[i] = -4;
			}
			if (this.Mb[i] != 0)
				continue;
			this.Ab[k][(m - 2)] = 15;
			g(k, m - 2);
			for (j = i; j < this.Jb; j++)
			{
				this.Kb[j] = this.Kb[(j + 1)];
				this.Lb[j] = this.Lb[(j + 1)];
				this.Mb[j] = this.Mb[(j + 1)];
				this.Nb[j] = this.Nb[(j + 1)];
				this.Z.copyArea((j + 1) * 20, 412, 20, 20, -20, 0);
			}
			this.Jb += -1;
		}
		for (int i = 0; i < this.ic; i++)
			d(this.jc[i], this.kc[i]);
		this.ic = 0;
		this.fc = this.ec;
		if (this.hc > 0)
			this.hc += -1;
		for (int i = 0; i < this.wb; i++)
			if (this.mb[i] == 1)
			{
				this.Z.copyArea(this.ib[i] - 2, this.jb[i] - 16, 16, 20, i * 16 - (this.ib[i] - 2), 396 - (this.jb[i] - 16));
			}
			else
			{
				if (this.lb[i] != 0)
					continue;
				this.Z.copyArea(this.ib[i] + 4, this.jb[i] + this.fc, 4, 4, i * 16 - (this.ib[i] + 4), 396 - (this.jb[i] + this.fc));
			}
		for (int i = 0; i < this.Jb; i++)
		{
			k = this.Kb[i];
			m = this.Lb[i];
			this.Z.copyArea(k * 16, (m - 2) * 16 + this.Mb[i], 20, 20, i * 20 - k * 16, 412 - (m - 2) * 16 - this.Mb[i]);
		}
		for (int i = 0; i < this.ub; i++)
			if (this.mb[i] == 1)
			{
				this.Z.drawImage(this.R[5], this.ib[i] - 4, this.jb[i] - 16, this);
				this.Z.drawImage(this.T[this.nb[i]], this.ib[i] + 4, this.jb[i], this);
			}
			else
			{
				if (this.lb[i] != 0)
					continue;
				this.Z.drawImage(this.T[this.nb[i]], this.ib[i] + 4, this.jb[i] + this.fc, this);
			}
		for (int i = 0; i < this.Jb; i++)
		{
			k = this.Kb[i];
			m = this.Lb[i];
			this.Z.drawImage(this.R[15], k * 16, (m - 2) * 16 + this.Mb[i], this);
		}
		localGraphics.drawImage(this.S, 0, 0, this);
		if (this.qb == 1)
			c(this.ob, this.pb);
		if (this.xb == this.pc[this.gc])
		{
			this.gc += 1;
			if (this.gc == this.nc)
				this.W = 10;
			else
				k();
		}
	}

	public void h()
	{
		this.Z = this.S.getGraphics();
		for (int i = 0; i < 40; i++)
		{
			this.ib[i] = this.sb;
			this.jb[i] = this.tb;
			this.kb[i] = -4;
			this.lb[i] = 0;
			this.mb[i] = 0;
			this.ub = 0;
			this.vb = 200;
			this.nb[i] = 0;
			this.Bb[(this.ib[i] / 4)][(this.jb[i] / 4)] = 1;
			this.Z.copyArea(this.ib[i] + 4, this.jb[i], 4, 4, i * 16 - (this.ib[i] + 4), 396 - this.jb[i]);
		}
	}

	public void e(int paramInt1, int paramInt2)
	{
		this.Z.setColor(Color.blue);
		this.Z.drawRect(paramInt1, paramInt2, 26, 26);
		this.Z.setColor(new Color(0, 132, 255));
		this.Z.drawRect(paramInt1 + 1, paramInt2 + 1, 24, 24);
	}

	public void f(int paramInt1, int paramInt2)
	{
		this.Z = this.S.getGraphics();
		this.ab = this.U.getGraphics();
		this.ab.setColor(Color.black);
		this.ab.fillRect(0, 0, 20, 20);
		this.ab.drawImage(this.R[this.Ab[(paramInt1 + 1)][(paramInt2 + 1)]], 16, 16, this);
		this.ab.drawImage(this.R[this.Ab[paramInt1][(paramInt2 + 1)]], 0, 16, this);
		this.ab.drawImage(this.R[this.Ab[(paramInt1 - 1)][(paramInt2 + 1)]], -16, 16, this);
		this.ab.drawImage(this.R[this.Ab[(paramInt1 + 1)][paramInt2]], 16, 0, this);
		this.ab.drawImage(this.R[this.Ab[paramInt1][paramInt2]], 0, 0, this);
		this.ab.drawImage(this.R[this.Ab[(paramInt1 - 1)][paramInt2]], -16, 0, this);
		this.ab.drawImage(this.R[this.Ab[(paramInt1 + 1)][(paramInt2 - 1)]], 16, -16, this);
		this.ab.drawImage(this.R[this.Ab[paramInt1][(paramInt2 - 1)]], 0, -16, this);
		this.ab.drawImage(this.R[this.Ab[(paramInt1 - 1)][(paramInt2 - 1)]], -16, -16, this);
		this.Z.drawImage(this.U, paramInt1 * 16, paramInt2 * 16, this);
	}

	public static void main(String[] paramArrayOfString)
	{
		Main localfleas = new Main();
		localfleas.fromMain(644, 390, "Flea circus - By Andrew Gower", false);
	}

	public void b()
	{
		c(40);
		this.bb = new Font("Helvetica", 0, 13);
		this.cb = new Font("Helvetica", 0, 13);
		this.db = new Font("Helvetica", 0, 90);
		this.eb = new Font("Helvetica", 0, 36);
		this.fb = new Font("Helvetica", 0, 50);
		this.gb = new Font("Helvetica", 1, 20);
		a(10);
		this.R = a("blocks.gif", 20, 20);
		a(20);
		this.T = a("fleas.gif", 4, 4);
		a(30);
		this.V = f.a("title.gif");
		a(50);
		byte[] arrayOfByte2 = null;
		byte[] arrayOfByte3 = null;
		try
		{
			byte[] arrayOfByte1 = f.b("fleas.jag");
			arrayOfByte2 = f.a("sfx.xm", 1000, arrayOfByte1);
			arrayOfByte3 = f.a("levels.lev", 0, arrayOfByte1);
		}
		catch (Exception localException)
		{
			System.out.println("Fatal error loading fleas.jag");
		}
		a(60);
		this.O = new c(arrayOfByte2);
		this.O.a();
		a(70);
		b(arrayOfByte3);
		a(100);
		this.S = createImage(644, 440);
		this.U = createImage(20, 20);
		for (int i = 0; i < 50; i++)
		{
			this.lc[i] = createImage(20, 20);
			this.mc[i] = createImage(20, 20);
		}
		this.W = 1;
		this.gc = 0;
	}

	public void d(int paramInt)
	{
		this.O.a(this.P, paramInt, 8000, 63);
		this.P = (1 - this.P);
	}

	public void i()
	{
		int i = (int)(Math.random() * 128.0D);
		if (i > 64)
			d(6);
		else
			d(7);
	}

	public void b(byte[] paramArrayOfByte)
	{
		int i = 0;
		this.nc = (paramArrayOfByte[(i++)] & 0xFF);
		for (int j = 0; j < this.nc; j++)
		{
			byte[] arrayOfByte = new byte[20];
			for (int k = 0; k < 20; k++)
				arrayOfByte[k] = paramArrayOfByte[(i++)];
			this.qc[j] = new String(arrayOfByte).trim();
		}
		for (j = 0; j < this.nc; j++)
			this.oc[j] = (paramArrayOfByte[(i++)] & 0xFF);
		for (j = 0; j < this.nc; j++)
			this.pc[j] = (paramArrayOfByte[(i++)] & 0xFF);
		for (j = 0; j < this.nc * 840; j++)
			this.rc[j] = (char)(paramArrayOfByte[(i++)] & 0xFF);
	}

	public void j()
	{
		for (int i = 0; i < this.Ob; i++)
		{
			int k = this.Pb[i];
			int m = this.Qb[i];
			int n = k * 16;
			int i1 = m * 16;
			int i2 = k * 16 + 20;
			int i3 = m * 16 + 16;
			if ((this.Ab[k][m] != '\016') && (this.Ab[k][m] != '\034'))
				this.Rb[i] = 1;
			int j = k - 1;
			while (this.Ab[j][m] == 0)
			{
				j--;
				n -= 16;
			}
			j = k + 1;
			while (this.Ab[j][m] == 0)
			{
				j++;
				i2 += 16;
			}
			this.Sb[i] = n;
			this.Tb[i] = i1;
			this.Ub[i] = i2;
			this.Vb[i] = i3;
		}
	}

	public void k()
	{
		l();
		m();
		h();
		this.W = 0;
	}

	public boolean mouseDown(Event paramEvent, int paramInt1, int paramInt2)
	{
		this.ob = paramInt1;
		this.pb = paramInt2;
		this.qb = 1;
		this.rb = 1;
		if ((this.Y == 0) && (this.W == 0))
		{
			this.Z = this.S.getGraphics();
			this.ab = this.U.getGraphics();
			if ((paramInt1 > 100 + this.zb) && (paramInt1 < 160 + this.zb) && (paramInt2 > 350))
			{
				g();
				this.yb = 1;
				e(114 + this.zb, 357);
			}
			if ((paramInt1 > 210 + this.zb) && (paramInt1 < 280 + this.zb) && (paramInt2 > 350))
			{
				g();
				this.yb = 2;
				e(232 + this.zb, 357);
			}
			if ((paramInt1 > 320 + this.zb) && (paramInt1 < 390 + this.zb) && (paramInt2 > 350))
			{
				g();
				this.yb = 3;
				e(349 + this.zb, 357);
			}
			c(this.ob, this.pb);
		}
		return true;
	}

	public void g(int paramInt1, int paramInt2)
	{
		this.jc[this.ic] = paramInt1;
		this.kc[this.ic] = paramInt2;
		this.ic += 1;
		j();
	}

	public void l()
	{
		this.Cb = 0;
		this.xb = 0;
		this.Jb = 0;
		this.ac = 0;
		this.Ob = 0;
		this.Y = 0;
		this.bc = 1;
		this.cc = 4;
		this.dc = 16;
		this.ec = 0;
		this.fc = 0;
		this.hc = 0;
		this.wb = this.oc[this.gc];
		int j;
		for (int i = 0; i < 40; i++)
			for (j = 0; j < 21; j++)
				this.Ab[i][j] = this.rc[(this.gc * 840 + i * 21 + j)];
		for (int i = 0; i < 40; i++)
			for (j = 0; j < 21; j++)
			{
				if (this.Ab[i][j] == '\017')
					this.Ab[i][(j + 2)] = 7;
				if (this.Ab[i][j] == '\001')
				{
					this.sb = (i * 16 + 8);
					this.tb = (j * 16 + 16);
				}
				if (this.Ab[i][j] == '\b')
				{
					b(i, j, 9, 8);
					this.Fb[(this.Cb - 1)] = 1;
				}
				if (this.Ab[i][j] == '\r')
					b(i, j, 13, 27);
				if (this.Ab[i][j] == '\016')
				{
					b(i, j, 14, 28);
					this.Pb[this.Ob] = i;
					this.Qb[this.Ob] = j;
					this.Rb[this.Ob] = 0;
					this.Ob += 1;
				}
				if (this.Ab[i][j] != '\020')
					continue;
				if (this.ac == 0)
				{
					this.Wb = i;
					this.Xb = j;
					this.ac = 1;
				}
				else
				{
					this.Yb = i;
					this.Zb = j;
					this.ac = 2;
				}
				b(i, j, 16, 29);
			}
		j();
	}

	public boolean mouseDrag(Event paramEvent, int paramInt1, int paramInt2)
	{
		this.ob = paramInt1;
		this.pb = paramInt2;
		return true;
	}

	public void m()
	{
		this.Z = this.S.getGraphics();
		this.Z.setColor(Color.black);
		this.Z.fillRect(0, 0, 644, 400);
		for (int i = 39; i >= 0; i--)
			for (int j = 20; j >= 0; j--)
			{
				int n = this.Ab[i][j];
				if (n != 0)
					this.Z.drawImage(this.R[n], i * 16, j * 16, this);
				for (int k = 0; k < 4; k++)
					for (int m = 0; m < 4; m++)
						if (n == 0)
							this.Bb[(i * 4 + k)][(j * 4 + m)] = 0;
						else if (n == 1)
							this.Bb[(i * 4 + k)][(j * 4 + m)] = 1;
						else if (n == 3)
						{
							if (k == 3 - m)
								this.Bb[(i * 4 + k)][(j * 4 + m)] = 1;
							else
								this.Bb[(i * 4 + k)][(j * 4 + m)] = 0;
						}
						else if (n == 4)
						{
							if (k == m)
								this.Bb[(i * 4 + k)][(j * 4 + m)] = 1;
							else
								this.Bb[(i * 4 + k)][(j * 4 + m)] = 0;
						}
						else if (n == 8)
							this.Bb[(i * 4 + k)][(j * 4 + m)] = -1;
						else if (n == 18)
							this.Bb[(i * 4 + k)][(j * 4 + m)] = -1;
						else if (n == 11)
						{
							if (m == 3)
								this.Bb[(i * 4 + k)][(j * 4 + m)] = 1;
							else
								this.Bb[(i * 4 + k)][(j * 4 + m)] = 0;
						}
						else
							this.Bb[(i * 4 + k)][(j * 4 + m)] = 2;
			}
		this.Z.drawImage(this.R[2], 117 + this.zb, 360, this);
		this.Z.drawImage(this.R[3], 235 + this.zb, 360, this);
		this.Z.drawImage(this.R[4], 352 + this.zb, 360, this);
		e(114 + this.zb, 357);
		this.yb = 1;
		this.Z.setFont(this.cb);
		this.Z.setColor(Color.white);
		this.Z.drawString("Password: " + this.qc[this.gc], 10, 357);
		this.Z.drawString("Fleas: " + String.valueOf(this.oc[this.gc]), 10, 371);
		this.Z.drawString("Rescue: " + String.valueOf(this.pc[this.gc]), 10, 385);
	}
}
