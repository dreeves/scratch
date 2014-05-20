package fishermen;

import lombok.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.*;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

public class Cerver
{
	private static final Server server = new Server() {
		protected Connection newConnection() {return new UserConnection();}
	};

	public static void main(String[] args)
	{
		M.register(server);
		
		server.addListener(new Listener()
		{
			public void disconnected(Connection conn)
			{
				if (!(conn instanceof UserConnection))
					return;
				UserConnection uc = (UserConnection)conn;
				if (uc.name == null)
					return;
				server.sendToAllTCP(new ChatMessage(uc.name + " left."));
				updateNames();
			}

			public void received(Connection conn, Object o)
			{
				if (!(conn instanceof UserConnection))
					return;
				UserConnection uc = (UserConnection)conn;
				if (o instanceof RegisterName) handleRegisterName(uc, (RegisterName)o);
				else if (o instanceof ChatCommand) handleChatCommand(uc, (ChatCommand)o);
			}
			
			private void handleRegisterName(UserConnection uc, RegisterName msg)
			{
				if (uc.name != null)
					return;
				uc.name = M.validateName(msg.name);
				server.sendToAllTCP(new ChatMessage(uc.name + " joined."));
				updateNames();
			}
			
			private void handleChatMessage(UserConnection uc, ChatMessage msg)
			{
				if (uc.name == null)
					return;
				
				if (msg.text == null)
					msg.text = "I just tried to send a null String in a ChatMessage! (Oh nyo!)";
				else if (msg.text.length() == 0)
					return;
				else if (msg.text.length() > 80)
					msg.text = msg.text.substring(0, 80);
				
				char[] s = msg.text.toCharArray();
				for (int i = 0; i < s.length; i++)
				if (s[i] != '\t' && (s[i] < 32 || s[i] > 126))
					s[i] = '#';
			
				msg.text = uc.name + ": " + new String(s);
				server.sendToAllTCP(msg);
			}
			
			private void handleChatCommand(UserConnection uc, ChatCommand comm)
			{
				if ("help".equals(comm.command))
					server.sendToTCP(uc.getID(), new ChatMessage("/say says something\n/help displays this rather unhelpful message"));
				else if ("say".equals(comm.command))
					handleChatMessage(uc, new ChatMessage(comm.args));
			}
		});
		try {server.bind(M.portTCP, M.portUDP);}
		catch (IOException e) {M.aintGonnaWork(e);}
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread("Thread-MyShutdownHook"){public void run(){server.stop();}});
	}
	
	private static void updateNames()
	{
		Connection[] conns = server.getConnections();
		List<String> names = new ArrayList<String>(conns.length);
		for (Connection conn : conns)
		if (conn instanceof UserConnection && ((UserConnection)conn).name != null)
			names.add(((UserConnection)conn).name);
		server.sendToAllTCP(new UpdateNames(names.toArray(new String[0])));
	}
}

class UserConnection extends Connection
{
	public String name;
}