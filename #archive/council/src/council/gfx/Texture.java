import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import java.awt.image.BufferedImage;

class {
	final int X, Y;
	priv int glName;
	
	ctor(int X, int Y) {ctor2(X, Y);}

	ctor(BufferedImage bi) {this(bi, GL_NEAREST, GL_NEAREST);}
	ctor(BufferedImage bi, int magFilter, int minFilter) {
		X = bi.getWidth(); Y = bi.getHeight();
		//if (bi.getColorModel().getPixelSize() != 32) Log.warn("Texture ctor", "this BufferedImage's bitdepth is " + bi.getColorModel().getPixelSize() + ", not 32.");
		Log.warn("Texture ctor", "this BufferedImage's bitdepth is " + bi.getColorModel().getPixelSize() + ", not 32.");
		ctorHelper(byteArrFrom(bi), magFilter, minFilter);
	}
	ctor(Image img) {this(img, GL_NEAREST, GL_NEAREST);}
	ctor(Image img, int magFilter, int minFilter) {X = img.X; Y = img.Y; ctorHelper(byteArrFrom(img), magFilter, minFilter);}
	#define comment(x)
	#define _byteArrFrom(type, gw, gh, grgb) \
	priv static byte[] byteArrFrom(type img) { \
		int w = gw, h = gh; \
		val ret = new byte[w * h * 4]; comment(bitdepth==32 => bytedepth==4) \
		fro(x, w) fro(y, h) { \
			int rgb = grgb; \
			ret[(y*w + x) * 4 + 0] = (byte)(rgb); \
			ret[(y*w + x) * 4 + 1] = (byte)(rgb >>> 8); \
			ret[(y*w + x) * 4 + 2] = (byte)(rgb >>> 16); \
			ret[(y*w + x) * 4 + 3] = (byte)(rgb >>> 24); \
		} \
		return ret; \
	}
	_byteArrFrom(Image, img.X, img.Y, img.g(x, y))
	_byteArrFrom(BufferedImage, img.getWidth(), img.getHeight(), img.getRGB(x, y))
	priv void ctorHelper(byte[] imgDat, int magFilter, int minFilter) {
		glName = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, glName);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, X, Y, 0, GL_BGRA, GL_UNSIGNED_BYTE, Buf.fromArr(imgDat));		
	}
	
	void copy(Texture tex) {if (X != tex.X || Y != tex.Y) throw new IllegalArgumentException(); else glName = tex.glName;}
	void bind() {glBindTexture(GL_TEXTURE_2D, glName);}
	void delete() {glDeleteTextures(glName); glName = 0;}
	
	void draw(int origX, int origY) {
		this.bind();
		glPushMatrix();
			glTranslate(origX, origY);
			glBegin(GL_QUADS);
				glTexCoord(0, 0); glVertex(0, 0);
				glTexCoord(1, 0); glVertex(X, 0);
				glTexCoord(1, 1); glVertex(X, Y);
				glTexCoord(0, 1); glVertex(0, Y);
			glEnd();
		glPopMatrix();
	}
}