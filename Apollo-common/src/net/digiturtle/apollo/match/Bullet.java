package net.digiturtle.apollo.match;

import java.util.UUID;

import net.digiturtle.apollo.Vector2;

public class Bullet {
	
	private Vector2 position, velocity;
	
	private UUID shooter;
	
	public Bullet (Vector2 position, Vector2 velocity, UUID shooter) {
		this.position = position;
		this.velocity = velocity;
		this.shooter = shooter;
	}
	
	public void update (float dt) {
		position = new Vector2(position.x + dt * velocity.x, position.y + dt * velocity.y);
	}
	
	public Vector2 getPosition () {
		return position;
	}
	
	public Vector2 getVelocity () {
		return velocity;
	}
	
	public UUID getShooter() {
		return shooter;
	}

}
