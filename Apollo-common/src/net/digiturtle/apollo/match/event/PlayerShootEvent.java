package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.match.Player;

public class PlayerShootEvent extends Event {

	private Player player;
	
	private Vector2 position, velocity;
	
	public PlayerShootEvent (Player player, Vector2 position, Vector2 velocity) {
		this.player = player;
		this.position = position;
		this.velocity = velocity;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
}
