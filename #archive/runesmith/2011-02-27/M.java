package runebot;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Rectangle;

import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.awt.Point;
import java.awt.MouseInfo;

import java.util.Scanner;

public class M {
	public static final Toolkit defaultTk = Toolkit.getDefaultToolkit();
	private static Robot robot;
	static {try {robot = new Robot();} catch (java.awt.AWTException e) {e.printStackTrace();}}
	
	public static void main(String[] args) {
		/*S.pln("waiting");
		S.sleep(3000);
		S.pln("starting");
		for (int i = 0; i < 2000; i++) {
			S.pln("clicking", i);
			click();
			S.sleep(110);
		}
		S.pln("ending");
		if (true) return;*/
	
		Pattern lowerLeft  = new Pattern(new Image("lowerLeft.png"), -7, 20);
		Pattern lowerRight = new Pattern(new Image("lowerRight.png"), 30, 36);
		Pattern upperRight = new Pattern(new Image("upperRight.png"), 20, -1);
		Pattern redFlag = new Pattern(new Image("flag.png"), 0, 0, 4, 6);
		Pattern ore = new Pattern(new Image("ore.png"), 0, 0, 5, 12);

		Point[] clickLoc = new Point[2];
		for (int i = 0; i < clickLoc.length; i++) {
			S.pln("enter to pick loc", i);
			new Scanner(System.in).nextLine();
			clickLoc[i] = getMouseLoc();
		}
		S.pln("got locs");
		
		long startingTime = S.nTime();
		
		for (int cycles = 0;; cycles++) {
			Image screen = getAndWriteScreen(startingTime);
			
			Point locSE = lowerRight.findIn(screen);
			if (locSE == null || lowerLeft.findIn(screen) == null)
				{S.sleep(1000); continue;}
			
			Image lastSlot = screen.getSubImage(locSE.x - 51, locSE.y - 79, locSE.x - 51 + 34, locSE.y - 79 + 32);
			writeImage(lastSlot, "pics/lastSlot" + cycles + ".png");
			if (ore.findIn(lastSlot) != null) {S.pln("inventory is full"); break;}
			
			if (S.nTime() - startingTime > 1800 * 1000000000L) {S.pln("been running for thirty minutes"); return;}
			
			clickAt(clickLoc[cycles % clickLoc.length]);
			
			S.sleep(approx(5000));
		}
		
		Point[] pathBack = {
				new Point(1268 - 1365, 104 - 95),
				new Point(1269 - 1365, 105 - 95),
				new Point(1246 - 1365, 113 - 95),
				new Point(1230 - 1365, 136 - 95),
				new Point(1219 - 1365, 141 - 95),
				new Point(1255 - 1365, 118 - 95),
			};
		
		for (int pbCount = 0; pbCount < pathBack.length; pbCount++) {
			Image screen = getAndWriteScreen(startingTime);
			
			Point locNE = upperRight.findIn(screen);
			if (locNE == null)
				{S.sleep(1000); continue;}
			
			clickAt(addPoints(pathBack[pbCount], locNE));
			
			S.sleep(approx(18500));
		}
	}
	
	static Point addPoints(Point a, Point b) {return new Point(a.x + b.x, a.y + b.y);}
	
	static Point getMouseLoc() {return MouseInfo.getPointerInfo().getLocation();}
	
	static void clickAt(Point p) {clickAt(p.x, p.y);}
	static void clickAt(int x, int y) {
		Point mouseLoc = getMouseLoc();
		mouseMove(x, y);
		click();
		mouseMove(mouseLoc);
	}
	static void click() {robot.mousePress(16); robot.mouseRelease(16);}
	
	static void mouseMove(int x, int y) {robot.mouseMove(x, y);}
	static void mouseMove(Point p) {robot.mouseMove(p.x, p.y);}
	
	static Image getAndWriteScreen(long startingTime) {
		BufferedImage screen = getScreen();
		long secs = (S.nTime() - startingTime) / 1000000000L;
		writeImage(screen, S.sep(" ", "pics/" + (secs / 3600), (secs / 60) % 60, (secs % 60) + ".png"));
		return new Image(screen);
	}
	static BufferedImage getScreen()
		{return robot.createScreenCapture(new Rectangle(0, 0, M.defaultTk.getScreenSize().width, M.defaultTk.getScreenSize().height));}
	
