package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.User;

public class UserLobbyEvent extends UserEvent {
	
	private User user;
	private int lobby;
	
	public UserLobbyEvent () {
		
	}
	
	public UserLobbyEvent (User user, int lobby) {
		this.user = user;
		this.lobby = lobby;
	}
	
	public User getUser () {
		return user;
	}
	
	public int getLobby () {
		return lobby;
	}

}
