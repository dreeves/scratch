package takns;

import lombok.*;
import java.util.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import takns.main.*;
import static takns.main.*;
import takns.units.Building;
import takns.units.MoveableUnit;

public class sprites {

static Side side_BuildIcons;

static BufferedImage icons_getBufferedImage(MoveableUnit vehicle) {
	vehicle.setSide(side_BuildIcons);
	
	val image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
	val g = image.createGraphics();
	vehicle.sprite.renderImageTo(g, 32, 42);
	g.dispose();
	
	return ImageConverter_convert(icons_halfSize(image), 2);
}
static BufferedImage icons_getBufferedImage(Building building) {
	building.setSide(side_BuildIcons);
	
	val image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
	val g = image.createGraphics();
	building.sprite.renderImageTo(g, 32-(building.width-1)*8, 42-(building.height-1)*8);
	g.dispose();
	return ImageConverter_convert(icons_halfSize(image), 2);
}
static BufferedImage icons_halfSize(BufferedImage in) {
	val out = new BufferedImage(in.getWidth() / 2, in.getHeight() / 2+8, BufferedImage.TYPE_INT_ARGB_PRE);

	int[] inPixels = new int[in.getWidth() * in.getHeight()];
	in.getRGB(0, 0, in.getWidth(), in.getHeight(), inPixels, 0, in.getWidth());
	
	int[] outPixels = Voxels.halfSize(inPixels, in.getWidth(), in.getHeight(), 2);

	out.setRGB(0, 0, out.getWidth(), out.getHeight()-8, outPixels, 0, out.getWidth());

	return out;
}
static class MonsterSprites {
	static BufferedImage[] blob;
	static BufferedImage blobShadow;

	static void buildSprites() {buildBlob();}
	static void buildBlob() {
		int rad = 8;
		int size = rad * 3;

		int[] pixels = new int[size * size];

		blob = new BufferedImage[2];
		for (int i = 0; i < 2; i++) {
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					double xx = x - size / 2 + 1;
					double yy = y - size / 2 + 1;
					if (i == 0 && y > size / 2) yy *= 2;
					if (i == 1) {
						yy += rad / 4;
						xx *= 1.2f;
					}

					int d0 = (int) (Math.sqrt(xx * xx + yy * yy) - 0.5f);

					int a = 0;
					int r = 255;
					int g = 255;
					int b = 255;

					if (d0 <= rad) {
						a = d0 * 170 / rad + 85;
						r = 64 - d0 * 25 / rad;
						g = 255 - d0*d0 * 90 / rad/rad;
						b = 64 - d0 * 25 / rad;
					}

					xx = x - (size / 2 - rad / 2);
					yy = y - (size / 2 - rad / 2);
					if (i == 1) {
						yy += rad / 4;
						xx *= 1.2f;
					}

					if (i == 0 && yy > 0) yy *= 2;

					int d1 = (int) (Math.sqrt(xx * xx + yy * yy) - 0.5f);
					
					if (a>0) {
						r = r*(rad*6-d1)/6/rad;
						g = g*(rad*6-d1)/6/rad;
						b = b*(rad*6-d1)/6/rad;
					}

					if (d1 <= rad / 8) {
						int l1 = 128;
						int l2 = 256 - l1;
						a = (a * l2 + 255 * l1) / 256;
						r = (r * l2 + 255 * l1) / 256;
						g = (g * l2 + 255 * l1) / 256;
						b = (b * l2 + 255 * l1) / 256;
					}

					pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
				}
			}


			blob[i] = ImageConverter_convert(size, size, pixels, 2);
		}
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				double xx = x - size / 2 + 1;
				double yy = y - size / 2 + 1;
				yy *= 2;

				int d = (int) (Math.sqrt(xx * xx + yy * yy) - 0.5f);

				int a = 0;
				int r = 0;
				int g = 0;
				int b = 0;

				if (d <= rad) {
					a = 64;
					r = 0;
					g = 0;
					b = 0;
				}

				pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
			}
		}

		blobShadow = ImageConverter_convert(size, size, pixels, 2);
	}
}
static class Sprites {
	static BufferedImage[][] debris;
	static BufferedImage[] whiteSmoke;
	static BufferedImage[] fireSmoke;
	static BufferedImage[] shadow;
	static BufferedImage[] bullet;
	static BufferedImage[] crater;

