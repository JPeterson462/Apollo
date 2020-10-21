package net.digiturtle.apollo;

import com.badlogic.gdx.math.Vector2;

public class Explosion {
	
	private Vector2 position;
	private int power;
	private float length;
	
	private float t;
	
	public Explosion (Vector2 position, int power, float length) {
		this.position = position;
		this.power = power;
		this.length = length;
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
	
	public float getLength () {
		return length;
	}
	
	public void update (float dt) {
		t = Math.min(length, t + dt);
	}

}
