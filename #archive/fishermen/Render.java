package fishermen;

import grass.tuple.*;
import org.lwjgl.*;
import lombok.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import java.util.List;
import java.util.ArrayList;

public class Render implements Runnable
{
	private Render() {}
	public static Render INST = new Render();

	private static boolean shouldQuit = false; //is the game loop running
	public static void quit() {shouldQuit = true;}

	public void run()
	{
		try
		{
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			DisplayMode chosenMode = null;
			for (int i = 0; i < modes.length; i++)
			if (modes[i].getWidth() == M.width && modes[i].getHeight() == M.height)
				{chosenMode = modes[i]; break;}
			if (chosenMode == null) throw new LWJGLException();
			Display.setDisplayMode(chosenMode);
			ClientWindow.INST.setDisplayParent();
			Display.create();
		}
		catch (LWJGLException e) {M.aintGonnaWork(e);}
		S.claim(Display.getDisplayMode().getWidth() == M.width && Display.getDisplayMode().getHeight() == M.height);
		resizeGLScene(M.width, M.height);
		initGL();
		gameLoop();
		Display.destroy();
	}

	private static final int fpsPollingPeriod = 2000;
	private @Getter static double fps = 10;
	private static String fpsString = "? frames in ? secs = ? fps";

	private static void gameLoop()
	{
		long startTime = S.mTime() + fpsPollingPeriod;
		int frames = 0;

		while (!shouldQuit)
		{
			Display.update(false);
			//Display.update();//assumes fps >= 100

			doLogic();
			renderGL();

			if (startTime > S.mTime())
				frames++;
			else
			{
				double timeUsed = (fpsPollingPeriod + startTime - S.mTime()) / 1000.0;
				startTime = S.mTime() + fpsPollingPeriod;
				fps = frames / timeUsed;
				fpsString = frames + " frames in " + timeUsed + " secs = " + Double.toString(fps).substring(0, Math.min(5, Double.toString(fps).length())) + " fps";
				frames = 0;
			}
		}
	}

//////////////////////////////////////////////////////////////// logic, mostly ////////////////////////////////////////////////////////////////

	private static final short MSGING  = (short)0x7ebf;
	private static final short PLAYING = (short)0x6c4e;
	private static final short JOINING = (short)0x36fb;

	private static List<String> scrollingText = new ArrayList<String>();
	private static StringBuilder currentText = new StringBuilder();
	private static StringBuilder ipToJoin = new StringBuilder("127.0.0.1");
	private static short logicMode = JOINING;

	private static void doLogic()
	{
		Display.processMessages();
		KeyIn.poll();
		//Mice.poll();

		if (logicMode == MSGING)
		{
			currentText.append(KeyIn.getLetters());
			if (KeyIn.once("tab"))
				currentText = new StringBuilder(M.spacesFromTabs(currentText.append('\t').toString()));
			if (currentText.length() > 0 && KeyIn.once("back"))
				currentText.setLength(currentText.length() - 1);
			if (KeyIn.once("return"))
			{
				String s = currentText.toString();
				currentText.setLength(0);
				logicMode = PLAYING;
				handleChatInput(s);
			}
		}
		else if (logicMode == PLAYING)
		{
			if (KeyIn.once("t"))
				logicMode = MSGING;
		}
		else if (logicMode == JOINING)
		{
			ipToJoin.append(KeyIn.getLetters());
			if (ipToJoin.length() > 0 && KeyIn.once("back"))
				ipToJoin.setLength(ipToJoin.length() - 1);
			if (ipToJoin.length() > 15)
				ipToJoin.setLength(15);
			if (KeyIn.once("return"))
			{
				String s = ipToJoin.toString();
				ipToJoin.setLength(0);
				logicMode = PLAYING;
				Klient.connect(5000, s);
				String name = String.valueOf(S.rand(100, 999));
				Klient.sendTCP(new RegisterName(name));
			}
		}
	}
	
	private static void handleChatInput(String s)
	{
		if (s == null || s.length() == 0)
			return;
		if (s.charAt(0) != '/')
			{Klient.sendTCP(new ChatCommand("say", s)); return;}
		if (s.length() == 1)
			return;
		
		int commEnd = s.indexOf(' ') == -1? s.length() : s.indexOf(' ');
		String command = s.substring(1, commEnd);
		String args = (commEnd == s.length()? "" : s.substring(commEnd + 1));
		
		Klient.sendTCP(new ChatCommand(command, args));
	}
	
	public static void addMessage(String msg)
	{
		while (true)
		{
			int i = msg.indexOf('\n');
			if (i == -1)
				{scrollingText.add(msg); break;}
			else
				{scrollingText.add(msg.substring(0, i)); msg = msg.substring(i + 1);}
		}
	}

//////////////////////////////////////////////////////////////// openGL, mostly ////////////////////////////////////////////////////////////////

	public static final float zNear = 0.1f; //near clipping distance
	public static final float zFar = 500f; //far clipping distance
	public static final float yFov = 85f; //field-of-view angle in the y direction
	private static double glowCount = S.rand() * 17.489;

	private static void resizeGLScene(int width, int height)
	{
		GL11.glViewport(0, 0, width, height);// Reset The Current Viewport
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL.loadIdentity();
		org.lwjgl.util.glu.Project.gluPerspective(yFov, width / (float)height, zNear, zFar);// Calculate The Aspect Ratio Of The Window
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL.loadIdentity();
	}

	private static void initGL()
	{
		if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object)
			M.aintGonnaWork(null);

		loadGLTextures();

