package flea;

import java.io.InputStream;
public class c extends InputStream
{
	byte[] a = new byte[131072];
	byte[] b;
	int[] c = new int[2000]; //probably more than needed
	int[] d = { 0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 };
	int e = 0;
	int f = 0;
	int g = 0;
	int h;
	int i;
	int j;
	int k;
	int l;
	int m;
	int n;
	int o = 0;
	int p;
	int q;
	int r;
	int s;
	int t;
	int u;
	int v;
	int w;
	int x;
	int y;
	int z;
	int A;
	int B;
	int C = 0;
	int D = 0;
	int[] E = new int[256];
	int[] F = new int[256];
	int[] G = new int[256];
	int[] H = new int[256];
	int[] I = new int[256];
	int[] J = new int[256];
	byte[] K = new byte[256];
	byte[] L = new byte[256];
	int[] M = new int[8000];
	int[] N = new int[68];
	int[] O = new int[64];
	int[] P = new int[64];
	int[] Q = new int[64];
	int[] R = new int[64];
	int[] S = new int[256];
	int[] T = new int[256];
	int U;
	int[] V = new int[64];
	int[] W = new int[64];
	int[] X = new int[64];
	int[] Y = new int[64];
	int[] Z = new int[64];
	int[] ab = new int[64];
	int[] bb = new int[64];
	int[] cb = new int[64];
	int[] db = new int[64];
	int[] eb = new int[64];
	int[] fb = new int[64];
	int[] gb = new int[64];
	int[] hb = new int[64];
	int[] ib = new int[64];
	int[] jb = new int[64];
	int[] kb = new int[64];
	int[] lb = new int[64];
	int[] mb = new int[64];
	int[] nb = new int[64];
	int[] ob = new int[64];
	int[] pb = new int[64];
	int[] qb = new int[256];
	int[] rb = new int[256];
	int[] sb = new int[256];
	int[] tb = new int[256];
	int[][] ub = new int[256][12];
	int[][] vb = new int[256][12];
	int[] wb = new int[256];
	int[] xb = new int[256];
	boolean[] yb = new boolean[64];
	byte[][] zb = new byte[256][96];
	int[] Ab = new int[32];
	int[] Bb = new int[32];
	int[] Cb = new int[32];
	int[] Db = new int[32];
	int[] Eb = new int[32];
	int Fb = 1;
	int Gb = 0;
	int Hb = 0;
	int Ib = -1;
	int Jb = 0;
	int Kb = 0;
	int Lb = 64;

