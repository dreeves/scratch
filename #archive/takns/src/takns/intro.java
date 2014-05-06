package takns;

import lombok.*;
import java.util.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.awt.Color;
import java.awt.Graphics;
import javax.imageio.ImageIO;

import takns.main.*;
import static takns.main.*;
import takns.gui.Text;

public class intro {

static class MojangLogo {
    static int[][] iparts = {
            {81,81,82,82,83,85,86,88,92,95,97,100,103,106,109,112,114,116,117,119,120,122,123,124,125,126,127,128,112,112,112,111,111,110,109,108,106,104,102,100,96,93,89,83,78,72,67,62,56,51,47,43,39,35,32,30,28,27,27,28,31,36,42,50,59,68,77,86,96,104,112,118,123,126,128,127,127,127,127,127,126,126,125,124,122,121,119,117,115,112,19,16,14,12,10,8,7,6,5,5,4,4,4,3,3,3,3,4,4,5,6,7,8,10,11,13,14,15,16,17,18,19,50,53,56,59,61,64,67,69,71,73,75,77,78,79,80,81},
            {37,30,25,22,21,21,22,24,30,33,36,38,39,40,39,37,36,36,38,41,45,49,54,60,65,75,89,90,82,81,80,78,77,75,73,71,68,66,63,61,58,55,52,49,47,45,44,43,44,44,46,48,50,53,56,60,65,70,75,85,93,100,106,110,113,115,116,116,116,116,115,114,113,113,112,113,114,115,116,117,119,120,121,123,124,125,126,127,127,128,128,127,127,126,125,124,123,121,120,119,117,116,115,114,113,112,37,34,32,30,28,27,26,25,24,23,23,23,22,22,22,22,22,22,23,23,24,26,27,28,30,31,32,34,35,36,37,37},
            {97,97,97,97,98,98,98,99,99,100,100,101,102,102,103,104,105,106,107,108,108,109,109,110,110,111,111,112,112,112,112,112,112,112,112,111,110,109,108,107,106,105,104,103,102,101,100,99,98,98,97,97,97},
            {22,21,20,18,17,15,13,11,9,7,6,4,2,1,0,0,0,0,1,2,4,6,7,9,11,13,15,17,18,20,21,22,23,24,25,26,27,28,29,29,29,30,30,29,29,29,28,27,26,25,24,23,22}
        };
	
	static int[][] xs = new int[2][];
	static int[][] ys = new int[2][];
	
	static {
        xs[0] = new int[iparts[0].length];
        ys[0] = new int[iparts[0].length];
        xs[1] = new int[iparts[2].length];
        ys[1] = new int[iparts[2].length];
        
        for (int j=0; j<xs[0].length; j++) {
            xs[0][j] = iparts[0][j]+(SCREEN_WIDTH-128)/2;
            ys[0][j] = iparts[1][j]+(SCREEN_HEIGHT-128)/2-12;
        }

        
        for (int j=0; j<xs[1].length; j++) {
            xs[1][j] = iparts[2][j]+(SCREEN_WIDTH-128)/2;
            ys[1][j] = iparts[3][j]+(SCREEN_HEIGHT-128)/2-12;
        }
	}
    
	static void render(Graphics g) {
		g.setColor(new Color(0xff5a5155));
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		g.setColor(new Color(0x40000000));
		g.fillPolygon(xs[0], ys[0], xs[0].length);
		g.fillPolygon(xs[1], ys[1], xs[1].length);
		
		g.translate(-1, -1);
		
		g.setColor(Color.WHITE);
		g.fillPolygon(xs[0], ys[0], xs[0].length);
		g.fillPolygon(xs[1], ys[1], xs[1].length);

		val t = "MOJANG SPECIFICATIONS";
		Text.drawString(t, g, SCREEN_WIDTH/2-t.length()*3+2, SCREEN_HEIGHT/2+58);
	}
}
static class TitleBuilder {
	int width = SCREEN_WIDTH;
	int height = SCREEN_HEIGHT;
	
	double angle = 0.2;
	double angle2 = -0.8;
	double cutOff = 0.30;
	double xOffset = 100;
	double yOffset = 100;
	
	String[] firstParts = {"The building", "The creation", "The fall", "The rise", "The era", "The age", "The weapons", "The plans", "The tale", "The end", "The dawn", "The rule"};

	String[] secondParts = {"a dynasty", "mankind", "a species", "a social construct", "robots", "machines", "androids", "a corporation", "two cities", "a madman", "a fat man"};

	String titleString = "Takns";

