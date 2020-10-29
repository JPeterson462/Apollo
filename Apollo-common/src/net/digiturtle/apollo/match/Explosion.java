package net.digiturtle.apollo.match;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Vector2;

public class Explosion {
	
	private Vector2 position;
	private int power;
	private float length;
	private Vector2[] path;
	private float delay;
	
	private float t;
	
	public Explosion () {
		
	}
	
	public Explosion (Vector2 position, int power, float length, Vector2[] path, float delay) {
		this.position = position;
		this.power = power;
		this.length = length;
		this.path = path;
		this.delay = delay;
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
	
	public void update (float dt) {
		t = Math.min(length + delay + ApolloSettings.EXPLOSIVE_THROW_DELAY, t + dt);
	}

}
