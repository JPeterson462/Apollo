package net.digiturtle.apollo.match.event;

public class UserUpgradeResponse {
	
	public int powerup;
	
	public boolean success;
	
	public UserUpgradeResponse () {
		
	}
	
	public UserUpgradeResponse (boolean success, int powerup) {
		this.success = success;
		this.powerup = powerup;
	}

}