	void a(int paramInt)
	{
		int i1 = 0;
		int i5 = 0;
		if ((this.o != 1) && (this.o != 2))
		{
			this.e = 0;
			this.g = 0;
			this.f = 0;
			return;
		}
		this.e += paramInt * this.n;
		if ((this.f == 0) && (this.e >= 20000))
		{
			if (this.o == 1)
			{
				this.o = 2;
				this.r = 0;
				this.q = 0;
				this.s = this.I[this.r];
				this.U = this.S[this.s];
				this.p = 0;
			}
			if ((this.q >= this.E[this.s]) || (this.Jb == 1) || (this.Gb == 1))
			{
				if (this.Gb == 1)
				{
					this.r = this.Hb;
					this.C = 1;
				}
				else
				{
					this.r += 1;
				}
				if (this.Ib != -1)
				{
					this.r = this.Ib;
					this.Ib = -1;
				}
				if (this.r >= this.h)
				{
					this.r = 0;
					this.C = 1;
				}
				this.q = 0;
				this.s = this.I[this.r];
				this.U = this.S[this.s];
				this.p = 0;
				if (this.Kb > 0)
				{
					this.Jb = 1;
					this.Hb = this.Kb;
					this.Kb = 0;
				}
				if (this.Jb == 1)
				{
					this.Hb = (this.Hb / 16 * 10 + this.Hb % 16);
					if (this.Hb >= this.E[this.s])
						this.Hb = 0;
					this.q = this.Hb;
					for (int i12 = 0; i12 < this.Hb; i12++)
					{
						this.z = 0;
						while (this.z < this.i)
						{
							this.t = (this.b[(this.U + this.p)] & 0xFF);
							this.p += 1;
							if (this.t < 128)
							{
								this.p += -1;
								this.t = 31;
							}
							else
							{
								this.t -= 128;
							}
							if ((this.t & 0x1) == 1)
							{
								this.u = (this.b[(this.U + this.p)] & 0xFF);
								this.p += 1;
							}
							if ((this.t & 0x2) == 2)
							{
								this.v = ((this.b[(this.U + this.p)] & 0xFF) - 1);
								this.p += 1;
							}
							if ((this.t & 0x4) == 4)
							{
								this.w = ((this.b[(this.U + this.p)] & 0xFF) - 16);
								this.p += 1;
							}
							if ((this.t & 0x8) == 8)
							{
								this.x = (this.b[(this.U + this.p)] & 0xFF);
								this.p += 1;
							}
							if ((this.t & 0x10) == 16)
							{
								this.y = (this.b[(this.U + this.p)] & 0xFF);
								this.p += 1;
							}
							this.z += 1;
						}
					}
				}
				this.Ib = -1;
				this.Jb = 0;
				this.Gb = 0;
			}
			this.q += 1;
			this.z = 0;
			while (this.z < this.i)
			{
				this.u = -1;
				this.v = -1;
				this.w = -1;
				this.x = -1;
				this.y = 0;
				this.t = (this.b[(this.U + this.p)] & 0xFF);
				this.p += 1;
				if (this.t < 128)
				{
					this.p += -1;
					this.t = 31;
				}
				else
				{
					this.t -= 128;
				}
				if ((this.t & 0x1) == 1)
				{
					this.u = (this.b[(this.U + this.p)] & 0xFF);
					this.p += 1;
				}
				if ((this.t & 0x2) == 2)
				{
					this.v = ((this.b[(this.U + this.p)] & 0xFF) - 1);
					this.p += 1;
				}
				if ((this.t & 0x4) == 4)
				{
					this.w = ((this.b[(this.U + this.p)] & 0xFF) - 16);
					this.p += 1;
				}
				if ((this.t & 0x8) == 8)
				{
					this.x = (this.b[(this.U + this.p)] & 0xFF);
					this.p += 1;
				}
				if ((this.t & 0x10) == 16)
				{
					this.y = (this.b[(this.U + this.p)] & 0xFF);
					this.p += 1;
				}
				if (!this.yb[this.z])
				{
					if ((this.w >= 80) && (this.w < 96))
					{
						this.pb[this.z] = 2;
						this.V[this.z] = (80 - this.w);
						this.w = -1;
					}
					if ((this.w >= 96) && (this.w < 112))
					{
						this.pb[this.z] = 2;
						this.V[this.z] = (this.w - 96);
						this.w = -1;
					}
					if ((this.w >= 112) && (this.w < 128))
					{
						this.w = (this.O[this.z] - (this.w - 112));
						if (this.w < 0)
							this.w = 0;
						else if (this.w > 63)
							this.w = 63;
					}
					if ((this.w >= 128) && (this.w < 136))
					{
						this.w = (this.O[this.z] + (this.w - 128));
						if (this.w < 0)
							this.w = 0;
						else if (this.w > 63)
							this.w = 63;
					}
					if (this.w > 64)
						this.w = -1;
					if (this.x == 13)
					{
						this.Jb = 1;
						this.Hb = this.y;
					}
					if (this.x == 15)
						if (this.y < 32)
							this.m = this.y;
						else
							this.n = this.y;
					if (this.x == 12)
						this.w = this.y;
					if (this.x == 14)
					{
						this.x = (this.x * 16 + this.y / 16);
						this.y &= 15;
					}
					if ((this.x == 236) && (this.y < this.m))
						this.u = 97;
					if (this.x == 11)
					{
						this.Gb = 1;
						this.Hb = this.y;
					}
					if (this.x == 20)
						this.u = 97;
					if (this.x == 21)
					{
						this.kb[this.z] = this.y;
						if (this.kb[this.z] >= this.ub[i5][(this.qb[i5] - 1)])
							this.kb[this.z] = (this.ub[i5][(this.qb[i5] - 1)] - 1);
					}
					if ((this.v >= 0) && (this.u <= 96))
					{
						this.kb[this.z] = 0;
						this.mb[this.z] = 1;
						this.nb[this.z] = 0;
					}
					if ((this.x == 3) && (this.w < 0) && (this.v != -1))
						this.w = this.G[this.Q[this.z]];
					if ((this.u >= 0) && (this.u <= 96) && (this.x != 3))
					{
						if ((this.v == -1) && (this.w < 0))
							this.w = this.O[this.z];
						if (this.v == -1)
						{
							this.v = this.Q[this.z];
						}
						else
						{
							this.R[this.z] = this.v;
							if (this.u < 96)
								this.v = this.zb[this.v][this.u];
							else
								this.v = this.zb[this.v][95];
						}
						if (this.w < 0)
							this.w = this.G[this.v];
						this.B = (7680 - (this.u + this.K[this.v]) * 16 * 4 - this.L[this.v] / 2);
						if (this.B < 500)
							this.B = 500;
						else if (this.B > 7999)
							this.B = 7999;
						this.ib[this.z] = this.B;
						this.A = this.M[this.B];
						this.Q[this.z] = this.v;
						this.O[this.z] = this.w;
						this.P[this.z] = this.B;
						int i2 = this.T[this.v];
						int i3 = this.F[this.v];
						if (this.x == 9)
							if (this.y * 256 > this.F[this.v])
							{
								i2 += this.F[this.v];
								i3 = 0;
							}
							else
							{
								i2 += this.y * 256;
								i3 -= this.y * 256;
							}
						a(this.z, i2, this.A, this.w * this.Lb / 64, i3, this.H[this.v]);
						this.Y[this.z] = 0;
					}
					else if (this.u > 96)
					{
						if (this.lb[this.z] == 1)
							this.mb[this.z] = 0;
						else
							b(this.z);
					}
					else if (this.w >= 0)
					{
						b(this.z, this.w * this.Lb / 64);
						this.O[this.z] = this.w;
					}
					if (this.x == 3)
					{
						this.ob[this.z] = 1;
						if ((this.u >= 0) && (this.u <= 96))
						{
							this.v = this.Q[this.z];
							this.B = (7680 - (this.u + this.K[this.v]) * 16 * 4 - this.L[this.v] / 2);
							if (this.B < 500)
								this.B = 500;
							else if (this.B > 7999)
								this.B = 7999;
							this.ib[this.z] = this.B;
						}
						if (this.y != 0)
						{
							this.jb[this.z] = this.y;
							if (this.l == 0)
								this.jb[this.z] *= 2;
						}
					}
					else if (this.x != 5)
					{
						this.ob[this.z] = 0;
					}
					if (this.x == 4)
					{
						this.Z[this.z] = 1;
						if (this.y / 16 > 0)
							this.W[this.z] = (this.y / 16);
						if ((this.y & 0xF) > 0)
							this.X[this.z] = (this.y & 0xF);
					}
					else if (this.x != 6)
					{
						if (this.Z[this.z] != 0)
						{
							this.A = this.M[this.P[this.z]];
							a(this.z, this.A);
						}
						this.Y[this.z] = 0;
						this.Z[this.z] = 0;
					}
					if ((this.x == 10) || (this.x == 6) || (this.x == 5))
					{
						this.pb[this.z] = 1;
						if (this.y != 0)
							this.V[this.z] = ((this.y & 0xF0) / 16 - (this.y & 0xF));
					}
					else if (this.pb[this.z] == 2)
					{
						this.pb[this.z] = 1;
					}
					else
					{
						this.pb[this.z] = 0;
					}
					if (this.x == 234)
					{
						if (this.y == 0)
							this.y = this.ab[this.z];
						else
							this.ab[this.z] = this.y;
						this.O[this.z] += this.y;
						if (this.O[this.z] < 0)
							this.O[this.z] = 0;
						else if (this.O[this.z] > 63)
							this.O[this.z] = 63;
						b(this.z, this.O[this.z] * this.Lb / 64);
					}
					if (this.x == 235)
					{
						if (this.y == 0)
							this.y = this.db[this.z];
						else
							this.db[this.z] = this.y;
						this.O[this.z] -= this.y;
						if (this.O[this.z] < 0)
							this.O[this.z] = 0;
						else if (this.O[this.z] > 63)
							this.O[this.z] = 63;
						b(this.z, this.O[this.z] * this.Lb / 64);
					}
					if (this.x == 1)
					{
						if (this.y != 0)
							this.gb[this.z] = this.y;
					}
					else
						this.gb[this.z] = 0;
					if (this.x == 2)
					{
						if (this.y != 0)
							this.hb[this.z] = this.y;
					}
					else
						this.hb[this.z] = 0;
					if (this.x == 225)
					{
						if (this.y == 0)
							this.y = this.bb[this.z];
						else
							this.bb[this.z] = this.y;
						this.P[this.z] -= this.y * 4;
						if (this.P[this.z] < 500)
							this.P[this.z] = 500;
						else if (this.P[this.z] > 7999)
							this.P[this.z] = 7999;
						this.A = this.M[this.P[this.z]];
						a(this.z, this.A);
					}
					if (this.x == 226)
					{
						if (this.y == 0)
							this.y = this.eb[this.z];
						else
							this.eb[this.z] = this.y;
						this.P[this.z] += this.y * 4;
						if (this.P[this.z] < 500)
							this.P[this.z] = 500;
						else if (this.P[this.z] > 7999)
							this.P[this.z] = 7999;
						this.A = this.M[this.P[this.z]];
						a(this.z, this.A);
					}
					if (this.x == 33)
						if (this.y / 16 == 1)
						{
							this.y &= 15;
							if (this.y == 0)
								this.y = this.cb[this.z];
							else
								this.cb[this.z] = this.y;
							this.P[this.z] -= this.y;
							if (this.P[this.z] < 500)
								this.P[this.z] = 500;
							else if (this.P[this.z] > 7999)
								this.P[this.z] = 7999;
							this.A = this.M[this.P[this.z]];
							a(this.z, this.A);
						}
						else
						{
							this.y &= 15;
							if (this.y == 0)
								this.y = this.fb[this.z];
							else
								this.fb[this.z] = this.y;
							this.P[this.z] += this.y;
							if (this.P[this.z] < 500)
								this.P[this.z] = 500;
							else if (this.P[this.z] > 7999)
								this.P[this.z] = 7999;
							this.A = this.M[this.P[this.z]];
							a(this.z, this.A);
						}
				}
				this.z += 1;
			}
		}
		if ((this.f > 0) && (this.e >= 20000))
		{
			this.z = 0;
			while (this.z < this.i)
			{
				if (!this.yb[this.z])
				{
					if (this.pb[this.z] != 0)
					{
						this.O[this.z] += this.V[this.z];
						if (this.O[this.z] < 0)
							this.O[this.z] = 0;
						else if (this.O[this.z] > 63)
							this.O[this.z] = 63;
						b(this.z, this.O[this.z] * this.Lb / 64);
					}
					if (this.Z[this.z] == 1)
					{
						this.Y[this.z] = ((this.Y[this.z] + this.W[this.z]) % 68);
						int i4 = this.N[this.Y[this.z]] * this.X[this.z] >> 8;
						this.B = (this.P[this.z] + i4);
						a(this.z, this.M[this.B]);
					}
					if (this.gb[this.z] > 0)
					{
						this.P[this.z] -= this.gb[this.z] * 4;
						if (this.P[this.z] < 500)
							this.P[this.z] = 500;
						else if (this.P[this.z] > 7999)
							this.P[this.z] = 7999;
						this.A = this.M[this.P[this.z]];
						a(this.z, this.A);
					}
					if (this.hb[this.z] > 0)
					{
						this.P[this.z] += this.hb[this.z] * 4;
						if (this.P[this.z] < 500)
							this.P[this.z] = 500;
						else if (this.P[this.z] > 7999)
							this.P[this.z] = 7999;
						this.A = this.M[this.P[this.z]];
						a(this.z, this.A);
					}
					if (this.ob[this.z] > 0)
					{
						if (this.P[this.z] < this.ib[this.z])
						{
							this.P[this.z] += this.jb[this.z] * 4;
							if (this.P[this.z] > this.ib[this.z])
								this.P[this.z] = this.ib[this.z];
						}
						if (this.P[this.z] > this.ib[this.z])
						{
							this.P[this.z] -= this.jb[this.z] * 4;
							if (this.P[this.z] < this.ib[this.z])
								this.P[this.z] = this.ib[this.z];
						}
						if (this.P[this.z] < 500)
							this.P[this.z] = 500;
						else if (this.P[this.z] > 7999)
							this.P[this.z] = 7999;
						this.A = this.M[this.P[this.z]];
						a(this.z, this.A);
					}
				}
				this.z += 1;
			}
		}
		this.g += 50;
		if ((this.g >= 64) || (this.e >= 20000))
		{
			this.g %= 64;
			this.z = 0;
			while (this.z < this.i)
			{
				if (!this.yb[this.z])
					if ((this.R[this.z] >= 0) && ((this.wb[this.R[this.z]] & 0x1) == 1))
					{
						this.lb[this.z] = 1;
						i5 = this.R[this.z];
						int i6 = this.kb[this.z];
						int i7 = 0; for (; this.ub[i5][(i7 + 1)] < i6; i7++);
						int i8 = this.ub[i5][i7];
						int i10 = this.ub[i5][(i7 + 1)];
						int i9 = this.vb[i5][i7];
						int i11 = this.vb[i5][(i7 + 1)];
						if (i10 == i8)
							i10++;
						int i13 = ((i10 - i6) * i9 + (i6 - i8) * i11) / (i10 - i8);
						int i14 = 64 - this.nb[this.z] / 500;
						if (i14 < 0)
							i14 = 0;
						this.w = (this.O[this.z] * i13 * i14 / 4096);
						b(this.z, this.w * this.Lb / 64);
						if (((this.wb[i5] & 0x2) == 2) && (this.mb[this.z] == 1) && (this.kb[this.z] == this.ub[i5][this.rb[i5]]))
							this.kb[this.z] += -1;
						if (this.kb[this.z] == this.ub[i5][(this.qb[i5] - 1)])
							this.kb[this.z] += -1;
						if (this.mb[this.z] == 0)
							this.nb[this.z] += this.xb[i5];
						this.kb[this.z] += 1;
						if (((this.wb[i5] & 0x4) == 4) && (this.kb[this.z] == this.ub[i5][this.tb[i5]]))
							this.kb[this.z] = this.ub[i5][this.sb[i5]];
					}
					else
					{
						this.lb[this.z] = 0;
					}
				this.z += 1;
			}
		}
		if (this.e >= 20000)
		{
			this.e -= 20000;
			this.f += 1;
			if (this.f >= this.m)
				this.f = 0;
		}
		if (this.e >= 20000)
			a(0);
	}

