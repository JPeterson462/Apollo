package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.Lobby.LobbyStatus;

public class UserLobbyQuery {
	
	public static class Request {
		
	}
	
	public static class LobbyResult {
		
		public int playersPerTeam, teams, playersConnected;
		
		public LobbyStatus lobbyStatus;
		
	}
	
	public static class Response {
		
		public LobbyResult[] lobbies;
		
	}

}
