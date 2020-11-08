package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.Lobby.LobbyStatus;

public class UserLobbyQuery {
	
	public static class Request {
		
	}
	
	public static class LobbyResult {
		
		public int playersPerTeam, teams, playersConnected;
		
		public String worldName;
		
		public LobbyStatus lobbyStatus;
		
		public String toString () {
			return "Lobby[" + worldName + ", " + lobbyStatus.name() + ": " + teams + "]";
		}
		
	}
	
	public static class Response {
		
		public LobbyResult[] lobbies;
		
	}

}