	public void b(int paramInt)
	{
		this.Cb[paramInt] = 0;
	}

	public c(byte[] paramArrayOfByte)
	{
		int tmp2133_2132 = 0;
		byte[] tmp2133_2131 = paramArrayOfByte;
		tmp2133_2131[tmp2133_2132] = (byte)(tmp2133_2131[tmp2133_2132] + 1);
		this.b = paramArrayOfByte;
		a(paramArrayOfByte);
		for (int i1 = -32768; i1 < 32767; i1++)
			this.a[(i1 + 65536)] = c(i1);
		for (int i1 = 0; i1 < 32768; i1++)
			this.a[i1] = this.a[32768];
		for (int i1 = 98304; i1 < 131072; i1++)
			this.a[i1] = this.a[98303];
		for (int i1 = 0; i1 < 32; i1++)
		{
			this.Ab[i1] = 0;
			this.Bb[i1] = 0;
			this.Cb[i1] = 0;
			this.Db[i1] = 0;
			this.Eb[i1] = 10;
			this.yb[i1] = false;
		}
		this.o = 1;
		this.C = 0;
	}

	public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
	{
		a(paramInt1, this.T[paramInt2], paramInt3, paramInt4, this.F[paramInt2], this.H[paramInt2]);
	}

	public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	{
		if (paramInt2 > 128)
		{
			read(paramArrayOfByte, paramInt1, 128);
			read(paramArrayOfByte, paramInt1 + 128, paramInt2 - 128);
			return paramInt2;
		}
		a(this.b, 0, 0, 0, this.c, paramInt2);
		a(this.a, this.c, paramArrayOfByte, 0, paramInt1, paramInt2);
		a(paramInt2);
		return paramInt2;
	}