	static void writeImage(Image img, String path) {
		if (img == null)
			{new NullPointerException().printStackTrace(); return;}
		BufferedImage bi = new BufferedImage(img.X, img.Y, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < img.X; x++)
		for (int y = 0; y < img.Y; y++)
			bi.setRGB(x, y, img.get(x, y));
		writeImage(bi, path);
	}
	static void writeImage(BufferedImage img, String path) {
		try {ImageIO.write(img, "png", new File("runebot/" + path));}
		catch (IOException e) {e.printStackTrace();}
		catch (IllegalArgumentException e) {e.printStackTrace();}
	}
	
	static BufferedImage getImage(String path) {
		BufferedImage ret = null;
		try {ret = ImageIO.read(M.class.getResource(path));}
		catch (IllegalArgumentException e) {S.pln(path + " not found or not readable or SOMETHING"); e.printStackTrace();}
		catch (IOException e) {S.pln(path + " not found or not readable or SOMETHING"); e.printStackTrace();}
		return ret;
	}
	
	static int approx(int i) {return (int)approx((double)i);}
	static double approx(double d) {
		double diff = S.rand.nextGaussian() * 0.1 * d;
		double absd = d < 0.0? -d : d;
		if (diff < absd * -0.2) diff = absd * -0.2;
		else if (diff > absd * 0.2) diff = absd * 0.2;
		return d + diff;
	}
}

class Image {
	private final int[][] dat;
	private final int modX, modY;
	public final int X, Y;
	
	public Image(String path) {this(M.getImage(path));}
	public Image(BufferedImage img) {
		modX = modY = 0;
		X = img.getWidth();
		Y = img.getHeight();
		dat = new int[X][Y];
		for (int x = 0; x < X; x++)
		for (int y = 0; y < Y; y++)
			dat[x][y] = img.getRGB(x, y);
	}
	public Image(int[][] img) {
		modX = modY = 0;
		X = img.length;
		Y = img[0].length;
		dat = img;
	}
	
	public Image getSubImage(int x1, int y1, int x2, int y2)
		{return new Image(this, x1, y1, x2, y2);}
	private Image(Image backingImage, int x1, int y1, int x2, int y2)
	{
		modX = Math.min(x1, x2);
		modY = Math.min(y1, y2);
		X = Math.max(x1, x2) - modX;
		Y = Math.max(y1, y2) - modY;
		dat = backingImage.dat;
	}
	
	public int get(int x, int y)
		{return dat[x + modX][y + modY];}
}

class Pattern
{
	private final Image img;
	private final Point answerMod;
	private final int checkFirst;
	private final int checkFirstX, checkFirstY;
	
	public Pattern(Image pattern) {this(pattern, 0, 0, 0, 0);}
	public Pattern(Image pattern, int answerModX, int answerModY) {this(pattern, answerModX, answerModY, 0, 0);}
	public Pattern(Image pattern, int answerModX, int answerModY, int checkFx, int checkFy)
		{img = pattern; answerMod = new Point(answerModX, answerModY); checkFirst = img.get(checkFirstX = checkFx, checkFirstY = checkFy);}
	
	public Point findIn(Image bigImg)
	{
		for (int x = 0; x < bigImg.X - img.X + 1; x++)
		for (int y = 0; y < bigImg.Y - img.Y + 1; y++)
		if (areColorsSimilar(bigImg.get(x + checkFirstX, y + checkFirstY), checkFirst))
		{
			boolean foundIt = true;
			for (int x2 = 0; foundIt && x2 < img.X; x2++)
			for (int y2 = 0; foundIt && y2 < img.Y; y2++)
			if (img.get(x2, y2) != 0xffff00ff && !areColorsSimilar(bigImg.get(x + x2, y + y2), img.get(x2, y2)))
				foundIt = false;
			if (foundIt)
				return new Point(x + answerMod.x, y + answerMod.y);
		}
		return null;
	}
	
	private static boolean areColorsSimilar(int a, int b)
	{
		int ar = a >> 16 & 0xff,
			ag = a >> 8 & 0xff,
			ab = a & 0xff,
			br = b >> 16 & 0xff,
			bg = b >> 8 & 0xff,
			bb = b & 0xff;
		return 
			ar < br + 8 && ar > br - 8 &&
			ag < bg + 8 && ag > bg - 8 &&
			ab < bb + 8 && ab > bb - 8;
	}
}