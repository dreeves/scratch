package fishermen;

import lombok.*;

import com.esotericsoftware.kryonet.*;
import com.esotericsoftware.kryo.Kryo;

import java.io.IOException;

public class Klient
{
	private static final Client client = new Client();
	private static boolean RENDERTESTING = false;

	public static void main(String[] args)
	{
		if (args.length > 0 && "-rendertest".equals(args[0]))
			RENDERTESTING = true;
		
		M.register(client);
		client.addListener(new Listener()
		{
			public void disconnected(Connection conn) {} //shut us down, don't just do nothing
			public void received(Connection conn, Object o)
			{
				if (o instanceof UpdateNames)
					;//.setNames(((UpdateNames)o).names);
				else if (o instanceof ChatMessage)
					Render.addMessage(((ChatMessage)o).text);
			}
		});
		client.start();
		Runtime.getRuntime().addShutdownHook(new Thread("Thread-MyShutdownHook"){public void run(){client.stop();}});
		
		ClientWindow.INST.dummyMethod();
	}
	
	public static void sendTCP(Object o)
		{if (!RENDERTESTING) client.sendTCP(o);}
		
	public static void connect(int timeout, String host)
	{if (RENDERTESTING) return;
		try {client.connect(timeout, host, M.portTCP, M.portUDP);}
		catch (IOException e) {M.aintGonnaWork(e);}
	}
}