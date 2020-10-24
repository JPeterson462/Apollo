package net.digiturtle.apollo.match.event;

import java.util.UUID;

import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.match.Explosion;

public class PlayerExplosiveEvent extends Event {
	
	private UUID player;
	
	private Explosion explosion;
	
	private Vector2 position;
	
	public PlayerExplosiveEvent () {
		
	}
	
	public PlayerExplosiveEvent (UUID player, Explosion explosion, Vector2 position) {
		this.player = player;
		this.explosion = explosion;
		this.position = position;
	}

	public UUID getPlayer() {
		return player;
	}

	public void setPlayer(UUID player) {
		this.player = player;
	}

	public Explosion getExplosion() {
		return explosion;
	}

	public void setExplosion(Explosion explosion) {
		this.explosion = explosion;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

}