	static void buildSprites() {
		buildDebris();
		buildWhiteSmoke();
		buildFireSmoke();
		buildShadow();
		buildBullet();
		buildCrater();
	}
	static void buildDebris() {
		int size = 16;
		int[] layer = noiseMap(5, 3);
		int[] pixels = new int[size * size];

		debris = new BufferedImage[terrain.terrains.size()][8];
		for (int t = 0; t < terrain.terrains.size(); t++) {
			int color = terrain.terrains.get(t).color;
			for (int i = 0; i < 8; i++) {
				for (int x = 0; x < size; x++) {
					for (int y = 0; y < size; y++) {
						int xx = x - size / 2;
						int yy = y - size / 2;

						int a = (layer[x + y * 32] + 80) * 4;
						a -= (int) (Math.sqrt(xx * xx + yy * yy) * 315 / (size / 2)) + i * 32;
						if (a > 255) a = 255;
						if (a < 0) a = 0;
						int r = 255 - (x + y) * 80 / (size * 2);
						int g = 255 - (x + y) * 80 / (size * 2);
						int b = 255 - (x + y) * 80 / (size * 2);

						r += (r * ((color >> 16) & 0xff) / 255)*4;
						g += (g * ((color >> 8) & 0xff) / 255)*4;
						b += (b * ((color) & 0xff) / 255)*4;
						r/=7;
						g/=7;
						b/=7;
						pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
					}
				}

				debris[t][i] = ImageConverter_convert(size, size, pixels, 2);
			}
		}
	}
	static void buildFireSmoke() {
		int size = 16;

		int[] pixels = new int[size * size];
		fireSmoke = new BufferedImage[8];
		for (int i = 0; i < 8; i++) {
			int[] layer = noiseMap(5, 3);
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					int xx = x - size / 2;
					int yy = y - size / 2;

					int radius = size / 2 * (i + 1) / 5 + 2;
					int a = 130 - (int) (Math.sqrt(xx * xx + yy * yy) * 155 / radius);
					if (a > 255) a = 255;
					a = a-layer[x+y*32];
					if (a > 255) a = 255;
					if (a < 0) a = 0;

					int r = 155 - (x + y) * 100 / (size * 2) + y * y * 250 / (size * size);
					int g = (155 - (x + y) * 100 / (size * 2))/4 + y * y * 150 / (size * size);
					int b = (155 - (x + y) * 100 / (size * 2))/4;
					
					int col = (a-16)*2;
					if (col>255) col = 255;
					if (col<0) col = 0;
					a = a*4;
					a = a*(7-i)/8;
					if (a>155) a = 155;
					r = r*(255-col)/255;
					g = g*(255-col)/255;
					b = b*(255-col)/255;
					pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
				}
			}

