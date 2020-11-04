package net.digiturtle.server.manager;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

public class MatchLobby {
	
	private HashMap<UUID, InetSocketAddress> connections;
	
	public MatchLobby () {
		connections = new HashMap<>();
	}

}
