package net.digiturtle.apollo.match.event;

import java.util.UUID;

public abstract class PlayerEvent extends Event {

	private UUID player;
	
	public PlayerEvent () {
		
	}
	
	public PlayerEvent (UUID player) {
		this.player = player;
	}

	public UUID getPlayer() {
		return player;
	}

	public void setPlayer(UUID player) {
		this.player = player;
	}

}
