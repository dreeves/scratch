package flea;

import java.io.PrintStream;

public class d
{
	static byte a(e parame)
	{
		return (byte)a(8, parame);
	}

	static void b(e parame)
	{
		int m = 0;
		int n = 0;
		int i1 = 0;
		int i2 = 0;
		int i3 = 0;
		int i4 = 0;
		int i5 = 0;
		int i6 = 0;
		int i7 = 0;
		int i8 = 0;
		int i9 = 0;
		int i10 = 0;
		int i11 = 0;
		int i12 = 0;
		int i13 = 0;
		int i14 = 0;
		int i15 = 0;
		int i16 = 0;
		int i17 = 0;
		int i18 = 0;
		int i19 = 0;
		int[] arrayOfInt1 = null;
		int[] arrayOfInt2 = null;
		int[] arrayOfInt3 = null;
		parame.z = 1;
		if (e.I == null)
			e.I = new int[parame.z * 100000];
		int i20 = 1;
		while (i20 != 0)
		{
			int i = a(parame);
			if (i == 23)
				return;
			i = a(parame);
			i = a(parame);
			i = a(parame);
			i = a(parame);
			i = a(parame);
			parame.A += 1;
			i = a(parame);
			i = a(parame);
			i = a(parame);
			i = a(parame);
			i = e(parame);
			if (i != 0)
				parame.w = true;
			else
				parame.w = false;
			if (parame.w)
				System.out.println("PANIC! RANDOMISED BLOCK!");
			parame.B = 0;
			i = a(parame);
			parame.B = (parame.B << 8 | i & 0xFF);
			i = a(parame);
			parame.B = (parame.B << 8 | i & 0xFF);
			i = a(parame);
			parame.B = (parame.B << 8 | i & 0xFF);
			for (m = 0; m < 16; m++)
			{
				i = e(parame);
				if (i == 1)
					parame.L[m] = true;
				else
					parame.L[m] = false;
			}
			for (m = 0; m < 256; m++)
				parame.K[m] = false;
			for (m = 0; m < 16; m++)
			{
				if (!parame.L[m])
					continue;
				for (n = 0; n < 16; n++)
				{
					i = e(parame);
					if (i != 1)
						continue;
					parame.K[(m * 16 + n)] = true;
				}
			}
			d(parame);
			i2 = parame.J + 2;
			i3 = a(3, parame);
			i4 = a(15, parame);
			for (m = 0; m < i4; m++)
			{
				n = 0;
				while (true)
				{
					i = e(parame);
					if (i == 0)
						break;
					n++;
				}
				parame.Q[m] = (byte)n;
			}
			byte[] arrayOfByte = new byte[6];
			for (int i23 = 0; i23 < i3; i23++)
				arrayOfByte[i23] = (byte)i23;
			int i22;
			for (m = 0; m < i4; m++)
			{
				int i23 = parame.Q[m];
				i22 = arrayOfByte[i23];
				while (i23 > 0)
				{
					arrayOfByte[i23] = arrayOfByte[(i23 - 1)];
					i23 = (byte)(i23 + -1);
				}
				arrayOfByte[0] = (byte)i22;
				parame.P[m] = (byte)i22;
			}
			for (i1 = 0; i1 < i3; i1++)
			{
				i13 = a(5, parame);
				for (m = 0; m < i2; m++)
				{
					while (true)
					{
						i = e(parame);
						if (i == 0)
							break;
						i = e(parame);
						if (i == 0)
							i13++;
						else
							i13--;
					}
					parame.R[i1][m] = (byte)i13;
				}
			}
			for (i1 = 0; i1 < i3; i1++)
			{
				int j = 32;
				int k = 0;
				for (m = 0; m < i2; m++)
				{
					if (parame.R[i1][m] > k)
						k = parame.R[i1][m];
					if (parame.R[i1][m] >= j)
						continue;
					j = parame.R[i1][m];
				}
				a(parame.S[i1], parame.T[i1], parame.U[i1], parame.R[i1], j, k, i2);
				parame.V[i1] = j;
			}
			i5 = parame.J + 1;
			i9 = 100000 * parame.z;
			i6 = -1;
			i7 = 0;
			for (m = 0; m <= 255; m++)
				parame.E[m] = 0;
			int i23 = 4095;
			for (int i21 = 15; i21 >= 0; i21--)
			{
				for (i22 = 15; i22 >= 0; i22--)
				{
					parame.N[i23] = (byte)(i21 * 16 + i22);
					i23--;
				}
				parame.O[i21] = (i23 + 1);
			}
			i10 = 0;
			if (i7 == 0)
			{
				i6++;
				i7 = 50;
				i18 = parame.P[i6];
				i19 = parame.V[i18];
				arrayOfInt1 = parame.S[i18];
				arrayOfInt3 = parame.U[i18];
				arrayOfInt2 = parame.T[i18];
			}
			i7--;
			i15 = i19;
			i16 = a(i15, parame);
			while (i16 > arrayOfInt1[i15])
			{
				i15++;
				i17 = e(parame);
				i16 = i16 << 1 | i17;
			}
			i8 = arrayOfInt3[(i16 - arrayOfInt2[i15])];
			while (i8 != i5)
				if ((i8 == 0) || (i8 == 1))
				{
					i11 = -1;
					i12 = 1;
					do
					{
						if (i8 == 0)
							i11 += 1 * i12;
						else if (i8 == 1)
							i11 += 2 * i12;
						i12 *= 2;
						if (i7 == 0)
						{
							i6++;
							i7 = 50;
							i18 = parame.P[i6];
							i19 = parame.V[i18];
							arrayOfInt1 = parame.S[i18];
							arrayOfInt3 = parame.U[i18];
							arrayOfInt2 = parame.T[i18];
						}
						i7--;
						i15 = i19;
						i16 = a(i15, parame);
						while (i16 > arrayOfInt1[i15])
						{
							i15++;
							i17 = e(parame);
							i16 = i16 << 1 | i17;
						}
						i8 = arrayOfInt3[(i16 - arrayOfInt2[i15])];
					}
					while ((i8 == 0) || (i8 == 1));
					i11++;
					i = parame.M[(parame.N[parame.O[0]] & 0xFF)];
					parame.E[(i & 0xFF)] += i11;
					while (i11 > 0)
					{
						e.I[i10] = (i & 0xFF);
						i10++;
						i11--;
					}
				}
				else
				{
					int i27 = i8 - 1;
					int i24;
					if (i27 < 16)
					{
						i24 = parame.O[0];
						i = parame.N[(i24 + i27)];
						while (i27 > 3)
						{
							int i28 = i24 + i27;
							parame.N[i28] = parame.N[(i28 - 1)];
							parame.N[(i28 - 1)] = parame.N[(i28 - 2)];
							parame.N[(i28 - 2)] = parame.N[(i28 - 3)];
							parame.N[(i28 - 3)] = parame.N[(i28 - 4)];
							i27 -= 4;
						}
						while (i27 > 0)
						{
							parame.N[(i24 + i27)] = parame.N[(i24 + i27 - 1)];
							i27--;
						}
						parame.N[i24] = (byte)i;
					}
					else
					{
						int i25 = i27 / 16;
						int i26 = i27 % 16;
						i24 = parame.O[i25] + i26;
						i = parame.N[i24];
						while (i24 > parame.O[i25])
						{
							parame.N[i24] = parame.N[(i24 - 1)];
							i24--;
						}
						parame.O[i25] += 1;
						while (i25 > 0)
						{
							parame.O[i25] += -1;
							parame.N[parame.O[i25]] = parame.N[(parame.O[(i25 - 1)] + 16 - 1)];
							i25--;
						}
						parame.O[0] += -1;
						parame.N[parame.O[0]] = (byte)i;
						if (parame.O[0] == 0)
						{
							i23 = 4095;
							for (int i21 = 15; i21 >= 0; i21--)
							{
								for (i22 = 15; i22 >= 0; i22--)
								{
									parame.N[i23] = parame.N[(parame.O[i21] + i22)];
									i23--;
								}
								parame.O[i21] = (i23 + 1);
							}
						}
					}
					parame.E[(parame.M[(i & 0xFF)] & 0xFF)] += 1;
					e.I[i10] = (parame.M[(i & 0xFF)] & 0xFF);
					i10++;
					if (i7 == 0)
					{
						i6++;
						i7 = 50;
						i18 = parame.P[i6];
						i19 = parame.V[i18];
						arrayOfInt1 = parame.S[i18];
						arrayOfInt3 = parame.U[i18];
						arrayOfInt2 = parame.T[i18];
					}
					i7--;
					i15 = i19;
					i16 = a(i15, parame);
					while (i16 > arrayOfInt1[i15])
					{
						i15++;
						i17 = e(parame);
						i16 = i16 << 1 | i17;
					}
					i8 = arrayOfInt3[(i16 - arrayOfInt2[i15])];
				}
			parame.v = 0;
			parame.u = 0;
			parame.G[0] = 0;
			for (m = 1; m <= 256; m++)
				parame.G[m] = parame.E[(m - 1)];
			for (m = 1; m <= 256; m++)
				parame.G[m] += parame.G[(m - 1)];
			for (m = 0; m < i10; m++)
			{
				i = (byte)(e.I[m] & 0xFF);
				e.I[parame.G[(i & 0xFF)]] |= m << 8;
				parame.G[(i & 0xFF)] += 1;
			}
			parame.C = (e.I[parame.B] >> 8);
			parame.F = 0;
			parame.C = e.I[parame.C];
			parame.D = (byte)(parame.C & 0xFF);
			parame.C >>= 8;
			parame.F += 1;
			parame.W = i10;
			c(parame);
			if ((parame.F == parame.W + 1) && (parame.v == 0))
				i20 = 1;
			else
				i20 = 0;
		}
	}