	public int read()
	{
		byte[] arrayOfByte = new byte[1];
		read(arrayOfByte, 0, 1);
		return arrayOfByte[0];
	}

	int a(byte[] paramArrayOfByte, int paramInt)
	{
		return (paramArrayOfByte[paramInt] & 0xFF) + (paramArrayOfByte[(paramInt + 1)] & 0xFF) * 256 + (paramArrayOfByte[(paramInt + 2)] & 0xFF) * 65536 + (paramArrayOfByte[(paramInt + 3)] & 0xFF) * 65536 * 256;
	}

	public void a() {sun.audio.AudioPlayer.player.start(this);}

	void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
	{
		if ((paramInt3 <= 0) || (paramInt3 > 340000))
		{
			b(paramInt1);
			return;
		}
		if (paramInt4 < 0)
			paramInt4 = 0;
		else if (paramInt4 > 63)
			paramInt4 = 63;
		this.Ab[paramInt1] = (paramInt2 << 8);
		this.Eb[paramInt1] = (paramInt2 + paramInt5 << 8);
		this.Db[paramInt1] = (paramInt6 << 8);
		this.Bb[paramInt1] = paramInt4;
		this.Cb[paramInt1] = (paramInt3 * 256 / 8000);
	}

	void a(byte[] paramArrayOfByte1, int[] paramArrayOfInt, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3)
	{
		for (paramInt1 = 0; paramInt1 < paramInt3; paramInt1++)
			paramArrayOfByte2[(paramInt2++)] = paramArrayOfByte1[((paramArrayOfInt[paramInt1] >> 8) + 65536)];
	}

