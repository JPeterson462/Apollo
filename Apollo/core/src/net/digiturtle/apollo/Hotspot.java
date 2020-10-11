package net.digiturtle.apollo;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Hotspot {
	
	private Vector2 position, size;
	private Rectangle rectangle;
	
	public Hotspot () {
		rectangle = new Rectangle();
	}
	
	public void setPosition (Vector2 position) {
		this.position = position;
	}
	
	public Vector2 getPosition () {
		return position;
	}
	
	public void setSize (Vector2 size) {
		this.size = size;
		
	}
	
	public Vector2 getSize () {
		return size;
	}
	
	public Vector2 getCenter () {
		return new Vector2(position.x + size.x/2, position.y + size.y/2);
	}
	
	public Rectangle getBounds () {
		rectangle.set(position.x, position.y - size.y, size.x, size.y);
		return rectangle;
	}

}
