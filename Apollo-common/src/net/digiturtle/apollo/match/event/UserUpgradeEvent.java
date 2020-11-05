package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.User;

public class UserUpgradeEvent extends UserEvent {
	
	private User user;
	
	private int powerup;
	
	public UserUpgradeEvent () {
		
	}
	
	public UserUpgradeEvent(User user, int powerup) {
		this.user = user;
		this.powerup = powerup;
	}
	
	public User getUser () {
		return user;
	}
	
	public int getPowerup () {
		return powerup;
	}

}
