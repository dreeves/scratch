package fishermen;

import lombok.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.awt.Toolkit;

import java.util.List;
import java.util.ArrayList;

public class M
{
	//sets the OS interrupt period to 1ms for the duration of this program (or is it this JVM?)
	static {
		Thread t = new Thread("Thread-InterruptPeriodSetter")
		{
			public void run()
				{while (true) S.sleep(Long.MAX_VALUE);}
		};
		t.setDaemon(true);
		t.start();
	}

	public static final Toolkit defaultTk = Toolkit.getDefaultToolkit();
	public static final int portTCP = 53914;
	public static final int portUDP = 43914;
	public static final int width = 800; //to be easily accessed with M.width&height
	public static final int height = 600; //does violate Good Non-Globality Principles or smth

	public static void register(EndPoint toRegisterOn)
	{
		Kryo kryo = toRegisterOn.getKryo();
		kryo.register(RegisterName.class);
		kryo.register(String[].class);
		kryo.register(UpdateNames.class);
		kryo.register(ChatMessage.class);
		kryo.register(ChatCommand.class);
	}
	
	public static boolean isNameValid(String name) {return validateName(name).equals(name);}
	public static String validateName(String name)
	{
		if (name == null)
			return "nullName";
		if (name.length() == 0)
			return "emptyName";
		if (name.length() == 1)
			name = "I am " + name;
		name = name.trim();
		char[] ret = new char[name.length() > 16 ? 16 : name.length()];
		for (int i = 0; i < ret.length; i++)
		{
			char c = name.charAt(i);
			if (c == '\t') c = ' ';
			else if (c != 0 && (c < 32 || c > 126)) c = '#';
			ret[i] = c;
		}
		int j = 0;
		for (int i = 0; i < ret.length; i++)
		if (ret[i] != 0)
		{
			ret[j] = ret[i];
			j++;
		}
		return new String(ret).substring(0, j);
	}
	
	public static String spacesFromTabs(String s) {return spacesFromTabs(s, 8);}
	public static String spacesFromTabs(String s, int tabLength)
	{
		List<Character> ret = new ArrayList<Character>();
		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) == '\t')
			{
				while (ret.size() % tabLength != tabLength - 1)
					ret.add(' ');
				ret.add(' ');
			}
			else
				ret.add(s.charAt(i));
		}
		char[] ret2 = new char[ret.size()];
		for (int i = 0; i < ret.size(); i++)
			ret2[i] = ret.get(i);
		return new String(ret2);
	}
	
	public static String asciiFromUnicode(String s)
	{
		char[] ret = s.toCharArray();
		for (int i = 0; i < ret.length; i++)
		if (ret[i] > 0xff)
			ret[i] = '?';
		return new String(ret);
	}
	
	public static BufferedImage getImage(String path)
	{
		BufferedImage ret = null;
		try {ret = ImageIO.read(M.class.getResource(path));}
		catch (IllegalArgumentException e) {S.pln(path + " not found or not readable or SOMETHING"); e.printStackTrace();}
		catch (IOException e) {S.pln(path + " not found or not readable or SOMETHING"); e.printStackTrace();}
		return ret;
	}
		
	public static void aintGonnaWork(Exception e)
	{
		S.pln("Oh well", "Sorry man, but this program totally ain't gonna work. Something with " +
			"your computer, maybe? Or, probably, it's my program's fault and I should fix it.");
		throw new Error("Ain't gonna work.", e == null? new Exception() : e);
	}
}

class RegisterName
{
	public final String name;
	public RegisterName() {name = null;}
	public RegisterName(String name) {this.name = name;}
}

class UpdateNames
{
	public final String[] names;
	public UpdateNames() {names = null;}
	public UpdateNames(String[] names) {this.names = names;}
}

class ChatMessage
{
	public String text;
	public ChatMessage() {}
	public ChatMessage(String text) {this.text = text;}
}

class ChatCommand
{
	public final String command;
	public final String args;
	public ChatCommand() {command = null; args = null;}
	public ChatCommand(String _command, String _args) {command = _command; args = _args;}
}