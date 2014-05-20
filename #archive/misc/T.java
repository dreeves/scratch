import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.lang.reflect.*;
import javax.swing.*;

public class T extends JPanel implements Runnable
{
	volatile BufferedImage bi;
	volatile long then;
	long now, time;
	Thread thread;
	double angle;
	int n;
	double rate;

	public T()
	{
		super(false);
		setPreferredSize(new Dimension(640,480));

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				GraphicsConfiguration gc = getGraphicsConfiguration();
				bi = gc.createCompatibleImage(getWidth(),getHeight());
			}
		});
	}

	public void start()
	{
		then = System.nanoTime();
		thread = new Thread(this);
		thread.start();
	}

	public void stop()
	{
		thread.interrupt();
	}

	public void run()
	{
		try
		{
			long now = 0;
			long then = System.nanoTime();

			while (true) {
				render();
				try {
					EventQueue.invokeAndWait(new Runnable() {
						public void run() {
							paintImmediately(getBounds());
						}
					});
				} catch (InvocationTargetException ite) {
					System.out.println(ite);
				}

				/*
				while (now < then + 10000000)
					now = System.nanoTime();
				then = now;
				*/
			}
		}
		catch (InterruptedException ie) {System.out.println(ie);}
	}

	public void render()
	{
		int w = getWidth();
		int h = getHeight();

		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		 RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		g.fillRect(0,0,w,h);

		g.setColor(Color.RED);
		g.drawString(String.format("%5.1f",rate),10,12);

		angle += 0.001;
		g.rotate(angle,w/2,h/2);
		g.setColor(Color.BLUE);
		g.fillRect(w/2 - 100,h/2 - 100,200,200);

		if (++n % 100 == 0)
		{
			now = System.nanoTime();
			time = now - then;
			then = now;
			rate = 100000000000.0 / time;
		}

		g.dispose();
	}

	public void paintComponent(Graphics g)
	{
		g.drawImage(bi,0,0,null);
	}

	public static void main(String[] args)
	{
		final T t3 = new T();
		final JFrame f = new JFrame();
		f.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent we) {
				t3.start();
			}
			public void windowClosing(WindowEvent we) {
				t3.stop();
				f.dispose();
			}
		});

		f.add(t3, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
	}
} 