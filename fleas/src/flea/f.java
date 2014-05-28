package flea;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;

public class f
{
	public static Component a;
	public static Applet b;
	public static boolean c = false;

	public static byte[] a(String paramString, int paramInt, byte[] paramArrayOfByte)
	{
		int i = paramArrayOfByte[0] * 256 + paramArrayOfByte[1];
		int j = 0;
		paramString = paramString.toUpperCase();
		int k = 0;
		for (; k < paramString.length(); k++)
			j = j * 61 + paramString.charAt(k) - 32;
		k = 2 + i * 10;
		for (int m = 0; m < i; m++)
		{
			int n = (paramArrayOfByte[(m * 10 + 2)] & 0xFF) * 16777216 + (paramArrayOfByte[(m * 10 + 3)] & 0xFF) * 65536 + (paramArrayOfByte[(m * 10 + 4)] & 0xFF) * 256 + (paramArrayOfByte[(m * 10 + 5)] & 0xFF);
			int i1 = (paramArrayOfByte[(m * 10 + 6)] & 0xFF) * 65536 + (paramArrayOfByte[(m * 10 + 7)] & 0xFF) * 256 + (paramArrayOfByte[(m * 10 + 8)] & 0xFF);
			int i2 = (paramArrayOfByte[(m * 10 + 9)] & 0xFF) * 65536 + (paramArrayOfByte[(m * 10 + 10)] & 0xFF) * 256 + (paramArrayOfByte[(m * 10 + 11)] & 0xFF);
			if (n == j)
			{
				byte[] arrayOfByte = new byte[i1 + paramInt];
				if (i1 != i2)
					d.a(arrayOfByte, i1, paramArrayOfByte, i2, k);
				else
					for (int i3 = 0; i3 < i1; i3++)
						arrayOfByte[i3] = paramArrayOfByte[(k + i3)];
				return arrayOfByte;
			}
			k += i2;
		}
		System.out.println("Warning file not found: " + paramString);
		return null;
	}

	public static Image a(String paramString) {
		Image r = Toolkit.getDefaultToolkit().getImage(paramString);
		if (r == null) System.out.println("Warning couldn't load: " + paramString);
		return r;
		/*Image localImage;
		if (b == null)
			localImage = Toolkit.getDefaultToolkit().getImage(paramString);
		else
			localImage = b.getImage(b.getCodeBase(), paramString);
		MediaTracker localMediaTracker = new MediaTracker(a);
		localMediaTracker.addImage(localImage, 0);
		try
		{
			localMediaTracker.waitForID(0);
		}
		catch (InterruptedException localInterruptedException)
		{
			System.out.println("Error!");
		}
		if ((localMediaTracker.isErrorID(0)) || (localImage == null))
		{
			localImage = null;
			System.out.println("Warning couldn't load: " + paramString);
		}
		return localImage;*/
	}

	public static byte[] b(String paramString)
		throws IOException
	{
		InputStream localInputStream = c(paramString);
		DataInputStream localDataInputStream = new DataInputStream(localInputStream);
		byte[] arrayOfByte1 = new byte[6];
		localDataInputStream.readFully(arrayOfByte1, 0, 6);
		int i = ((arrayOfByte1[0] & 0xFF) << 16) + ((arrayOfByte1[1] & 0xFF) << 8) + (arrayOfByte1[2] & 0xFF);
		int j = ((arrayOfByte1[3] & 0xFF) << 16) + ((arrayOfByte1[4] & 0xFF) << 8) + (arrayOfByte1[5] & 0xFF);
		byte[] arrayOfByte2 = new byte[j];
		localDataInputStream.readFully(arrayOfByte2, 0, j);
		localDataInputStream.close();
		if (j != i)
		{
			byte[] arrayOfByte3 = new byte[i];
			d.a(arrayOfByte3, i, arrayOfByte2, j, 0);
			return arrayOfByte3;
		}
		return arrayOfByte2;
	}

	public static void a(Component paramComponent, boolean paramBoolean)
	{
		a = paramComponent;
		if (paramBoolean)
			b = (Applet)paramComponent;
		else
			b = null;
	}

	public static InputStream c(String paramString)
		throws IOException
	{
		Object localObject;
		if (b == null)
		{
			localObject = new FileInputStream(paramString);
		}
		else
		{
			URL localURL = new URL(b.getCodeBase(), paramString);
			localObject = localURL.openStream();
		}
		return (InputStream)localObject;
	}
}