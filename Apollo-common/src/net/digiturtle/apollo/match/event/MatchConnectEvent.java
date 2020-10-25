package net.digiturtle.apollo.match.event;

import java.util.UUID;

public class MatchConnectEvent extends MatchEvent {

	private UUID uniqueIdentifier;
	
	public MatchConnectEvent () {
		
	}
	
	public MatchConnectEvent (UUID uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}
	
	public UUID getUniqueIdentifier () {
		return uniqueIdentifier;
	}
	
	public void setUniqueIdentifier (UUID uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}
	
}
