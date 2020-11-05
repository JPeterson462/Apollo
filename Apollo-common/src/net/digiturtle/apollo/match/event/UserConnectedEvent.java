package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.User;

public class UserConnectedEvent extends UserEvent {
	
	private User user;
	
	public UserConnectedEvent () {
		
	}
	
	public UserConnectedEvent (User user) {
		this.user = user;
	}
	
	public User getUser () {
		return user;
	}

}
