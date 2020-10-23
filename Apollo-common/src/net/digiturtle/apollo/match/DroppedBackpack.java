package net.digiturtle.apollo.match;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Rectangle;
import net.digiturtle.apollo.Vector2;

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
		bounds.width = ApolloSettings.DROPPED_BACKPACK_SIZE;
		bounds.height = ApolloSettings.DROPPED_BACKPACK_SIZE;
		bounds.x = position.x - bounds.width/2;
		bounds.y = position.y - bounds.height/2;
		return bounds;
	}
	
	public void update (float dt) {
		
	}

}
