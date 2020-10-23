package net.digiturtle.apollo.match;

import net.digiturtle.apollo.Vector2;

public interface IBody {
	
	public void setAngularVelocity (float velocity);
	
	public void setLinearVelocity (float vx, float vy);
	
	public void setLinearVelocity (Vector2 velocity);
	
	public Vector2 getLinearVelocity ();

	public Vector2 getPosition ();
	
	public float getAngle ();
	
	public void setTransform (Vector2 position, float rotation);

}
