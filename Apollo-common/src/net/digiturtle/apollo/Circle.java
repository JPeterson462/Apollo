package net.digiturtle.apollo;

public class Circle {
	
	public float x, y, radius;
	
	public void set (Vector2 position, float radius) {
		x = position.x;
		y = position.y;
		this.radius = radius;
	}
	
	public boolean contains (float x, float y) {
		float dx = x - this.x, dy = y - this.y;
		return dx * dx + dy * dy < radius * radius;
	}

	public String toString () {
		return "Circle[(" + x + ", " + y + "); " + radius + "]";
	}

}
