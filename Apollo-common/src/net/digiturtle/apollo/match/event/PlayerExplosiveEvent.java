package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.match.Explosion;
import net.digiturtle.apollo.match.Player;

public class PlayerExplosiveEvent extends Event {
	
	private Player player;
	
	private Explosion explosion;
	
	private Vector2 position;
	
	public PlayerExplosiveEvent () {
		
	}
	
	public PlayerExplosiveEvent (Player player, Explosion explosion, Vector2 position) {
		this.player = player;
		this.explosion = explosion;
		this.position = position;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
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