	static void c(e parame)
	{
		byte b = parame.u;
		int j = parame.v;
		int k = parame.F;
		int m = parame.D;
		int[] arrayOfInt = e.I;
		int n = parame.C;
		byte[] arrayOfByte = parame.p;
		int i1 = parame.q;
		int i2 = parame.r;
		int i3 = i2;
		int i4 = parame.W + 1;
		label416: while (true)
		{
			if (j > 0)
			{
				while (true)
				{
					if (i2 == 0)
						break label416;
					if (j == 1)
						break;
					arrayOfByte[i1] = b;
					j--;
					i1++;
					i2--;
				}
				if (i2 == 0)
				{
					j = 1;
					break;
				}
				arrayOfByte[i1] = b;
				i1++;
				i2--;
			}
			int i6 = 1;
			while (i6 != 0)
			{
				i6 = 0;
				if (k == i4)
				{
					j = 0;
					break;
				}
				b = (byte)m;
				n = arrayOfInt[n];
				int i = (byte)(n & 0xFF);
				n >>= 8;
				k++;
				if (i != m)
				{
					m = i;
					if (i2 == 0)
					{
						j = 1;
						break;
					}
					arrayOfByte[i1] = b;
					i1++;
					i2--;
					i6 = 1;
				}
				else
				{
					if (k != i4)
						continue;
					if (i2 == 0)
					{
						j = 1;
						break;
					}
					arrayOfByte[i1] = b;
					i1++;
					i2--;
					i6 = 1;
				}
			}
			j = 2;
			n = arrayOfInt[n];
			int i = (byte)(n & 0xFF);
			n >>= 8;
			k++;
			if (k != i4)
				if (i != m)
				{
					m = i;
				}
				else
				{
					j = 3;
					n = arrayOfInt[n];
					i = (byte)(n & 0xFF);
					n >>= 8;
					k++;
					if (k != i4)
						if (i != m)
						{
							m = i;
						}
						else
						{
							n = arrayOfInt[n];
							i = (byte)(n & 0xFF);
							n >>= 8;
							k++;
							j = (i & 0xFF) + 4;
							n = arrayOfInt[n];
							m = (byte)(n & 0xFF);
							n >>= 8;
							k++;
						}
				}
		}
		int i5 = parame.s;
		parame.s += i3 - i2;
		if (parame.s < i5)
			parame.t += 1;
		parame.u = b;
		parame.v = j;
		parame.F = k;
		parame.D = m;
		e.I = arrayOfInt;
		parame.C = n;
		parame.p = arrayOfByte;
		parame.q = i1;
		parame.r = i2;
	}

