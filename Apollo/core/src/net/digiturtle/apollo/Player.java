package net.digiturtle.apollo;

import java.util.UUID;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player {
	
	public enum Direction {
		UP(0),
		UP_RIGHT((float)(Math.PI/4)),
		UP_LEFT((float)(-Math.PI/4)),
		DOWN((float)Math.PI),
		DOWN_RIGHT((float)(3*Math.PI/4)),
		DOWN_LEFT((float)(-3*Math.PI/4)),
		RIGHT((float)(Math.PI/2)),
		LEFT((float)(-Math.PI/2));
		
		public final float angle;
		Direction(float angle) {
			this.angle = angle;
		}
	}
	
	public static final int ORIENTATION_LEFT = 1<<0, ORIENTATION_RIGHT = 1<<1, ORIENTATION_UP = 1<<2, ORIENTATION_DOWN = 1<<3;
	
	private UUID uuid;
	private Body body;
	private int orientation = 0;
	private RenderablePlayer renderablePlayer;
	
	public Player (UUID uuid) {
		this.uuid = uuid;
		renderablePlayer = new RenderablePlayer();
	}
	
	public UUID getId () {
		return uuid;
	}
	
	public int getOrientation () {
		return orientation;
	}
	
	public RenderablePlayer getRenderablePlayer() {
		return renderablePlayer;
	}
	
	public void setBody (Body body) {
		this.body = body;
	}
	
	public Body getBody () {
		return body;
	}
	
	public Vector2 getPosition () {
		return body.getPosition();
	}
	
	private float getRotation (int orientation) {
		if ((orientation & (ORIENTATION_LEFT | ORIENTATION_RIGHT)) != 0) {
			orientation -= (ORIENTATION_LEFT | ORIENTATION_RIGHT);
		}
		if ((orientation & (ORIENTATION_UP | ORIENTATION_DOWN)) != 0) {
			orientation -= (ORIENTATION_UP | ORIENTATION_DOWN);
		}
		return getDirection(orientation).angle;
	}
	
	public Direction getDirection (int orientation) {
		if ((orientation & ORIENTATION_UP) != 0) {
			if ((orientation & ORIENTATION_LEFT) != 0) {
				return Direction.UP_LEFT;
			}
			else if ((orientation & ORIENTATION_RIGHT) != 0) {
				return Direction.UP_RIGHT;
			}
			else {
				return Direction.UP;
			}
		}
		else if ((orientation & ORIENTATION_DOWN) != 0) {
			if ((orientation & ORIENTATION_LEFT) != 0) {
				return Direction.DOWN_LEFT;
			}
			else if ((orientation & ORIENTATION_RIGHT) != 0) {
				return Direction.DOWN_RIGHT;
			}
			else {
				return Direction.DOWN;
			}
		}
		else {
			if ((orientation & ORIENTATION_LEFT) != 0) {
				return Direction.LEFT;
			}
			else if ((orientation & ORIENTATION_RIGHT) != 0) {
				return Direction.RIGHT;
			}
			else {
				return Direction.UP;
			}
		}
	}
	
	public void changeOrientation(int orientation) {
		if (orientation != this.orientation) {
			Vector2 movement = new Vector2();
			if ((orientation & ORIENTATION_LEFT) != 0) {
				movement.add(1, -1);
			}
			if ((orientation & ORIENTATION_RIGHT) != 0) {
				movement.add(-1, 1);
			}
			if ((orientation & ORIENTATION_UP) != 0) {
				movement.add(-1, -1);
			}
			if ((orientation & ORIENTATION_DOWN) != 0) {
				movement.add(1, 1);
			}
			movement.nor();
			body.setAngularVelocity(getRotation(orientation) - body.getAngle());
			body.setLinearVelocity(movement.scl(256));
			this.orientation = orientation;
			switch (getDirection(orientation)) {
			case DOWN:
				renderablePlayer.setFrame(5);
				break;
			case DOWN_LEFT:
				renderablePlayer.setFrame(6);
				break;
			case DOWN_RIGHT:
				renderablePlayer.setFrame(4);
				break;
			case LEFT:
				renderablePlayer.setFrame(7);
				break;
			case RIGHT:
				renderablePlayer.setFrame(3);
				break;
			case UP:
				renderablePlayer.setFrame(1);
				break;
			case UP_LEFT:
				renderablePlayer.setFrame(0);
				break;
			case UP_RIGHT:
				renderablePlayer.setFrame(2);
				break;
			default:
				break;
			}
		}
	}

}
