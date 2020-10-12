package net.digiturtle.apollo;

import java.util.UUID;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.digiturtle.apollo.graphics.RenderablePlayer;
import net.digiturtle.apollo.graphics.VisualFX;

public class Player {
	
	public enum Direction {
		UP(0, ORIENTATION_UP),
		UP_RIGHT((float)(Math.PI/4), ORIENTATION_UP | ORIENTATION_RIGHT),
		UP_LEFT((float)(-Math.PI/4), ORIENTATION_UP | ORIENTATION_LEFT),
		DOWN((float)Math.PI, ORIENTATION_DOWN),
		DOWN_RIGHT((float)(3*Math.PI/4), ORIENTATION_DOWN | ORIENTATION_RIGHT),
		DOWN_LEFT((float)(-3*Math.PI/4), ORIENTATION_DOWN | ORIENTATION_LEFT),
		RIGHT((float)(Math.PI/2), ORIENTATION_RIGHT),
		LEFT((float)(-Math.PI/2), ORIENTATION_LEFT);
		
		public final float angle;
		public final int orientation;
		Direction(float angle, int orientation) {
			this.angle = angle;
			this.orientation = orientation;
		}
	}
	
	public enum State {
		STANDING,
		WALKING,
		COLLECTING
	}
	
	public static final int ORIENTATION_LEFT = 1<<0, ORIENTATION_RIGHT = 1<<1, ORIENTATION_UP = 1<<2, ORIENTATION_DOWN = 1<<3;
	
	private UUID uuid;
	private Body body;
	private int orientation = 0;
	private RenderablePlayer renderablePlayer;
	private Vector2 position, velocity;
	private int health, team;
	private VisualFX visualFx;
	private Backpack backpack;
	private State state;
	
	public Player (UUID uuid) {
		this.uuid = uuid;
		renderablePlayer = new RenderablePlayer();
		position = new Vector2();
		velocity = new Vector2();
		health = ApolloSettings.PLAYER_HEALTH;
		visualFx = new VisualFX();
		backpack = new Backpack();
		state = State.STANDING;
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
	
	public int getHealth () {
		return health;
	}
	
	public void setHealth (int health) {
		this.health = health;
	}
	
	public int getTeam () {
		return team;
	}
	
	public void setTeam (int team) {
		this.team = team;
	}
	
	public VisualFX getVisualFX () {
		return visualFx;
	}
	
	public Backpack getBackpack() {
		return backpack;
	}
	
	public void update (float dt) {
		if (body == null) {
			// Not simulated with Box2D
			position.add(new Vector2(velocity).scl(dt));
		}
	}
	
	public void setBody (Body body) {
		this.body = body;
	}
	
	public Body getBody () {
		return body;
	}
	
	public Vector2 getPosition () {
		return body != null ? body.getPosition() : position;
	}
	
	public void setPosition (Vector2 position) {
		if (body != null) {
			body.setTransform(position, getRotation(orientation));
		} else {
			this.position.set(position);
		}
	}
	
	public Vector2 getVelocity () {
		return body != null ? body.getLinearVelocity() : velocity;
	}
	
	public void relocate (Vector2 position, Vector2 velocity) {
		this.position.set(position);
		this.velocity.set(velocity);
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
	
	public void setDirection (Direction direction) {
		orientRenderablePlayer(direction);
	}
	
	private void orientRenderablePlayer (Direction direction) {
		switch (direction) {
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
	
	public Direction getDirection () {
		return getDirection(this.orientation);
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
			movement.scl(-1);//FIXME
			if (body != null) {
				body.setAngularVelocity(getRotation(orientation) - body.getAngle());
				body.setLinearVelocity(movement.scl(256));
			} else {
				velocity = movement.scl(256);
			}
			this.orientation = orientation;
			orientRenderablePlayer(getDirection(orientation));
		}
	}
	
	public State getState () {
		return state;
	}
	
	public void setState (State state) {
		this.state = state;
	}

}