	static void d(e parame)
	{
		parame.J = 0;
		for (int i = 0; i < 256; i++)
		{
			if (!parame.K[i])
				continue;
			parame.M[parame.J] = (byte)i;
			parame.J += 1;
		}
	}

	static byte e(e parame)
	{
		return (byte)a(1, parame);
	}

	static void a(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
	{
		int i = 0;
		for (int j = paramInt1; j <= paramInt2; j++)
			for (int k = 0; k < paramInt3; k++)
			{
				if (paramArrayOfByte[k] != j)
					continue;
				paramArrayOfInt3[i] = k;
				i++;
			}
		for (int j = 0; j < 23; j++)
			paramArrayOfInt2[j] = 0;
		for (int j = 0; j < paramInt3; j++)
			paramArrayOfInt2[(paramArrayOfByte[j] + 1)] += 1;
		for (int j = 1; j < 23; j++)
			paramArrayOfInt2[j] += paramArrayOfInt2[(j - 1)];
		for (int j = 0; j < 23; j++)
			paramArrayOfInt1[j] = 0;
		int m = 0;
		for (int j = paramInt1; j <= paramInt2; j++)
		{
			m += paramArrayOfInt2[(j + 1)] - paramArrayOfInt2[j];
			paramArrayOfInt1[j] = (m - 1);
			m <<= 1;
		}
		for (int j = paramInt1 + 1; j <= paramInt2; j++)
			paramArrayOfInt2[j] = ((paramArrayOfInt1[(j - 1)] + 1 << 1) - paramArrayOfInt2[j]);
	}

	public static int a(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3)
	{
		e locale = new e();
		locale.k = paramArrayOfByte2;
		locale.l = paramInt3;
		locale.p = paramArrayOfByte1;
		locale.q = 0;
		locale.m = paramInt2;
		locale.r = paramInt1;
		locale.y = 0;
		locale.x = 0;
		locale.n = 0;
		locale.o = 0;
		locale.s = 0;
		locale.t = 0;
		locale.A = 0;
		b(locale);
		paramInt1 -= locale.r;
		return paramInt1;
	}

	static int a(int paramInt, e parame)
	{
		int i;
		while (true)
		{
			if (parame.y >= paramInt)
			{
				int j = parame.x >> parame.y - paramInt & (1 << paramInt) - 1;
				parame.y -= paramInt;
				i = j;
				break;
			}
			parame.x = (parame.x << 8 | parame.k[parame.l] & 0xFF);
			parame.y += 8;
			parame.l += 1;
			parame.m += -1;
			parame.n += 1;
			if (parame.n == 0)
				parame.o += 1;
		}
		return i;
	}
}