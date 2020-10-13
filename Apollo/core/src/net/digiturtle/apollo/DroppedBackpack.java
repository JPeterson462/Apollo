package net.digiturtle.apollo;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DroppedBackpack {
	
	private Backpack backpack;
	private Vector2 position;
	private Rectangle bounds;
	
	public DroppedBackpack () {
		bounds = new Rectangle();
	}
	
	public Backpack getBackpack () {
		return backpack;
	}
	
	public void setBackpack (Backpack backpack) {
		this.backpack = backpack;
	}
	
	public Vector2 getPosition () {
		return position;
	}
	
	public void setPosition (Vector2 position) {
		this.position = position;
	}
	
	public Rectangle getBounds () {
		bounds.setSize(ApolloSettings.DROPPED_BACKPACK_SIZE, ApolloSettings.DROPPED_BACKPACK_SIZE);
		bounds.setCenter(position);
		return bounds;
	}
	
	public void update (float dt) {
		
	}

}
