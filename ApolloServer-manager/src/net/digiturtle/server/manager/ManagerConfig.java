package net.digiturtle.server.manager;

public class ManagerConfig {
	
	public int port;
	
	public String dbFile;
	
	public LobbyConfig[] lobbies;
	
	public static class LobbyConfig {
		
		public String ip;
		public int port;
		
		public int numTeams, numPlayers;
		
	}

}
