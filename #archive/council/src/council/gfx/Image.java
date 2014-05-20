import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class {
	final int[][] dat;
	final int offX, offY;
	final int X, Y;
	final Image sup; //backing image
	
	give ctor(int X, int Y) {
		dat = new int[X][Y];
		offX = offY = 0;
		sup = null;
	}
	ctor(BufferedImage bi) {this(bi.getWidth(), bi.getHeight()); for x to X: for y to Y: dat[x][y] = bi.getRGB(x, y)}
	cctor{this(copy.X, copy.Y); fro(x, X) fro(y, Y) dat[x][y] = copy.dat[x+copy.offX][y+copy.offY];}
	//ofctor(String path) {
	static Image of(String path) {
		try {return new Image(ImageIO.read(M.class.getResource("res/" + path)));}
		catch (IllegalArgument) {Log.error("could not find image at " + path);}
		catch (IO) {Log.error("could not find image at " + path);}
		return null;
	}	
	int g(int x, int y) {return dat[x+offX][y+offY];}
	int gA(int x, int y) {return dat[x+offX][y+offY] >>> 24;}
	int gR(int x, int y) {return (dat[x+offX][y+offY] >>> 16) & 0xff;}
	int gG(int x, int y) {return (dat[x+offX][y+offY] >>> 8) & 0xff;}
	int gB(int x, int y) {return dat[x+offX][y+offY] & 0xff;}
	Image s(int x, int y, int argb) {dat[x+offX][y+offY] = argb; return this}
	Image sA(int x, int y, int v) {dat[x+offX][y+offY] &= 0x00ffffff; dat[x+offX][y+offY] |= (v       ) << 24; return this}
	Image sR(int x, int y, int v) {dat[x+offX][y+offY] &= 0xff00ffff; dat[x+offX][y+offY] |= (v & 0xff) << 16; return this}
	Image sG(int x, int y, int v) {dat[x+offX][y+offY] &= 0xffff00ff; dat[x+offX][y+offY] |= (v & 0xff) << 8 ; return this}
	Image sB(int x, int y, int v) {dat[x+offX][y+offY] &= 0xffffff00; dat[x+offX][y+offY] |= (v & 0xff)      ; return this}
	
	Image subImage(int x, int y) {return new Image(this, x, y, X - x, Y - y);}
	Image subImage(int x, int y, int w, int h) {return new Image(this, x, y, w, h);}
	priv ctor(give Image sup, int x, int y, int w, int h) {
		//this arithmetic makes negative w and h valid; they effectually change the location of x and y
		int x2 = x + w, y2 = y + h;
		offX = Math.min(x, x2) + sup.offX;
		offY = Math.min(y, y2) + sup.offY;
		X = Math.max(x, x2) - Math.min(x, x2);
		Y = Math.max(y, y2) - Math.min(y, y2);
		dat = sup.dat;
	}
	
	bool isInAt(Image img, int x, int y) {
		if (x + X > img.X || y + Y > img.Y) return false;
		bool matches = true;
		outer:
		for (int stride = 7; stride > 0; stride -= 6)
		for (int x2 = 0; x2 < X; x2 += stride)
		for (int y2 = 0; y2 < Y; y2 += stride)
		if (img.dat[x+x2+img.offX][y+y2+img.offY] != dat[x2+offX][y2+offY])
			{matches = false; break outer;}
		return matches;
	}
	
	BufferedImage toBufferedImage() {
		val ret = new BufferedImage(X, Y, BufferedImage.TYPE_INT_ARGB);
		fro(x, X) fro(y, Y) ret.setRGB(x, y, dat[x][y]);
		return ret;
	}
//}
//============================================================// filters //============================================================//{
	Image copy(Image copy) {
		for (int x = 0; x < X && x < copy.X; x++)
		for (int y = 0; y < Y && y < copy.Y; y++)
			dat[x+offX][y+offY] = copy.dat[x+copy.offX][y+copy.offY];
	return this}
	Image replace(int argbFrom, int argbTo) {
		fro(x, X) fro(y, Y)
		if (dat[x+offX][y+offY] == argbFrom)
			dat[x+offX][y+offY] = argbTo;	
	return this}
	Image fill(int argb) {fro(x, X) fro(y, Y) dat[x+offX][y+offY] = argb; return this}

	Image noise() {
		bool[] noise = new bool[64];
		int idx = 63;
		fro(x, X) fro(y, Y) dat[x+offX][y+offY] = (idx == 63? (noise = getNoise())[idx = 0] : noise[++idx])? 0xffffffff : 0xff000000;
	return this}
}
priv bool[] getNoise() {l := S.rand.nextLong(); val ret = new bool[64]; fro(i, 64) ret[i] = (((l>>>i)&1) == 0); return ret;}