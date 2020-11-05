package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.User;

public class UserJoinEvent extends UserEvent {
	
	private User requestedUser;
	
	public UserJoinEvent () {
		
	}
	
	public UserJoinEvent (User requestedUser) {
		this.requestedUser = requestedUser;
	}
	
	public User getRequestedUser () {
		return requestedUser;
	}

}
