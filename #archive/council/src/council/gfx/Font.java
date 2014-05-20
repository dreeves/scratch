import static org.lwjgl.opengl.GL11.*;

final Font DEFAULT;
{
	dimg := Image.of("font.png");
	for y to dimg.Y: for x=1 to dimg.X:
	if dimg.g(x-1, y) == 0xffffffff && dimg.g(x, y) != 0xffffffff:
		dimg.s(x, y, 0xff000000);
	DEFAULT = Font.of(dimg);
}

class {
	final String name;
	final int sizeX, sizeY;
	final bool isProportional;
	priv final int[] widths, offsets;
	priv final Texture tex;
	
	//ofctor(Image img) {
	static Font of(Image img) {
		if (img.Y % 6 != 0 || img.X % 16 != 0) {Log.warn("bad font image"); img = img.subImage(0, 0, img.X/16*16, img.Y/6*6);}
		img = new Image(img).replace(0xffff00ff, 0).replace(0xff0094ff, 0);
		int sizeY = img.Y / 6, sizeX = img.X / 16;
		val widths = new int[6 * 16];
		val offsets = new int[6 * 16];
		fora(i, widths) {
			val cha = img.subImage(i % 16 * sizeX, i / 16 * sizeY);
			int j = 0;
			for (; j < sizeX && cha.g(j, 0) == 0xff008000; j++) offsets[i]++;
			for (; j < sizeX && cha.g(j, 0) != 0xff008000; j++) widths[i]++;
		}
		bool isProp = true;
		for (int i = 1; i < widths.length; i++)
		if (widths[i] != widths[0])
			{isProp = false; break;}
		img.replace(0xff008000, 0);
		fro(x, 16) fro(y, 6) {
			val sub = img.subImage(x*16, y*16, 16, 16);
			fro(x2, sub.X-1) fro(y2, sub.Y-1)
			if (sub.gA(x2, y2) == 0xff && sub.gA(x2+1, y2+1) == 0x00)
				sub.s(x2+1, y2+1, 0x40000000);
		}
		return new Font("aFont", img.X/16, img.Y/6, isProp, widths, offsets, new Texture(img));
	}
	priv Font(String name, int sizeX, int sizeY, bool isProportional, int[] widths, int[] offsets, Texture tex)
		{ctor7(name, sizeX, sizeY, isProportional, widths, offsets, tex);}
	
	void drawString(String s, int posX, int posY) {
		if (s == null || s.length() == 0) return;
		tex.bind();
		glPushMatrix();
			glTranslate(posX, posY);
			for (char c : s.toCharArray())
				drawCharQuad(clamp(c));
		glPopMatrix();
	}
	priv void drawCharQuad(char c) {
		int ic = c - 0x20;
		float x = ic % 16 / 16f;
		float y = ic / 16 / 6f;
		float w = 1/16f;
		float h = 1/6f;
		int off = -offsets[ic];
		glBegin(GL_QUADS);
			glTexCoord(x  , y  ); glVertex(off      , 0    );
			glTexCoord(x+w, y  ); glVertex(off+sizeX, 0    );
			glTexCoord(x+w, y+h); glVertex(off+sizeX, sizeY);
			glTexCoord(x  , y+h); glVertex(off      , sizeY);
		glEnd();
		glTranslate(widths[ic], 0);
	}
	
	int calcLength(String s) {
		if (isProportional) {
			return s.length() * sizeX;
		} else {
			int ret = 0;
			fors(i, s) ret += width(s.charAt(i));
			return ret;
		}
	}
	
	priv int width(char c) {return widths[clamp(c) - 0x20];}
}
priv char clamp(char c) {return c < 0x20 || c > 0x7f? '?' : c;}