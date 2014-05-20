import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Imagine
{
	public static void main(String[] args) {
		String path = "1.png";
		S.pln("Thinking... ");
		
		Image img = Image.read(path);
		/*Image dest = img.subImage(0, 0, 16, 16);
		Image pattern = img.subImage(16, 0, 16, 16);
		
		double pavgg = 0;
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++)
			pavgg += pattern.g(x, y);
		pavgg /= 256;
		double pavgrog = 0;
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++)
			pavgrog += (double)pattern.r(x, y) / pattern.g(x, y);
		pavgrog /= 256;
		double pavgbog = 0;
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++)
			pavgbog += (double)pattern.b(x, y) / pattern.g(x, y);
		pavgbog /= 256;
		double davgg = 0;
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++)
			davgg += dest.g(x, y);
		davgg /= 256;
		
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++) {
			dest.setR(x, y, Math.min(0xff, (int)(dest.r(x, y) * 1/pavgrog        )));
			dest.setG(x, y, Math.min(0xff, (int)(dest.g(x, y) * pavgg / davgg                  )));
			dest.setB(x, y, Math.min(0xff, (int)(dest.b(x, y) * 1/pavgbog        )));
		}*/

		/*Color pavg = new Color();
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++)
			pavg.add(new Color(pattern.get(x, y)));
		pavg.mul(1/256d);
		Color davg = new Color();
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++)
			davg.add(new Color(dest.get(x, y)));
		davg.mul(1/256d);
		
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++) {
			dest.setR(x, y, Math.min(0xff, (int)(dest.r(x, y) * pavg.r / davg.r)));
			dest.setG(x, y, Math.min(0xff, (int)(dest.g(x, y) * pavg.g / davg.g)));
			dest.setB(x, y, Math.min(0xff, (int)(dest.b(x, y) * pavg.b / davg.b)));
		}*/
		
		List<Color> list = newList();
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++)
			list.add(new Color(img.get(x, y)));
			
		Collections.shuffle(list);
		
		for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++)
			img.set(x, y, list.get(x * 16 + y).argb());
		
		img.write("png", path);
		
		S.p("Thunk.");
	}
	
	private static int min(int... a) {int ret = a[0]; for (int i : a) if (i < ret) ret = i; return ret;}
	private static int max(int... a) {int ret = a[0]; for (int i : a) if (i > ret) ret = i; return ret;}
	private static <E> ArrayList<E> newList() {return new ArrayList<E>();}
}

final class Color implements Comparable<Color>
{
	public static final Color WHITE = new Color(1, 1, 1, 1);
	public static final Color BLACK = new Color(1, 0, 0, 0);
	
	public float a, r, g, b;
	
	public Color() {}
	public Color(int argb) {this((argb>>>24)/255f, ((argb>>>16)&0xff)/255f, ((argb>>>8)&0xff)/255f, (argb&0xff)/255f);}
	public Color(float r, float g, float b) {this(1, r, g, b);}
	public Color(float _a, float _r, float _g, float _b) {a = _a; r = _r; g = _g; b = _b;}
	public Color(Color toCopy) {a = toCopy.a; r = toCopy.r; g = toCopy.g; b = toCopy.b;}
	public static Color makeGrey(double d) {return makeGrey((float)d);}
	public static Color makeGrey(float f) {return f == 1? WHITE : (f == 0? BLACK : new Color(f, f, f));}
	
	public void set(Color toCopy) {a = toCopy.a; r = toCopy.r; g = toCopy.g; b = toCopy.b;}
	public Color withAlpha(float alpha) {return alpha == a? this : new Color(alpha, r, g, b);}
	public Color mul(Color col) {return col == WHITE? this : (this == WHITE? col : new Color(a * col.a, r * col.r, g * col.g, b * col.b));}
	
	public Color mul(double d) {a *= d; r *= d; g *= d; b *= d; return this;}
	public Color add(Color col) {a += col.a; r += col.r; g += col.g; b += col.b; return this;}
	
	public int argb() {return (((int)(a*255))<<24) | (((int)(r*255))<<16) | (((int)(g*255))<<8) | ((int)(b*255));}
	
	public int compareTo(Color rh) {return (int)(((r + g + b) - (rh.r + rh.g + rh.b)) * 200);}
}

		/*for (int x = 0; x < 16; x++)
		for (int y = 0; y < 16; y++) {
			int rgb = img.getRGB(x, y);
			int r = rgb >>> 16 & 0xff;
			int g = rgb >>> 8 & 0xff;
			int b = rgb & 0xff;
			//g *= 1.28 / 1.23;
			//b *= 1.28 / 1.23 * 1.89 / 1.63;
			r = Math.min(0xff, (int)(r * 157.0 / 84.0));
			g = Math.min(0xff, (int)(g * 127.0 / 84.0));
			b = Math.min(0xff, (int)(b * 78.0 / 84.0));
			//r += 3;
			r <<= 16;
			g <<= 8;
			img.setRGB(x, y, 0xff000000 | r | g | b);
		}*/

