package net.digiturtle.apollo.match;

import java.util.HashMap;
import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.match.Arsenal.Powerup;

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
		STANDING(ApolloSettings.PLAYER_STANDING_FRAME, 1, 1f),
		WALKING(ApolloSettings.PLAYER_WALKING_FRAME, ApolloSettings.PLAYER_WALKING_FRAME_COUNT, .5f/(float)ApolloSettings.PLAYER_WALKING_FRAME_COUNT),
		COLLECTING(0,0,1f),
		THROWING(ApolloSettings.PLAYER_THROWING_FRAME, ApolloSettings.PLAYER_THROWING_FRAME_COUNT, .4f/(float)ApolloSettings.PLAYER_THROWING_FRAME_COUNT),
		;
		
		public final int frame, numFrames;
		public final float timePerFrame;
		State(int frame, int numFrames, float timePerFrame) {
			this.frame = frame;
			this.numFrames = numFrames;
			this.timePerFrame = timePerFrame;
		}
	}
	
	public static final int ORIENTATION_LEFT = 1<<0, ORIENTATION_RIGHT = 1<<1, ORIENTATION_UP = 1<<2, ORIENTATION_DOWN = 1<<3;
	
	private UUID uuid;
	private IBody body;
	private int orientation = 0;
	private Direction cache;
	private IRenderablePlayer renderablePlayer;
	private Vector2 position, velocity;
	private int health, team;
	private IVisualFX visualFx;
	private Backpack backpack;
	private State state, temporaryState = null;
	private float temporaryStateLen;
	private Arsenal arsenal;
	private HashMap<Powerup, Float> timeLeft;
	
	public Player (UUID uuid, IVisualFX visualFx, IRenderablePlayer renderablePlayer) {
		this.uuid = uuid;
		this.renderablePlayer = renderablePlayer;
		position = new Vector2();
		velocity = new Vector2();
		health = ApolloSettings.PLAYER_HEALTH;
		this.visualFx = visualFx;
		backpack = new Backpack();
		state = State.STANDING;
		arsenal = new Arsenal();
		timeLeft = new HashMap<>();
	}
	
	public void engagePowerup (Powerup powerup) {
		timeLeft.put(powerup, powerup.time);
	}
	
	public void clearPowerups () {
		timeLeft.clear();
	}
	
	public float getPowerupTimeLeft (Powerup powerup) {
		return timeLeft.getOrDefault(powerup, 0f);
	}
	
	public UUID getId () {
		return uuid;
	}
	
	public int getOrientation () {
		return orientation;
	}
	
	public IRenderablePlayer getRenderablePlayer() {
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
		if (renderablePlayer != null) {
			renderablePlayer.setTeam(team);
		}
	}
	
	public IVisualFX getVisualFX () {
		return visualFx;
	}
	
	public Backpack getBackpack () {
		return backpack;
	}
	
	public void setBackpack (Backpack backpack) {
		this.backpack = backpack;
	}
	
	public Arsenal getArsenal () {
		return arsenal;
	}
	
	public void setArsenal (Arsenal arsenal) {
		this.arsenal = arsenal;
	}
	
	public float getAngle () {
		return body != null ? body.getAngle() : getDirection().angle;
	}
	
	public void update (float dt) {
		for (Powerup powerup : timeLeft.keySet()) {
			timeLeft.put(powerup, Math.max(0,timeLeft.get(powerup) - dt));
		}
		if (temporaryStateLen > 0) {
			temporaryStateLen -= dt;
			if (temporaryStateLen <= 0) {
				temporaryState = null;
				temporaryStateLen = 0;
				renderablePlayer.onStateChange(state);
			}
		}
		if (body == null) {
			// Not simulated with Box2D
			position.add(new Vector2(velocity).scl(dt));
		}
		if (renderablePlayer != null) {
			renderablePlayer.update(dt);
		}
	}
	
	public void setBody (IBody body) {
		this.body = body;
	}
	
	public IBody getBody () {
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
		if (renderablePlayer == null) return;
		switch (direction) {
		case DOWN:
			renderablePlayer.setFrame(ApolloSettings.PLAYER_FRAME_DOWN);
			break;
		case DOWN_LEFT:
			renderablePlayer.setFrame(ApolloSettings.PLAYER_FRAME_DOWN_LEFT);
			break;
		case DOWN_RIGHT:
			renderablePlayer.setFrame(ApolloSettings.PLAYER_FRAME_DOWN_RIGHT);
			break;
		case LEFT:
			renderablePlayer.setFrame(ApolloSettings.PLAYER_FRAME_LEFT);
			break;
		case RIGHT:
			renderablePlayer.setFrame(ApolloSettings.PLAYER_FRAME_RIGHT);
			break;
		case UP:
			renderablePlayer.setFrame(ApolloSettings.PLAYER_FRAME_UP);
			break;
		case UP_LEFT:
			renderablePlayer.setFrame(ApolloSettings.PLAYER_FRAME_UP_LEFT);
			break;
		case UP_RIGHT:
			renderablePlayer.setFrame(ApolloSettings.PLAYER_FRAME_UP_RIGHT);
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
				cache = Direction.UP_LEFT;
			}
			else if ((orientation & ORIENTATION_RIGHT) != 0) {
				cache = Direction.UP_RIGHT;
			}
			else {
				cache = Direction.UP;
			}
		}
		else if ((orientation & ORIENTATION_DOWN) != 0) {
			if ((orientation & ORIENTATION_LEFT) != 0) {
				cache = Direction.DOWN_LEFT;
			}
			else if ((orientation & ORIENTATION_RIGHT) != 0) {
				cache = Direction.DOWN_RIGHT;
			}
			else {
				cache = Direction.DOWN;
			}
		}
		else {
			if ((orientation & ORIENTATION_LEFT) != 0) {
				cache = Direction.LEFT;
			}
			else if ((orientation & ORIENTATION_RIGHT) != 0) {
				cache = Direction.RIGHT;
			}
			else {
				/*if (cache == null) {
					cache = Direction.DOWN;
				}*/
				if (orientation != this.orientation) {
					cache = getDirection(this.orientation);
				}
			}
		}
		if (cache == null) {
			cache = Direction.DOWN;
		}
		return cache;
	}
	
	public float restoreSpeed () {
		if (state == Player.State.WALKING) {
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
			movement.scl(-1);
			float speedBoost = getPowerupTimeLeft(Powerup.SPEED) > 0 ? Powerup.SPEED.boosts[this.getArsenal().getStatuses().get(Powerup.SPEED).getLevel()] : 1;
			if (body != null) {
				body.setAngularVelocity(getRotation(orientation) - body.getAngle());
				body.setLinearVelocity(movement.scl(ApolloSettings.PLAYER_SPEED * speedBoost));
			} else {
				velocity = movement.scl(ApolloSettings.PLAYER_SPEED * speedBoost);
			}
			return movement.len2();
		} else {
			if (body != null) {
				body.setLinearVelocity(0, 0);
			} else {
				velocity = new Vector2();
			}
			return 0;
		}
	}
	
	public void changeOrientation (int orientation) {
		orientRenderablePlayer(getDirection(orientation));
		this.orientation = orientation;
	}
	
	public void setTemporaryState (State state, float len) {
		temporaryState = state;
		temporaryStateLen = len;
		if (renderablePlayer != null) {
			renderablePlayer.onStateChange(state);
		}
	}
	
	public State getState () {
		return state;
	}
	
	public void setState (State state) {
		this.state = state;
		if (renderablePlayer != null) {
			if (temporaryState == null || state != State.STANDING) { // Don't lose the temporary state if going to STANDING
				renderablePlayer.onStateChange(state);
			}
		}
	}

}