	BufferedImage buildTitleImage() {
		String subString = firstParts[rand.nextInt(firstParts.length)];
		subString += " of ";
		subString += secondParts[rand.nextInt(secondParts.length)];
        val mtMtfi = make_terrain_Map_tiles_for_intro();
		
		angle2 = -rand.nextDouble();

		val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] pixels = new int[width * height];

		double sin = Math.sin(angle);
		double cos = Math.cos(angle);

		int[][] tileImages = new int[256][];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (y > height * cutOff) {
					double d = (4000*height/240) / (y - height * cutOff);

					int fog = (int) (2000 / (Math.sqrt(d)));
					if (fog > 256) fog = 256;

					double xo = (x - width / 2.0) / width * 2 * d;
					double yo = d;

					double xp = cos * xo - sin * yo + xOffset;
					double yp = cos * yo + sin * xo + yOffset;

					int X = ((int) xp) & (64 * 16 - 1);
					int Y = ((int) yp) & (64 * 16 - 1);

					int txTile = X / 16;
					int tyTile = Y / 16;

					X -= txTile * 16;
					Y -= tyTile * 16;

					int tile = mtMtfi[txTile + tyTile * 64];
					if (tileImages[tile] == null) {
						tileImages[tile] = new int[16 * 16];
						terrain.ctiles.tiles[tile].image.getRGB(0, 0, 16, 16, tileImages[tile], 0, 16);
					}
					int color = tileImages[tile][X + Y * 16];

					int r = (color >> 16) & 0xff;
					int g = (color >> 8) & 0xff;
					int b = (color) & 0xff;

					r = r * fog / 256;
					g = g * fog / 256;
					b = b * fog / 256;
					pixels[x + y * width] = (255 << 24) | (r << 16) | (g << 8) | b;
				} else {
					int fog = (int) (256 - (y - height * cutOff));
					int r = 0;
					int g = 0;
					int b = fog;
					pixels[x + y * width] = (255 << 24) | (r << 16) | (g << 8) | b;
				}
			}
		}

		double sin2 = Math.sin(angle2);
		double cos2 = Math.cos(angle2);

		for (int y = 0; y < height; y++) {
			for (int l = 0; l < 2; l++) {
				for (int z = 0; z < 8; z++) {
					for (int x = 0; x < width; x++) {
						if (y > height * cutOff) {
							double d = (2000*height/240) / (y - height * cutOff);

							int fog = 255;
							if (fog > 256) fog = 256;

							double xo = (x - width / 2.0) / width * 2 * d;
							double yo = d;

							double xp = cos * xo - sin * yo + 18;
							double yp = cos * yo + sin * xo - 13;

							if (l == 1) {
								xp -= 8;
								yp -= 8;

								double xx = cos2 * xp - sin2 * yp;
								double yy = cos2 * yp + sin2 * xp;

								xp = xx + 8;
								yp = yy + 8;
							}

							int X = ((int) xp);
							int Y = ((int) yp);

							if (X >= 0 && X < 16 && Y >= 0 && Y < 16) {
								val above = z < 7 && (X >= 0 && Y >= 0 && X < 16 && Y < 16 && (sprites.Voxels.titleImageVoxels[l][z + 1][X + Y * 16] & 0xff000000) != 0);

								int color = sprites.Voxels.titleImageVoxels[l][z][X + Y * 16];
								if (color != 0) {
									int col = 200;

									int hh0 = (int) (height*2/3 * (z + l * 4) / d);
									int hh1 = (int) (height*2/3 * (z + 1 + l * 4) / d);

									for (int yy = y - hh0; yy >= y - hh1; yy--) {
										if (yy == y - hh1) if (!above) col = 255;

										int r = (color >> 16) & 0xff;
										int g = (color >> 8) & 0xff;
										int b = (color) & 0xff;
										r = r * fog / 256 * col / 256;
										g = g * fog / 256 * col / 256;
										b = b * fog / 256 * col / 256;
										if (yy >= 0 && yy < height) pixels[x + yy * width] = (255 << 24) | (r << 16) | (g << 8) | b;
									}
								}
							}
						}
					}
				}
			}
		}

		image.setRGB(0, 0, width, height, pixels, 0, width);
		
		val gr = image.createGraphics();
		val tf = gr.getTransform();
		gr.translate(width/2, 40);
		gr.scale(4, 4);
		
		Text.drawString(titleString, gr, (-titleString.length()*6)/2, -5);
		gr.setTransform(tf);
		Text.drawString(subString, gr, (width-subString.length()*6)/2, 40+10);
		gr.dispose();

		return image;
	}
}

}