class Image
{
	public final int[][] dat;
	public final int offX, offY;
	public final int width, height, X, Y;
	
	public Image(int w, int h) {
		dat = new int[w][h];	
		offX = offY = 0;
		width = X = w;
		height = Y = h;
	}
	public Image(BufferedImage bi) {
		this(bi.getWidth(), bi.getHeight());
		for (int x = 0; x < width; x++)
		for (int y = 0; y < height; y++)
			dat[x][y] = bi.getRGB(x, y);
	}
	public Image(Image copy) {
		this(copy.width, copy.height);
		for (int x = 0; x < width; x++)
		for (int y = 0; y < height; y++)
			dat[x][y] = copy.dat[x+copy.offX][y+copy.offY];
	}
	
	public void set(int x, int y, int argb) {dat[x+offX][y+offY] = argb;}
	public int get(int x, int y) {return dat[x+offX][y+offY];}
	public int a(int x, int y) {return dat[x+offX][y+offY] >>> 24;}
	public int r(int x, int y) {return (dat[x+offX][y+offY] >>> 16) & 0xff;}
	public int g(int x, int y) {return (dat[x+offX][y+offY] >>> 8) & 0xff;}
	public int b(int x, int y) {return dat[x+offX][y+offY] & 0xff;}
	public void sA(int x, int y, int val) {dat[x+offX][y+offY] &= 0x00ffffff; dat[x+offX][y+offY] |= (val       ) << 24;}
	public void sR(int x, int y, int val) {dat[x+offX][y+offY] &= 0xff00ffff; dat[x+offX][y+offY] |= (val & 0xff) << 16;}
	public void sG(int x, int y, int val) {dat[x+offX][y+offY] &= 0xffff00ff; dat[x+offX][y+offY] |= (val & 0xff) << 8 ;}
	public void sB(int x, int y, int val) {dat[x+offX][y+offY] &= 0xffffff00; dat[x+offX][y+offY] |= (val & 0xff)      ;}
	
	public Image subImage(int x, int y) {return new Image(this, x, y, width - x, height - y);}
	public Image subImage(int x, int y, int w, int h) {return new Image(this, x, y, w, h);}
	private Image(Image backingImage, int x, int y, int w, int h) {
		//this arithmetic makes negative w and h valid; they effectually change the location of x and y
		int x2 = x + w, y2 = y + h;
		offX = Math.min(x, x2);
		offY = Math.min(y, y2);
		width = X = Math.max(x, x2) - offX;
		height = Y = Math.max(y, y2) - offY;
		dat = backingImage.dat;
	}
	
	public Image replace(int colFrom, int colTo) {
		for (int x = 0; x < X; x++)
		for (int y = 0; y < Y; y++)
		if (dat[x+offX][y+offY] == colFrom)
			dat[x+offX][y+offY] = colTo;	
		return this;
	}
	
	public boolean isInAt(Image img, int x, int y) {
		if (x + X > img.X || y + Y > img.Y) return false;
		boolean matches = true;
		outer:
		for (int stride = 7; stride > 0; stride -= 6) {
			for (int x2 = 0; x2 < X; x2 += stride)
			for (int y2 = 0; y2 < Y; y2 += stride)
			if (img.dat[x+x2+img.offX][y+y2+img.offY] != dat[x2+offX][y2+offY])
				{matches = false; break outer;}
		}
		return matches;
	}
	
	public BufferedImage bufferedImageFrom() {
		BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < width; x++)
		for (int y = 0; y < height; y++)
			ret.setRGB(x, y, dat[x][y]);
		return ret;
	}
	
	public boolean write(String formatName, String path) {return write(formatName, new File(path));}
	public boolean write(String formatName, File path) {try {ImageIO.write(bufferedImageFrom(), formatName, path);} catch (IOException e) {return false;} return true;}
	public boolean write(String formatName, OutputStream out) {try{ImageIO.write(bufferedImageFrom(), formatName, out);} catch(IOException e) {return false;} return true;}
	
	public static Image read(String path   ) {return read(new File(path));}
	public static Image read(File path     ) {try {return new Image(ImageIO.read(path));} catch (IOException e) {return null;}}
	public static Image read(InputStream in) {try {return new Image(ImageIO.read(in));  } catch (IOException e) {return null;}}
	public static Image read(URL path      ) {try {return new Image(ImageIO.read(path));} catch (IOException e) {return null;}}
}

class S
{
	/**.toString()'s stuff with a separator*/
	public static String sep(String separator, Object... args) {
		StringBuilder ret = new StringBuilder();
		for (Object o : args) {
			String s = String.valueOf(o);
			if (s.length() != 0) ret.append(s).append(separator);
		}
		if (args.length > 0) ret.setLength(ret.length() - separator.length());
		return ret.toString();
	}

	/**prints .toString()'d stuff with a separator of ' + " " + '*/
	public static void p(Object... args) {System.out.print(S.sep(" ", args));}

	/**prints .toString()'d stuff with a separator of ' + " " + '*/
	public static void pln(Object... args) {System.out.println(S.sep(" ", args));}
}