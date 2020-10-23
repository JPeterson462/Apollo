package net.digiturtle.apollo;

public class Rectangle {
	
	public float x, y, width, height;
	
	public Rectangle () {
		
	}
	
	public Rectangle (float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void set (float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contains (float x, float y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}
	
	public boolean contains (Vector2 vector) {
		return contains(vector.x, vector.y);
	}
	
	public String toString () {
		return "Rectangle[(" + x + ", " + y + "); (" + width + ", " + height + ")]";
	}

}