			fireSmoke[i] = ImageConverter_convert(size, size, pixels, 2);
		}
	}
	static void buildWhiteSmoke() {
		int size = 16;

		int[] pixels = new int[size * size];
		whiteSmoke = new BufferedImage[8];
		for (int i = 0; i < 8; i++) {
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					int xx = x - size / 2;
					int yy = y - size / 2;

					int radius = size / 2 * (i + 1) / 5 + 2;
					int a = 155 - (int) (Math.sqrt(xx * xx + yy * yy) * 155 / radius);
					a = a * (7 - i) / 7;
					if (a > 255) a = 255;
					if (a < 0) a = 0;

					int r = 255 - (x + y) * 100 / (size * 2) + y * y * 50 / (size * size);
					int g = 255 - (x + y) * 100 / (size * 2);
					int b = 255 - (x + y) * 100 / (size * 2);
					
					pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
				}
			}

			whiteSmoke[i] = ImageConverter_convert(size, size, pixels, 2);
		}
	}
	static void buildShadow() {
		int size = 16;

		int[] pixels = new int[size * size];
		shadow = new BufferedImage[8];
		for (int i = 0; i < 8; i++) {
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					int xx = x - size / 2;
					int yy = (y - size / 2) * 2;

					int a = 255;
					a -= (int) (Math.sqrt(xx * xx + yy * yy) * 315 / (size / 2)) + i * 32;
					if (a > 40) a = 40;
					if (a < 0) a = 0;
					int r = 0;
					int g = 0;
					int b = 0;

					pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
				}
			}

			shadow[i] = ImageConverter_convert(size, size, pixels, 2);
		}
	}
	static void buildBullet() {
		int size = 16;

		int[] pixels = new int[size * size];
		bullet = new BufferedImage[8];
		for (int i = 0; i < 8; i++) {
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					int xx = x - size / 2;
					int yy = (y - size / 2);

					int a = 255;
					a -= (int) (Math.sqrt(xx * xx + yy * yy) * 315 / (size / 2)) + i * 32;
					a = a * 10;

					xx = (x + 3) - size / 2;
					yy = (y + 3) - size / 2;
					int dd = (int) (Math.sqrt(xx * xx + yy * yy) * 255 / (size * 2));
					if (a > 255) a = 255;
					if (a < 0) a = 0;
					int r = 255 - dd;
					int g = 255 - dd;
					int b = 0;

					pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
				}
			}

			bullet[i] = ImageConverter_convert(size, size, pixels, 2);
		}
	}
	static void buildCrater() {
		int size = 16;

		int[] pixels = new int[size * size];
		crater = new BufferedImage[8];
		for (int i = 0; i < 8; i++) {
			int[] layer = noiseMap(5, 2);
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					int xx = x - size / 2;
					float yy = (y - size / 2) * 1.5f;

					int a = (layer[x + y * 32] + 90) * 2;
					a -= (int) (Math.sqrt(xx * xx + yy * yy) * 200 / (size / 2));

					if (a > 200) a = 200;
					if (a < 0) a = 0;
					int r = 0;
					int g = 0;
					int b = 0;

					pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
				}
			}

			crater[i] = ImageConverter_convert(size, size, pixels, 2);
		}
	}
}
static class Voxels {
	static final float[] SIN = { 0.0f, 0.38f, 0.71f, 0.92f, 1.0f, 0.92f, 0.71f, 0.38f, 0f, -0.38f, -0.71f, -0.92f, -1.0f, -0.92f, -0.71f, -0.38f, };
	static final float[] COS = { 1.0f, 0.92f, 0.71f, 0.38f, 0f, -0.38f, -0.71f, -0.92f, -1.0f, -0.92f, -0.71f, -0.38f, 0.0f, 0.38f, 0.71f, 0.92f, };

	static int[][][] titleImageVoxels = new int[2][][];

	static BufferedImage[] turret;
	static BufferedImage[] turretShadow;

	static BufferedImage[][] tankBase;
	static BufferedImage[] baseShadow;

	static BufferedImage[][] carBase;
	static BufferedImage[] carShadow;

	static BufferedImage[][][] harvesterBase;
	static BufferedImage[] harvesterShadow;

	static BufferedImage[] hqBase;
	static BufferedImage hqShadow;

	static BufferedImage[] wallTurretBase;
	static BufferedImage wallTurretShadow;

	static BufferedImage[] walls;
	static BufferedImage[] wallShadows;

	static final int SIDES = 2;

	static final int[] SIDE_COLORS = { 0xffc00000, 0xff4040e0, 0xff00e000, 0xffe000e0 };
	static final int[] SIDE_COLORS2 = { 0xffff0000, 0xff0000ff, 0xff00ff00, 0xffff00ff };

	static boolean reduced = false;

