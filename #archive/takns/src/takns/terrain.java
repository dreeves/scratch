package takns;

import lombok.*;
import java.util.*;
import java.awt.image.BufferedImage;

import takns.main.*;
import static takns.main.*;

public class terrain {

static int
    PASSABLE_AIR = 0x0001,
	PASSABLE_LAND = 0x0002,
	PASSABLE_WATER = 0x0004;

static List<Terrain> terrains = l();
static Terrain
    WATER = new Terrain(0, "water", 0xff7785da, PASSABLE_AIR | PASSABLE_WATER, 10),
    SHORE = new Terrain(1, "shore", 0xff5765ba, PASSABLE_AIR | PASSABLE_WATER, 12),
    SAND  = new Terrain(2, "sand", 0xffc6b664, PASSABLE_AIR | PASSABLE_LAND, 15),
    GRASS = new Terrain(3, "grass", 0xff6ba863, PASSABLE_AIR | PASSABLE_LAND, 10),
    SLAB  = new Terrain(4, "slab", 0xffb0b0b0, PASSABLE_AIR | PASSABLE_LAND, 8),
    GEM   = new Terrain(5, "gems", 0xffdd5dec, PASSABLE_AIR | PASSABLE_LAND, 12);

static class Map {
	int[] tiles;
	units.Unit[] blockMap;
	
	int width = 64;
	int height = 64;

	Map() {
		tiles = new int[64 * 64];
		blockMap = new units.Unit[64 * 64];

		addNoise(3, -10, ctiles.TYPE_WATER_TO_SAND);
		addNoiseOver(ctiles.TYPE_WATER, 3, -5, ctiles.TYPE_WATER_TO_SAND); // RIVERS!
		addNoiseOver(ctiles.TYPE_SAND, 3, 0, ctiles.TYPE_SAND_TO_GRASS);

		splatOver(ctiles.TYPE_SAND, ctiles.TYPE_GEMS, 8, 20, 32, 4);
		splatDecorators(ctiles.TYPE_GRASS, ctiles.TYPE_GRASS_DECORATORS, 8, 3000);
		splatDecorators(ctiles.TYPE_SAND, ctiles.TYPE_SAND_DECORATORS, 8, 300);
	}
	void splatDecorators(int tile, int type, int typeCount, int count) {
		for (int i = 0; i < count; i++) {
			int x = rand.nextInt(64);
			int y = rand.nextInt(64);
			if (tiles[x + y * 64] == tile) 
				tiles[x + y * 64] = type + rand.nextInt(typeCount);
		}
	}
	void splatOver(int tile, int type, int types, int count, int strands, int spread) {
		for (int i = 0; i < count; i++) {
			int xo = rand.nextInt(64);
			int yo = rand.nextInt(64);
			if (tiles[xo + yo * 64] == tile) {
				for (int s = 0; s < strands; s++) {
					int x = xo;
					int y = yo;
					for (int k = 0; k < spread; k++) {
						x += rand.nextInt(7) - 3;
						y += rand.nextInt(7) - 3;
						if (x >= 0 && y >= 0 && x < 64 && y < 64 && (tiles[x + y * 64] == tile || (tiles[x + y * 64] >= type && tiles[x + y * 64] < type + types))) 
							tiles[x + y * 64] = type + rand.nextInt(types);
					}
				}
			}
		}
	}
	void addNoise(int noise, int limit, int offset) {addNoise(noiseMap(7, noise), limit, offset);}
	void addNoise(int[] layer, int limit, int offset) {
		for (int y = 0; y < 64; y++) {
			for (int x = 0; x < 64; x++) {
				int tile = 0;
				for (int i = 0; i < 4; i++)
					if (layer[(x + (i & 1) + (y + (i >> 1)) * 128)] > limit)
						tile += 1 << i;

				if (tile > 0) tiles[x + y * 64] = tile + offset;
			}
		}
	}
	void addNoiseOver(int tile, int noise, int limit, int offset) {
		int[] layer = noiseMap(7, 3);
		for (int y = 0; y < 64; y++) {
			for (int x = 0; x < 64; x++) {
				if (tiles[x + y * 64] != tile) {
					layer[x + y * 128] = -200;
					layer[x + y * 128 + 1] = -200;
					layer[x + y * 128 + 128] = -200;
					layer[x + y * 128 + 129] = -200;
				}
			}
		}

		addNoise(layer, limit, offset);
	}
	Terrain getTerrainTypeAtPixel(float x, float y) {return getTerrainTypeAt((int) (x / 16), (int) (y / 16));}
	Terrain getTerrainTypeAt(int xTile, int yTile) {
		if (xTile < 0) xTile = 0;
		if (yTile < 0) yTile = 0;
		if (xTile >= width) xTile = width - 1;
		if (yTile >= height) yTile = height - 1;
		return ctiles.tiles[tiles[xTile + yTile * width]].terrain;
	}	

