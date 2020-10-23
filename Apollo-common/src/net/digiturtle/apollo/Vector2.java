package net.digiturtle.apollo;

public class Vector2 {
	
	public float x, y;
	
	public Vector2 () {
		
	}
	
	public Vector2 (float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2 (Vector2 src) {
		x = src.x;
		y = src.y;
	}
	
	public void set (float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set (Vector2 src) {
		x = src.x;
		y = src.y;
	}
	
	public Vector2 add (float dx, float dy) {
		x += dx;
		y += dy;
		return this;
	}

	public Vector2 add (Vector2 delta) {
		x += delta.x;
		y += delta.y;
		return this;
	}
	
	public float len2 () {
		return x * x + y * y;
	}
	
	public Vector2 nor () {
		float len = (float)Math.sqrt(x * x + y * y);
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}
	
	public Vector2 scl (float s) {
		x *= s;
		y *= s;
		return this;
	}
	
	public String toString () {
		return "Vector2[" + x + ", " + y + "]";
	}

}