		GL.enable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);// Blending Function For Translucency Based On Source Alpha Value
		//GL.enable(GL11.GL_CULL_FACE);
		GL.enable(GL11.GL_TEXTURE_2D);
		//GL11.glShadeModel(GL11.GL_SMOOTH);				// Enables Smooth Shading
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);			// Black Background
		GL11.glClearDepth(1.0f);							// Depth Buffer Setup
		//GL.enable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);					// The Type Of Depth Test To Do
		GL.hint(GL.PERSPECTIVE_CORRECTION_HINT, GL.NICEST);	// Really Nice Perspective Calculations
		//GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);//useful for VBOs? idk(
	}

	private static void loadGLTextures()
	{
		//GL.NEAREST, GL.LINEAR, GL.LINEAR_MIPMAP_NEAREST
		//curses font
			BufferedImage curses8x13 = M.getImage("res/curses_8x13.png");
			for (int x = 0; x < curses8x13.getWidth(); x++)
			for (int y = 0; y < curses8x13.getHeight(); y++)
			if (curses8x13.getRGB(x, y) == 0xffff00ff)
				curses8x13.setRGB(x, y, 0x00000000);
			for (int x = 0; x < curses8x13.getWidth() - 1; x++)
			for (int y = 0; y < curses8x13.getHeight() - 1; y++)
			if (curses8x13.getRGB(x, y) == 0xffffffff && curses8x13.getRGB(x + 1, y + 1) == 0x00000000)
				curses8x13.setRGB(x + 1, y + 1, 0xff000000);
			Font.createFont("curses", new Texture(curses8x13, GL.NEAREST, GL.NEAREST));
	}

	private static void renderGL()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL.loadIdentity();
		
		GL.beginOrtho();
		
		Font curses = new Font("curses", 1);
		
		//background
		if (logicMode == MSGING || logicMode == PLAYING || logicMode == JOINING)
		{
			glowCount += 0.0005;
			float flo1 = (float)(Math.sin(glowCount * 1.73) * 0.5 + 0.5);
			float flo2 = (float)(Math.cos(glowCount * 1.14) * 0.5 + 0.5);
			float flo3 = (float)(Math.cos(glowCount * 0.78) * 0.5 + 0.5);
			GL.disable(GL11.GL_TEXTURE_2D);
			GL.begin(GL.QUADS);
				GL.color3f(flo3, flo2, flo2); GL.vertex3f(M.width,        0, 0); //GL.color3f(0.7f, 0.1f, 0.1f);
				GL.color3f(flo3, flo1, flo3); GL.vertex3f(M.width, M.height, 0); //GL.color3f(0.7f, 0.7f, 0.7f);
				GL.color3f(flo1, flo3, flo1); GL.vertex3f(      0, M.height, 0); //GL.color3f(0.7f, 0.1f, 0.7f);
				GL.color3f(flo2, flo1, flo2); GL.vertex3f(      0,        0, 0); //GL.color3f(0.7f, 0.7f, 0.1f);
			GL.end();
			GL.enable(GL11.GL_TEXTURE_2D);
			GL.color3f(1, 1, 1);
		}
		
		if (logicMode == JOINING)
		{
			StringBuilder sb = new StringBuilder().append("| ").append(ipToJoin);
			while (sb.length() < 15 + 2) sb.append(' ');
			sb.append(" |");
			curses.drawString("-------------------", M.width / 2 - curses.sizeX * 17 / 2, M.height / 2 + curses.sizeY * ( 1) / 2);
			curses.drawString(sb.toString()        , M.width / 2 - curses.sizeX * 17 / 2, M.height / 2 + curses.sizeY * (-1) / 2);
			curses.drawString("-------------------", M.width / 2 - curses.sizeX * 17 / 2, M.height / 2 + curses.sizeY * (-3) / 2);
		}
			
		if (logicMode == MSGING)
		{
			//text shadery
				int tsBorder = 4, tsHeight = 21;//notch's is 24;
				GL.disable(GL11.GL_TEXTURE_2D);
				GL.color4f(0, 0, 0, 0.5f);
				GL.begin(GL.QUADS);
					GL.texCoord2f(0, 0); GL.vertex3f(      0 + tsBorder,        0 + tsBorder, 0);
					GL.texCoord2f(1, 0); GL.vertex3f(M.width - tsBorder,        0 + tsBorder, 0);
					GL.texCoord2f(1, 0); GL.vertex3f(M.width - tsBorder, tsHeight + tsBorder, 0);
					GL.texCoord2f(0, 0); GL.vertex3f(      0 + tsBorder, tsHeight + tsBorder, 0);
				GL.end();
				GL.enable(GL11.GL_TEXTURE_2D);
				GL.color4f(1, 1, 1, 1);
			
			//text entry display
				curses.drawString(currentText.toString(), 8, 8);
		}
		
		if (logicMode == MSGING || logicMode == PLAYING)
		{
			//text display
				for (int i = 0; i < scrollingText.size() && i < 30; i++)
					curses.drawString(scrollingText.get(scrollingText.size() - i - 1), 4, curses.sizeY * (i + 2) + 1);

			curses.drawString(fpsString, 0, M.height - curses.sizeY*1);
			curses.drawString("this is a string, yay!", 0, M.height - curses.sizeY*2);
			curses.drawString("this is also a string! so much happiness^^", 0, M.height - curses.sizeY*3);
		}
		
		GL.endOrtho();
	}
}

class GL2D
{
	public static void drawTexture(Texture tex, int origX, int origY)
	{
	}
}