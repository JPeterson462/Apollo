package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.Lobby;

public class MatchStatusEvent {
	
	public Lobby.LobbyStatus status;
	
	public String ip;
	
	public int port;
	
	public MatchStatusEvent () {
		
	}
	
	public MatchStatusEvent (Lobby.LobbyStatus status, String ip, int port) {
		this.status = status;
		this.ip = ip;
		this.port = port;
	}

}
