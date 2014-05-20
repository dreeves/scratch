package jfront;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class TextFile
{
	private boolean isClosed = false;
	private BufferedWriter out;
	
	public TextFile(String file)
		{this(file, "UTF-8");}
	
	public TextFile(String file, String charsetName)
	{
		try {out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charsetName));}
		catch (FileNotFoundException e) {isClosed = true;}
		catch (UnsupportedEncodingException e) {isClosed = true;}
	}
	
	public void write(String s)
	{
		if (isClosed) throw new IllegalStateException();
		try {out.write(s);}
		catch (IOException e) {close();}
	}
	
	public void write(char[] dat)
	{
		if (isClosed) throw new IllegalStateException();
		try {out.write(dat);}
		catch (IOException e) {close();}
	}
	
	public void flush()
	{
		if (isClosed) throw new IllegalStateException();
		try {out.flush();}
		catch (IOException e) {close();}
	}
	
	public void close()
	{
		if (isClosed) throw new IllegalStateException();
		try {out.close();}
		catch (IOException e) {}
		isClosed = true;
	}
	
	public boolean isClosed() {return isClosed;}
}