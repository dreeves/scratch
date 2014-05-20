package fishermen;

import org.lwjgl.*;
import lombok.*;

import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class Font
{
	public final int sizeX, sizeY;
	public final String name;
	private final Texture tex;
	private final int sizeFactor;

	public Font(String _name, int _sizeFactor)
	{
		name = _name;
		sizeFactor = _sizeFactor > 0? _sizeFactor : 1;
		tex = fontList.get(name);
		if (tex == null)
			throw new RuntimeException("That font doesn't exist.");
		sizeX = tex.width / 16 * sizeFactor;
		sizeY = tex.height / 16 * sizeFactor;
	}
	
	public void drawString(String s, int posX, int posY)
	{
		if (s == null || s.length() == 0)
			return;
		s = M.asciiFromUnicode(M.spacesFromTabs(s));
		GL.bindTexture2D(tex);
		GL.pushMatrix();
		GL.translate(posX, posY, 0);
		for (int i = 0; i < s.length(); i++)
			{drawCharQuad(s.charAt(i)); GL.translate(sizeX, 0, 0);}
		GL.popMatrix();
	}
	
		//used by drawString
	private void drawCharQuad(char c)
	{
		int x = c % 16;
		int y = c / 16;
		GL.begin(GL.QUADS);
			GL.texCoord2f((x    ) / 16f, (y + 1) / 16f); GL.vertex3f(0    , 0    , 0);
			GL.texCoord2f((x + 1) / 16f, (y + 1) / 16f); GL.vertex3f(sizeX, 0    , 0);
			GL.texCoord2f((x + 1) / 16f, (y    ) / 16f); GL.vertex3f(sizeX, sizeY, 0);
			GL.texCoord2f((x    ) / 16f, (y    ) / 16f); GL.vertex3f(0    , sizeY, 0);
		GL.end();
	}

	
//////////////////////////////////////////////////////////////// static ////////////////////////////////////////////////////////////////

	private static final HashMap<String, Texture> fontList = new HashMap<String, Texture>();
	
	public static void createFont(String name, Texture tex)
	{
		fontList.put(name, tex);
	}
}