	void block(int x, int y, units.Unit unit) {blockMap[x + y * width] = unit;}
	void unblock(int x, int y) {blockMap[x + y * width] = null;}
	units.Unit getUnitAt(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) return null;
		return blockMap[x + y * width];
	}
	int getTile(int xTile, int yTile) {return tiles[xTile + yTile * width];}
	void setTile(int xTile, int yTile, int tile) {
		tiles[xTile + yTile * width] = tile;
		world.minimap_updateArea(xTile, yTile, 1, 1);
	}
}
static class Terrain {
	String name;
	int color;
	int passableFlags;
	int travelCost;
    
    int id;

	Terrain(int id, String name, int color, int passableFlags, int travelCost) {
        terrains.add(this);
		this.name = name;
		this.color = color;
		this.passableFlags = passableFlags;
		this.travelCost = travelCost;
        
        this.id = id;
	}
}
static class Tile {
	BufferedImage image;
	Terrain terrain;

	Tile(int[] pixels, int width, int height, Terrain terrain) {
		boolean hasAlpha = false;
		for (int i = 0; i < width * height && !hasAlpha; i++)
			if ((pixels[i] & 0xff000000) != 0xff000000) hasAlpha = true;
		

		val image = ImageConverter_convert(width, height, pixels, hasAlpha?2:0);

		this.image = image;
		this.terrain = terrain;
	}
}
static class ctiles {
	static final int METHOD_NONE = 0;
	static final int METHOD_DARKEN = 1;
	static final int METHOD_SHORE = 2;
	static final int METHOD_NOISE = 3;
	static final int METHOD_LERP = 4;

	static final int TYPE_WATER = 0;
	static final int TYPE_WATER_TO_SAND = 0;
	static final int TYPE_SAND = 15;
	static final int TYPE_SAND_TO_GRASS = 15;
	static final int TYPE_GRASS = 30;

	static final int TYPE_GRASS_DECORATORS = 32;
	static final int TYPE_SAND_DECORATORS = 40;

	static final int TYPE_SLAB = 64;
	static final int TYPE_GEMS = 65;

	static Tile[] tiles = new Tile[256];

