package fishermen;

import org.lwjgl.*;
import lombok.*;

import java.awt.Canvas;
import javax.swing.JFrame;
import java.awt.Cursor;

class ClientWindow
{
	public static final ClientWindow INST = new ClientWindow();
	public void dummyMethod() {}
	
	private JFrame jf = new JFrame("73# (#47 |<|_13|\\|7 0|= |=1$#39|/\\/\\3|\\|");
	
	public void setDisplayParent() throws LWJGLException
		{org.lwjgl.opengl.Display.setParent(displayParent);}
	private Canvas displayParent = new Canvas()
	{
		private Thread renderThread = new Thread(Render.INST, "Thread-Render");
		public final void addNotify()
		{
			super.addNotify();
			//Once the Canvas is created its add notify method will call this method to start the LWJGL Display and game loop in another thread.
			renderThread.start();
		}
		public final void removeNotify()
		{
			//Tell game loop to stop running, after which the LWJGL Display will be destoryed. The main thread will wait for the Display.destroy() to complete
			Render.quit();
			try {renderThread.join();}
			catch (InterruptedException e) {e.printStackTrace();}
			super.removeNotify();
		}
	};
	
	private ClientWindow()
	{
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false);
		jf.getContentPane().add(displayParent);
		jf.setResizable(false);
		//jf.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent ev) {System.exit(0);}});
		
		displayParent.setSize(M.width, M.height);
		displayParent.setFocusable(true);
		displayParent.requestFocusInWindow();
		displayParent.setIgnoreRepaint(true);
			
		jf.setVisible(true);
		setFrameSize();
	}
	
	private void setFrameSize()
	{
		int wmod = jf.getInsets().right + jf.getInsets().left;
		int hmod = jf.getInsets().top + jf.getInsets().bottom;
		int w = wmod + M.width;
		int h = hmod + M.height;
		java.awt.Dimension screen = M.defaultTk.getScreenSize();
		jf.setBounds((screen.width - w)/2, (screen.height - h)/2, w, h);
	}
	
	private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private static final Cursor BLANK_CURSOR = M.defaultTk.createCustomCursor(M.defaultTk.createImage(new byte[0]), new java.awt.Point(), "blank cursor");
		/**true = default cursor. false = blank cursor.*/
	public void setCursor(boolean b)
		{jf.setCursor(b? DEFAULT_CURSOR : BLANK_CURSOR);}
}