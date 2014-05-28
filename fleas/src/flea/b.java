package flea;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Window;

public class b extends Frame
{
	int a;
	int b;
	int c = 0;
	int d = 28;
	a e;
	Graphics f;

	public b(a parama, int paramInt1, int paramInt2, String paramString, boolean paramBoolean1, boolean paramBoolean2)
	{
		this.a = paramInt1;
		this.b = paramInt2;
		this.e = parama;
		if (paramBoolean2 == true)
			this.d = 48;
		else
			this.d = 28;
		setTitle(paramString);
		setResizable(paramBoolean1);
		show();
		toFront();
		resize(this.a, this.b);
		this.f = getGraphics();
	}

	public final void paint(Graphics paramGraphics)
	{
		this.e.paint(paramGraphics);
	}

	public int a()
	{
		return size().width;
	}

	public void resize(int paramInt1, int paramInt2)
	{
		super.resize(paramInt1, paramInt2 + this.d);
	}

	public int b()
	{
		return size().height - this.d;
	}

	public Graphics getGraphics()
	{
		Graphics localGraphics = super.getGraphics();
		if (this.c == 0)
			localGraphics.translate(0, 24);
		else
			localGraphics.translate(-5, 0);
		return localGraphics;
	}

	public boolean handleEvent(Event paramEvent)
	{
		if (paramEvent.id == 401)
			this.e.keyDown(paramEvent, paramEvent.key);
		else if (paramEvent.id == 402)
			this.e.keyUp(paramEvent, paramEvent.key);
		else if (paramEvent.id == 501)
			this.e.mouseDown(paramEvent, paramEvent.x, paramEvent.y - 24);
		else if (paramEvent.id == 506)
			this.e.mouseDrag(paramEvent, paramEvent.x, paramEvent.y - 24);
		else if (paramEvent.id == 502)
			this.e.mouseUp(paramEvent, paramEvent.x, paramEvent.y - 24);
		else if (paramEvent.id == 503)
			this.e.mouseMove(paramEvent, paramEvent.x, paramEvent.y - 24);
		else if (paramEvent.id == 201)
			this.e.destroy();
		else if (paramEvent.id == 1001)
			this.e.action(paramEvent, paramEvent.target);
		else if (paramEvent.id == 403)
			this.e.keyDown(paramEvent, paramEvent.key);
		else if (paramEvent.id == 404)
			this.e.keyUp(paramEvent, paramEvent.key);
		return true;
	}
}