	static void buildVoxels() {
		buildTurrets();
		buildTanks();

		if (!reduced) {
			buildCars();
			buildHarvesters();
			
			buildHqs();
			buildWallTurrets();
			buildWalls();
		}
	}
	static void buildWalls() {
		walls = new BufferedImage[16];
		wallShadows = new BufferedImage[16];

		for (int i = 0; i < 16; i++) {
			int[][] layers = new int[16][16 * 16];
			if (i != 5 && i != 10) fillBlock(layers, 16, 4, 4, 0, 8, 8, 10, 0xff909090);

			if ((i & 0x01) != 0) fillBlock(layers, 16, 6, 0, 0, 4, 8, 8, 0xff606060);
			if ((i & 0x02) != 0) fillBlock(layers, 16, 0, 6, 0, 8, 4, 8, 0xff606060);
			if ((i & 0x04) != 0) fillBlock(layers, 16, 6, 8, 0, 4, 8, 8, 0xff606060);
			if ((i & 0x08) != 0) fillBlock(layers, 16, 8, 6, 0, 8, 4, 8, 0xff606060);

			walls[i] = voxelizeDir(layers, 16, 0);
			wallShadows[i] = buildShadowDir(layers, 16, 0);
		}
	}
	static void buildWallTurrets() {
		int[][] layers = new int[16][16 * 16];

		wallTurretBase = new BufferedImage[SIDES];
		for (int i = 0; i < SIDES; i++) {
			fillBlock(layers, 16, 2, 2, 0, 12, 12, 10, SIDE_COLORS[i]);
			fillBlock(layers, 16, 6, 0, 0, 4, 16, 8, 0xff606060);
			fillBlock(layers, 16, 0, 6, 0, 16, 4, 8, 0xff606060);

			wallTurretBase[i] = voxelizeDir(layers, 16, 0);
			if (i == 0) wallTurretShadow = buildShadowDir(layers, 16, 0);
		}
	}
	static void buildHqs() {
		int[][] layers = new int[16][32 * 32];

		hqBase = new BufferedImage[SIDES];
		for (int i = 0; i < SIDES; i++) {
			fillBlock(layers, 32, 1, 1, 0, 30, 30, 6, 0xff606060);
			fillBlock(layers, 32, 2, 2, 0, 28, 28, 6, 0);
			fillBlock(layers, 32, 7, 2, 0, 8, 30, 6, 0);

			fillBlock(layers, 32, 0, 0, 0, 3, 3, 7, 0xff606060);
			fillBlock(layers, 32, 29, 0, 0, 3, 3, 7, 0xff606060);
			fillBlock(layers, 32, 0, 29, 0, 3, 3, 7, 0xff606060);
			fillBlock(layers, 32, 29, 29, 0, 3, 3, 7, 0xff606060);

			fillBlock(layers, 32, 4, 5, 0, 32 - 10, 10, 10, SIDE_COLORS[i]);
			fillBlock(layers, 32, 6, 5, 0, 10, 18, 8, SIDE_COLORS[i]);
			fillBlock(layers, 32, 7, 7, 0, 1, 1, 14, 0xffffffff);
			fillBlock(layers, 32, 9, 7, 0, 1, 1, 13, 0xffffffff);

			fillBlock(layers, 32, 26, 26, 12, 6, 1, 4, SIDE_COLORS[i]);
			fillBlock(layers, 32, 26, 26, 0, 1, 1, 16, 0xffd0d0a0);

			hqBase[i] = voxelizeDir(layers, 32, 0);
			if (i == 0) hqShadow = buildShadowDir(layers, 32, 0);
		}
	}
	static void buildTurrets() {
		int[][] layers = new int[8][16 * 16];

		fillBlock(layers, 16, 6, 0, 0, 4, 8, 3, 0xff505060);
		fillBlock(layers, 16, 7, 0, 1, 2, 8, 1, 0xff000000);
		fillBlock(layers, 16, 4, 8, 0, 8, 8, 3, 0xffb0b0c0);

		titleImageVoxels[1] = layers;

		turret = voxelize(layers, 16, 16);
		turretShadow = buildShadows(layers, 16, 16);
	}
	static void buildTanks() {
		tankBase = new BufferedImage[SIDES][];
		for (int i = 0; i < SIDES; i++) {
			int[][] layers = new int[8][16 * 16];
			fillBlock(layers, 16, 3, 1, 1, 10, 14, 3, SIDE_COLORS[i]);

			fillBlock(layers, 16, 1, 2, 0, 2, 14, 4, 0xff808080);
			fillBlock(layers, 16, 13, 2, 0, 2, 14, 4, 0xff808080);
			fillBlock(layers, 16, 1, 0, 2, 2, 2, 2, 0xff808080);
			fillBlock(layers, 16, 13, 0, 2, 2, 2, 2, 0xff808080);

			if (i == 0) titleImageVoxels[0] = layers;

			tankBase[i] = voxelize(layers, 16, 16);
			if (i == 0) baseShadow = buildShadows(layers, 16, 16);
		}
	}
	static void buildHarvesters() {
		harvesterBase = new BufferedImage[3][SIDES][];
		for (int i = 0; i < SIDES; i++) {
			for (int m = 0; m < 3; m++) {
				int[][] layers = new int[8][16 * 16];
				fillBlock(layers, 16, 3, 0, 1, 10, 15, 3, SIDE_COLORS[i]);
				fillBlock(layers, 16, 3, 4 - 3, 1 + 3, 10, 8, 1, 0xff303030);
				fillBlock(layers, 16, 3, 4, 1, 10, 8 + 4, 6, SIDE_COLORS[i]);

				fillBlock(layers, 16, 4, 8, 1, 8, 8, 6, 0);
				if (m == 0) fillBlock(layers, 16, 4, 8, 1, 8, 8, 3, 0xff505050);
				else if (m == 1) fillBlock(layers, 16, 4, 8, 1, 8, 8, 4, 0xff805080);
				else fillBlock(layers, 16, 4, 8, 1, 8, 8, 6, 0xffe080e0);

				fillBlock(layers, 16, 1, 2, 0, 2, 4, 3, 0xff808080);
				fillBlock(layers, 16, 13, 2, 0, 2, 4, 3, 0xff808080);

				fillBlock(layers, 16, 1, 7, 0, 2, 4, 3, 0xff808080);
				fillBlock(layers, 16, 13, 7, 0, 2, 4, 3, 0xff808080);

				fillBlock(layers, 16, 1, 12, 0, 2, 4, 3, 0xff808080);
				fillBlock(layers, 16, 13, 12, 0, 2, 4, 3, 0xff808080);

				harvesterBase[m][i] = voxelize(layers, 16, 16);
				if (i == 0 && m == 0) harvesterShadow = buildShadows(layers, 16, 16);
			}
		}
	}
	static void buildCars() {
		carBase = new BufferedImage[SIDES][];
		for (int i = 0; i < SIDES; i++) {
			int[][] layers = new int[8][16 * 16];
			fillBlock(layers, 16, 3, 1, 1, 10, 14, 3, SIDE_COLORS[i]);
			fillBlock(layers, 16, 3, 10 - 3, 1 + 3, 10, 6, 1, 0xff303030);
			fillBlock(layers, 16, 3, 10, 1, 10, 6, 4, SIDE_COLORS[i]);

			fillBlock(layers, 16, 5, 0, 4, 2, 6, 1, 0xff606060);
			fillBlock(layers, 16, 9, 0, 4, 2, 6, 1, 0xff606060);

			fillBlock(layers, 16, 1, 1, 0, 2, 4, 3, 0xff808080);
			fillBlock(layers, 16, 13, 1, 0, 2, 4, 3, 0xff808080);

			fillBlock(layers, 16, 1, 11, 0, 2, 5, 4, 0xff808080);
			fillBlock(layers, 16, 13, 11, 0, 2, 5, 4, 0xff808080);

			carBase[i] = voxelize(layers, 16, 16);
			if (i == 0) carShadow = buildShadows(layers, 16, 16);
		}
	}
	static void fillBlock(int[][] pixels, int size, int x0, int y0, int z0, int xs, int ys, int zs, int color) {
		for (int z = z0; z < z0 + zs; z++)
			for (int y = y0; y < y0 + ys; y++)
				for (int x = x0; x < x0 + xs; x++)
					pixels[z][x + y * size] = color;
	}
	static BufferedImage[] voxelize(int[][] layers, int size, int dirs) {
		if (reduced) return null;

		BufferedImage[] images = new BufferedImage[dirs];
		for (int dir = 0; dir < dirs; dir++) images[dir] = voxelizeDir(layers, size, dir);
		return images;
	}
	static BufferedImage voxelizeDir(int[][] layers, int size, int dir) {
		if (reduced) return null;

		int scale = 1;
		int newSize = size * 3 / 2 * scale;
		float sin = SIN[dir];
		float cos = COS[dir];

		int imgWidth = newSize;
		int imgHeight = newSize + layers.length * scale;
		int[] pixels = new int[imgWidth * imgHeight];
		int yo = layers.length * scale;

		for (int ll = 0; ll < layers.length * scale; ll++) {
			int l = ll / scale;
			int[] input = layers[l];
			for (int y = 0; y < newSize; y++) {
				for (int x = 0; x < newSize; x++) {
					float _x = (x - (newSize / 2f - 0.5f * scale)) / scale;
					float _y = (y - (newSize / 2f - 0.5f * scale)) / scale;

					int xi = (int) ((cos * _x + sin * _y + (size / 2f - 0.5f)));
					int yi = (int) ((cos * _y - sin * _x + (size / 2f - 0.5f)));

					boolean above = false;
					boolean diagonalAbove = false;
					if (l < layers.length - 1) {
						if (xi >= 0 && yi >= 0 && xi < size && yi < size && (layers[l + 1][xi + yi * size] & 0xff000000) != 0) above = true;
						float _x2 = (x - (newSize / 2f - 0.5f) - 1 * scale) / scale;
						float _y2 = (y - (newSize / 2f - 0.5f) - 1 * scale) / scale;
						int xi2 = (int) ((cos * _x2 + sin * _y2 + (size / 2f - 0.5f * scale)));
						int yi2 = (int) ((cos * _y2 - sin * _x2 + (size / 2f - 0.5f * scale)));
						if (xi2 >= 0 && yi2 >= 0 && xi2 < size && yi2 < size && (layers[l + 1][xi2 + yi2 * size] & 0xff000000) != 0) diagonalAbove = true;
					}
					int color = 255;
					if (above) color = 200;
					if (diagonalAbove) color = 128;

					if (xi >= 0 && yi >= 0 && xi < size && yi < size && (input[xi + yi * size] & 0xff000000) != 0) {
						int a = 255;
						int r = (input[xi + yi * size] >> 16) & 0xff;
						int g = (input[xi + yi * size] >> 8) & 0xff;
						int b = (input[xi + yi * size]) & 0xff;

						r = r * color / 255;
						g = g * color / 255;
						b = b * color / 255;
						pixels[x + (y + yo - ll) * imgWidth] = (a << 24) | (r << 16) | (g << 8) | b;
					}
				}
			}
		}

		BufferedImage res;
		if (scale == 2) {
			int[] p2 = halfSize(pixels, imgWidth, imgHeight, scale);
			res = new BufferedImage(imgWidth / scale, imgHeight / scale, BufferedImage.TYPE_INT_ARGB_PRE);
			res.setRGB(0, 0, imgWidth / scale, imgHeight / scale, p2, 0, imgWidth / scale);
			res = ImageConverter_convert(res, 1);
		} else res = ImageConverter_convert(imgWidth, imgHeight, pixels, 1);

		return res;
	}
	static int[] halfSize(int[] pixels, int width, int height, int scale) {
		if (reduced) return null;

		int[] result = new int[(width / scale) * (height / scale)];
		for (int x = 0; x < width; x += scale) {
			for (int y = 0; y < height; y += scale) {
				int r = 0;
				int g = 0;
				int b = 0;
				int a = 0;
				int samples = 0;
				for (int xx = x; xx < x + scale; xx++)
					for (int yy = y; yy < y + scale; yy++) {
						int aa = (pixels[xx + yy * width] >> 24) & 0xff;
						int rr = (pixels[xx + yy * width] >> 16) & 0xff;
						int gg = (pixels[xx + yy * width] >> 8) & 0xff;
						int bb = (pixels[xx + yy * width]) & 0xff;
						a += aa;
						if (aa > 0) {
							r += rr;
							g += gg;
							b += bb;
							samples++;
						}
					}

				int col = 0;
				if (samples > 0) {
					a /= samples;
					r /= samples;
					g /= samples;
					b /= samples;
					col = (a << 24) | (r << 16) | (g << 8) | b;
				}

				result[(x / scale) + (y / scale) * (width / scale)] = col;
			}
		}
		return result;
	}
	static BufferedImage[] buildShadows(int[][] layers, int size, int dirs) {
		if (reduced) return null;

		BufferedImage[] images = new BufferedImage[dirs];
		for (int dir = 0; dir < dirs; dir++) images[dir] = buildShadowDir(layers, size, dir);
		return images;
	}
	static BufferedImage buildShadowDir(int[][] layers, int size, int dir) {
		if (reduced) return null;

		int newSize = size * 3 / 2;
		float sin = SIN[dir];
		float cos = COS[dir];

		int imgWidth = newSize + layers.length / 2;
		int imgHeight = newSize + layers.length;
		int[] pixels = new int[imgWidth * imgHeight];

		for (int l = 0; l < layers.length; l++) {
			int[] input = layers[l];
			for (int y = 0; y < newSize; y++) {
				for (int x = 0; x < newSize; x++) {
					float _x = x - (newSize / 2f - 0.5f);
					float _y = y - (newSize / 2f - 0.5f);

					int xi = (int) (cos * _x + sin * _y + (size / 2f - 0.5f));
					int yi = (int) (cos * _y - sin * _x + (size / 2f - 0.5f));
					if (xi >= 0 && yi >= 0 && xi < size && yi < size && (input[xi + yi * size] & 0xff000000) != 0) 
						pixels[(x + l / 2) + (y + l) * imgWidth] = 0x40000000;
				}
			}
		}

		val res = ImageConverter_convert(imgWidth, imgHeight, pixels, 2);
		return res;
	}
}

}