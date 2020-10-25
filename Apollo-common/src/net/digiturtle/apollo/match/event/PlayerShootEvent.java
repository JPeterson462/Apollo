package net.digiturtle.apollo.match.event;

import java.util.UUID;

import net.digiturtle.apollo.Vector2;

public class PlayerShootEvent extends PlayerEvent {

	private Vector2 position, velocity;
	
	public PlayerShootEvent () {
		
	}
	
	public PlayerShootEvent (UUID player, Vector2 position, Vector2 velocity) {
		super(player);
		this.position = position;
		this.velocity = velocity;
	}
	
	public Vector2 getPosition () {
		return position;
	}

	public void setPosition (Vector2 position) {
		this.position = position;
	}

	public Vector2 getVelocity () {
		return velocity;
	}

	public void setVelocity (Vector2 velocity) {
		this.velocity = velocity;
	}
	
}