	byte c(int paramInt)
	{
		int i1 = paramInt >> 8 & 0x80;
		if (i1 != 0)
			paramInt = -paramInt;
		if (paramInt > 32635)
			paramInt = 32635;
		paramInt += 132;
		int i2 = this.d[(paramInt >> 7 & 0xFF)];
		int i3 = paramInt >> i2 + 3 & 0xF;
		return (byte)((i1 | i2 << 4 | i3) ^ 0xFFFFFFFF);
	}

	int b(byte[] paramArrayOfByte, int paramInt)
	{
		return (paramArrayOfByte[paramInt] & 0xFF) + (paramArrayOfByte[(paramInt + 1)] & 0xFF) * 256;
	}

	void a(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, int paramInt4)
	{
		int i1 = this.Ab[0];
		int i2 = this.Cb[0];
		int i3 = this.Eb[0];
		int i4 = this.Db[0];
		int i5 = this.Bb[0] * this.Fb;
		for (paramInt3 = 0; paramInt3 < paramInt4; paramInt3++)
		{
			paramInt1 = i1 & 0xFF;
			paramInt2 = i1 >> 8;
			paramArrayOfInt[paramInt3] = ((paramArrayOfByte[paramInt2] * (256 - paramInt1) + paramArrayOfByte[(paramInt2 + 1)] * paramInt1) * i5);
			if ((i1 += i2 < i3? 1 : 0) != 0)
				continue;
			if (i4 == 0)
			{
				int tmp113_112 = 0;
				i2 = tmp113_112;
				this.Cb[0] = tmp113_112;
				i1 = i3 - 1;
			}
			else
			{
				i1 = (i1 - i3) % i4 + i3 - i4;
			}
		}
		this.Ab[0] = i1;
		for (int i6 = 1; i6 < this.i; i6++)
		{
			i1 = this.Ab[i6];
			i2 = this.Cb[i6];
			i3 = this.Eb[i6];
			i4 = this.Db[i6];
			i5 = this.Bb[i6] * this.Fb;
			for (paramInt3 = 0; paramInt3 < paramInt4; paramInt3++)
			{
				paramInt1 = i1 & 0xFF;
				paramInt2 = i1 >> 8;
				paramArrayOfInt[paramInt3] += (paramArrayOfByte[paramInt2] * (256 - paramInt1) + paramArrayOfByte[(paramInt2 + 1)] * paramInt1) * i5;
				if ((i1 += i2 < i3? 1 : 0) != 0)
					continue;
				if (i4 == 0)
				{
					int tmp288_287 = 0;
					i2 = tmp288_287;
					this.Cb[i6] = tmp288_287;
					i1 = i3 - 1;
				}
				else
				{
					i1 = (i1 - i3) % i4 + i3 - i4;
				}
			}
			this.Ab[i6] = i1;
		}
	}

