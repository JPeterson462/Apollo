package net.digiturtle.apollo.match;

import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Vector2;

public class Explosion {
	
	private Vector2 position;
	private int power;
	private float length;
	private Vector2[] path;
	private float delay;
	private UUID cause;
	
	private float t;
	
	public Explosion () {
		
	}
	
	public Explosion (Vector2 position, int power, float length, Vector2[] path, float delay, UUID cause) {
		this.position = position;
		this.power = power;
		this.length = length;
		this.path = path;
		this.delay = delay;
		this.cause = cause;
	}
	
	public Vector2 getPosition () {
		return position;
	}
	
	public int getPower () {
		return power;
	}
	
	public float getTime () {
		return t;
	}
	
	public float getDelay () {
		return delay;
	}
	
	public float getLength () {
		return length;
	}
	
	public Vector2[] getPath () {
		return path;
	}
	
	public UUID getCause () {
		return cause;
	}
	
	public void update (float dt) {
		t = Math.min(length + delay + ApolloSettings.EXPLOSIVE_THROW_DELAY, t + dt);
	}

}