	static void createTiles() {
		int[] water = generateWater();
		int[] sand = generateSand();
		int[] grass = generateGrass();
		int[] black = generateSolid(255, 0, 0, 0);
		int[] fog = generateSolid(140, 0, 0, 0);
		int[] transparent = generateSolid(0, 0, 0, 0);
		int[] slab = generateSlab();

		createTiles(TYPE_WATER_TO_SAND, water, METHOD_SHORE, WATER, 192 + 16, sand, METHOD_DARKEN, SHORE);
		createTiles(TYPE_SAND_TO_GRASS, sand, METHOD_NOISE, SAND, 64 - 16, grass, METHOD_DARKEN, GRASS);


		createTiles(256 - 32, fog, METHOD_LERP, null, 192, transparent, METHOD_NONE, null);
		createTiles(256 - 16, black, METHOD_LERP, null, 192, transparent, METHOD_NONE, null);

		createGems(TYPE_GEMS, GEM);
		createSandDecorators(TYPE_SAND_DECORATORS, SAND);
		createGrassDecorators(TYPE_GRASS_DECORATORS, GRASS);

		createTile(TYPE_SLAB, slab, SLAB);
	}
	static void createSandDecorators(int offset, Terrain terrain) {
		for (int i = 0; i < 8; i++) {
			int[] pixels = generateSand();

			for (int j = 0; j < i / 4 + 1; j++) {
				int r = rand.nextInt(8) + 4;
				int x = rand.nextInt(17 - r);
				int y = rand.nextInt(17 - r);

				for (int xx = x; xx < x + r; xx++)
					for (int yy = y; yy < y + r; yy++) {
						float xd = (xx - (x + r / 2.0f));
						float yd = (yy - (y + r / 2.0f));
						float d = (xd * xd + yd * yd);

						float xd2 = ((xx - 1) - (x + r / 2.0f));
						float yd2 = ((yy - 1) - (y + r / 2.0f));
						float d2 = (xd2 * xd2 + yd2 * yd2);

						if (d < r * r / 4.0f) {
							if (d > (r - 3) * (r - 3) / 4.0f) {
								float dd = d;
								d = d2;
								d2 = dd;
							}

							if (d < d2)
								lerp(pixels, (xx) + (yy) * 16, 0xff808040, (int) (64 * d / (r * r / 4.0f)) + 16);
							else
								lerp(pixels, (xx) + (yy) * 16, 0xffffffcf, (int) (64 * d2 / (r * r / 4.0f)) + 16);
						}
					}
			}

			tiles[i + offset] = new Tile(pixels, 16, 16, terrain);
		}
	}
	static void createGems(int offset, Terrain terrain) {
		for (int i = 0; i < 8; i++) {
			int[] pixels = generateSand();

			int gemColor = lerp(0xffdd5dec, 0xff707070, (7 - i) * 24);
			int highlightColor = lerp(0xffffffff, gemColor, (7 - i) * 16);
			int shadeColor = lerp(gemColor, 0xff000000, 64);

			for (int y = 0; y < 14; y++) {
				for (int x = 0; x < 14; x++) {
					if (rand.nextInt(64) < i + 3) {
						lerp(pixels, (x + 0) + (y + 0) * 16, highlightColor, 255);
						lerp(pixels, (x + 1) + (y + 0) * 16, gemColor, 255);
						lerp(pixels, (x + 0) + (y + 1) * 16, shadeColor, 255);
						lerp(pixels, (x + 1) + (y + 1) * 16, shadeColor, 255);
						lerp(pixels, (x + 2) + (y + 1) * 16, 0xff000000, 32);
						lerp(pixels, (x + 1) + (y + 2) * 16, 0xff000000, 32);
						lerp(pixels, (x + 2) + (y + 2) * 16, 0xff000000, 32);
					}
				}
			}

			tiles[i + offset] = new Tile(pixels, 16, 16, terrain);
		}
	}
	static void createGrassDecorators(int offset, Terrain terrain) {
		for (int i = 0; i < 8; i++) {
			int[] pixels = generateGrass();

			for (int j = 0; j < (i + 1) * 4; j++) {
				int x = rand.nextInt(15);
				int y = rand.nextInt(14);

				int flowerColor = 0xffc0ffc0;
				lerp(pixels, (x + 0) + (y + 0) * 16, 0xffcfffcf, 64);
				lerp(pixels, (x + 0) + (y + 1) * 16, flowerColor, 64);
				lerp(pixels, (x + 0) + (y + 2) * 16, 0xff000000, 16);
				lerp(pixels, (x + 1) + (y + 2) * 16, 0xff000000, 16);
			}

			tiles[i + offset] = new Tile(pixels, 16, 16, terrain);
		}
	}
	static int[] generateSolid(int a, int r, int g, int b) {
		int[] pixels = new int[16 * 16];

		for (int y = 0; y < 16; y++)
			for (int x = 0; x < 16; x++)
				pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
		return pixels;
	}
	static int[] generateGrass() {
		int[] pixels = new int[16 * 16];

		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int c = rand.nextInt(32);
				c = c * rand.nextInt(256) / 255;
				int a = 255;
				int r = ((GRASS.color >> 16) & 0xff) + c;
				int g = ((GRASS.color >> 8) & 0xff) + c;
				int b = ((GRASS.color >> 0) & 0xff) + c;

				pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
				if (rand.nextInt(16) == 0 && x > 1) {
					pixels[x + y * 16 - 1] = (a << 24) | (r << 16) | (g << 8) | (b);
					pixels[x + y * 16 - 2] = (a << 24) | (r << 16) | (g << 8) | (b);
				}
			}
		}
		return pixels;
	}
	static int[] generateSand() {
		int[] pixels = new int[16 * 16];

		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int c = rand.nextInt(16);
				int a = 255;
				int r = ((SAND.color >> 16) & 0xff) + c;
				int g = ((SAND.color >> 8) & 0xff) + c;
				int b = ((SAND.color >> 0) & 0xff) + c;

				pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
			}
		}
		return pixels;
	}
	static int[] generateWater() {
		int[] pixels = new int[16 * 16];
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int a = 255;
				int c = rand.nextInt(16);
				int r = ((WATER.color >> 16) & 0xff) + c;
				int g = ((WATER.color >> 8) & 0xff) + c;
				int b = ((WATER.color >> 0) & 0xff) + c;

				pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
			}
		}
		return pixels;
	}
	static int[] generateSlab() {
		int[] pixels = new int[16 * 16];
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				int a = 255;
				int c = rand.nextInt(8);
				if ((x & 7) == 0 || (y & 7) == 0) c -= 20;
				if ((x & 7) == 7 || (y & 7) == 7) c += 20;
				int r = 200 - (c + (x&7) + (y&7));
				int g = 200 - (c + (x&7) + (y&7));
				int b = 200 - (c + (x&7) + (y&7));

				pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
			}
		}
		return pixels;
	}
	static void createTile(int offset, int[] pixels, Terrain terrain) {tiles[offset] = new Tile(pixels, 16, 16, terrain);}
	static void createTiles(int offset, int[] source, int method0, Terrain firstTerrain, int limit, int[] dest, int method1, Terrain otherTerrain) {
		int[] hm = noiseMap(4, 1);

		for (int i = 0; i < 16; i++) {
			int h0 = ((i >> 0) & 1) * (256 + 64);
			int h1 = ((i >> 1) & 1) * (256 + 64);
			int h2 = ((i >> 2) & 1) * (256 + 64);
			int h3 = ((i >> 3) & 1) * (256 + 64);

			int[] pixels = new int[16 * 16];

			for (int y = 15; y >= 0; y--) {
				int hh0 = h0 + (h2 - h0) * y / 16;
				int hh1 = h1 + (h3 - h1) * y / 16;

				for (int x = 0; x < 16; x++) {
					int hh = hh0 + (hh1 - hh0) * x / 16 - 32;
					hh += hm[x + y * 16] / 3;
					int color, color2, p, m;

					if (hh < limit) {
						p = hh * 256 / limit;
						color = source[x + y * 16];
						color2 = dest[x + y * 16];
						m = method0;
					} else {
						p = 255 - ((hh - limit) * 256 / (256 - limit));
						color = dest[x + y * 16];
						color2 = source[x + y * 16];
						m = method1;
					}

					switch (m) {
						case METHOD_DARKEN:
							color = lerp(color, 0xff000000, p / 2 - 64);
							break;
						case METHOD_SHORE:
							if (p > 128) color = lerp(color, lerp(color2, 0xff000000, 128), p - 64);
							break;
						case METHOD_NOISE:
							color = lerp(color, lerp(color2, 0xff000000, 64), rand.nextInt(255) > p + 64 ? 0 : (p + 64));
							break;
						case METHOD_LERP:
							color = lerp(color, color2, p);
							break;
					}

					pixels[x + y * 16] = color;
				}
			}

			tiles[i + offset] = new Tile(pixels, 16, 16, i == 0 ? firstTerrain : otherTerrain);
		}
	}
	static void lerp(int[] pixels, int pos, int to, int a) {pixels[pos] = lerp(pixels[pos], to, a);}
	static int lerp(int from, int to, int a) {
		if (a < 0) a = 0;
		if (a > 255) a = 255;
		int aSource = (from >> 24) & 0xff;
		int rSource = (from >> 16) & 0xff;
		int gSource = (from >> 8) & 0xff;
		int bSource = (from) & 0xff;

		int aDest = (to >> 24) & 0xff;
		int rDest = (to >> 16) & 0xff;
		int gDest = (to >> 8) & 0xff;
		int bDest = (to) & 0xff;

		int aRes = aSource + (aDest - aSource) * a / 255;
		int rRes = rSource + (rDest - rSource) * a / 255;
		int gRes = gSource + (gDest - gSource) * a / 255;
		int bRes = bSource + (bDest - bSource) * a / 255;

		return (aRes << 24) | (rRes << 16) | (gRes << 8) | bRes;
	}
}

}