	public void a(int paramInt1, int paramInt2)
	{
		this.Cb[paramInt1] = (paramInt2 * 256 / 8000);
	}

	void a(byte[] paramArrayOfByte)
	{
		for (int i3 = 0; i3 < 8000; i3++)
			this.M[i3] = (int)(8363.0D * Math.pow(2.0D, (4608 - i3) / 768.0D));
		for (int i3 = 0; i3 < 68; i3++)
			this.N[i3] = (int)(-2048.0D * Math.sin(i3 * 0.0923998D));
		for (int i3 = 0; i3 < 64; i3++)
		{
			this.ab[i3] = 0;
			this.bb[i3] = 0;
			this.cb[i3] = 0;
			this.db[i3] = 0;
			this.eb[i3] = 0;
			this.fb[i3] = 0;
			this.V[i3] = 0;
			this.W[i3] = 0;
			this.X[i3] = 0;
			this.Y[i3] = 0;
			this.Z[i3] = 0;
			this.gb[i3] = 0;
			this.hb[i3] = 0;
			this.ib[i3] = 0;
			this.jb[i3] = 0;
			this.Q[i3] = 0;
			this.O[i3] = 0;
			this.P[i3] = 0;
			this.kb[i3] = 0;
			this.R[i3] = -1;
			this.lb[i3] = 0;
			this.mb[i3] = 0;
			this.nb[i3] = 0;
			this.ob[i3] = 0;
			this.pb[i3] = 0;
		}
		this.h = b(paramArrayOfByte, 64);
		this.i = b(paramArrayOfByte, 68);
		this.j = b(paramArrayOfByte, 70);
		this.k = b(paramArrayOfByte, 72);
		this.l = b(paramArrayOfByte, 74);
		this.m = b(paramArrayOfByte, 76);
		this.n = b(paramArrayOfByte, 78);
		for (int i3 = 0; i3 < this.h; i3++)
			this.I[i3] = c(paramArrayOfByte, i3 + 80);
		int i12 = 60 + a(paramArrayOfByte, 60);
		int i7 = 0;
		for (int i3 = 0; i3 < this.j; i3++)
		{
			this.E[i3] = b(paramArrayOfByte, i12 + i7 + 5);
			this.S[i3] = (i12 + i7 + a(paramArrayOfByte, i12 + i7));
			i7 += a(paramArrayOfByte, i12 + i7) + b(paramArrayOfByte, i12 + i7 + 7);
		}
		int i13 = i12 + i7;
		i7 = 0;
		int i3 = 0;
		for (int i4 = 0; i4 < this.k; i4++)
		{
			int i9 = b(paramArrayOfByte, i13 + i7 + 27);
			for (int i6 = 0; i6 < 96; i6++)
				if (i9 > 0)
					this.zb[i4][i6] = (byte)(i3 + c(paramArrayOfByte, i13 + i7 + 33 + i6));
				else
					this.zb[i4][i6] = -1;
			this.qb[i4] = c(paramArrayOfByte, i13 + i7 + 225);
			this.rb[i4] = c(paramArrayOfByte, i13 + i7 + 227);
			this.sb[i4] = c(paramArrayOfByte, i13 + i7 + 228);
			this.tb[i4] = c(paramArrayOfByte, i13 + i7 + 229);
			this.wb[i4] = c(paramArrayOfByte, i13 + i7 + 233);
			this.xb[i4] = b(paramArrayOfByte, i13 + i7 + 239);
			for (int i6 = 0; i6 < 12; i6++)
			{
				this.ub[i4][i6] = b(paramArrayOfByte, i13 + i7 + 129 + i6 * 4);
				this.vb[i4][i6] = b(paramArrayOfByte, i13 + i7 + 131 + i6 * 4);
			}
			int i11 = a(paramArrayOfByte, i13 + i7 + 29);
			i7 += a(paramArrayOfByte, i13 + i7);
			int i8 = i7 + i11 * i9;
			for (int i6 = 0; i6 < i9; i6++)
			{
				int i10 = c(paramArrayOfByte, i13 + i7 + 14);
				if ((i10 & 0x10) == 16)
					this.J[i3] = 1;
				else
					this.J[i3] = 0;
				if ((i10 & 0x3) == 0)
				{
					this.H[i3] = 0;
					this.F[i3] = a(paramArrayOfByte, i13 + i7);
				}
				else
				{
					this.F[i3] = (a(paramArrayOfByte, i13 + i7 + 4) + a(paramArrayOfByte, i13 + i7 + 8));
					this.H[i3] = a(paramArrayOfByte, i13 + i7 + 8);
				}
				this.L[i3] = paramArrayOfByte[(i13 + i7 + 13)];
				this.K[i3] = (byte)(paramArrayOfByte[(i13 + i7 + 16)] - 1);
				this.G[i3] = c(paramArrayOfByte, i13 + i7 + 12);
				this.T[i3] = (i13 + i8);
				if (this.J[i3] == 1)
				{
					this.F[i3] /= 2;
					this.H[i3] /= 2;
				}
				i8 += a(paramArrayOfByte, i13 + i7);
				i7 += i11;
				i3++;
			}
			i3 -= i9;
			for (int i6 = 0; i6 < i9; i6++)
			{
				int i2 = 0;
				for (int i5 = 0; i5 < this.F[i3]; i5++)
				{
					int i1;
					if (this.J[i3] == 0)
					{
						i1 = paramArrayOfByte[(this.T[i3] + i5)] + i2;
						paramArrayOfByte[(this.T[i3] + i5)] = (byte)((byte)i1 / 2);
					}
					else
					{
						i1 = b(paramArrayOfByte, this.T[i3] + i5 * 2) + i2;
						paramArrayOfByte[(this.T[i3] + i5)] = (byte)((byte)(i1 / 256) / 2);
					}
					i2 = i1;
				}
				i3++;
				i7 = i8;
			}
		}
	}

	public void b(int paramInt1, int paramInt2)
	{
		if (paramInt2 < 0)
			paramInt2 = 0;
		else if (paramInt2 > 63)
			paramInt2 = 63;
		this.Bb[paramInt1] = paramInt2;
	}

	int c(byte[] paramArrayOfByte, int paramInt)
	{
		return paramArrayOfByte[paramInt] & 0xFF;
	}
}