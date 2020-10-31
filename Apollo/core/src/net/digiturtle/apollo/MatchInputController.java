package net.digiturtle.apollo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import net.digiturtle.apollo.graphics.DebugRenderer;
import net.digiturtle.apollo.match.Arsenal.Powerup;
import net.digiturtle.apollo.match.Explosion;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.ResourceRegion;
import net.digiturtle.apollo.match.event.PlayerExplosiveEvent;
import net.digiturtle.apollo.match.event.PlayerPowerupEvent;
import net.digiturtle.apollo.match.event.PlayerShootEvent;
import net.digiturtle.apollo.match.event.PlayerStateChangeEvent;

public class MatchInputController implements InputProcessor {
	
	private Match match;
	
	public MatchInputController(Match match) {
		this.match = match;
	}

	@Override
	public boolean keyDown(int keycode) {
		Player player = match.getPlayer(Apollo.userId);
		if (player == null) return false;
        if(keycode == Input.Keys.A) {
        	match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.WALKING, false, player.getOrientation() | Player.ORIENTATION_LEFT));
        }
        if(keycode == Input.Keys.D) {
            match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.WALKING, false, player.getOrientation() | Player.ORIENTATION_RIGHT));
        }
        if(keycode == Input.Keys.W) {
            match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.WALKING, false, player.getOrientation() | Player.ORIENTATION_UP));
        }
        if(keycode == Input.Keys.S) {
            match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.WALKING, false, player.getOrientation() | Player.ORIENTATION_DOWN));
        }
        	
        return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		Player player = match.getPlayer(Apollo.userId);
		if (player == null) return false;
        if(keycode == Input.Keys.A) {
            match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.STANDING, false, player.getOrientation() & ~Player.ORIENTATION_LEFT));
        }
        if(keycode == Input.Keys.D) {
        	match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.STANDING, false, player.getOrientation() & ~Player.ORIENTATION_RIGHT));
        }
        if(keycode == Input.Keys.W) {
        	match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.STANDING, false, player.getOrientation() & ~Player.ORIENTATION_UP));
        }
        if(keycode == Input.Keys.S) {
            match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.STANDING, false, player.getOrientation() & ~Player.ORIENTATION_DOWN));
        }
        
        if (keycode == Input.Keys.GRAVE) {
        	System.out.println(player.getPosition() + " | " + 
        				MathUtils.rectangleToString(match.getResourceRegions().get(0).getBounds()) + " | " + 
        				match.getResourceRegions().get(0).getBounds().contains(player.getPosition()) + " | " + 
        				MathUtils.testRectanglePoint(match.getResourceRegions().get(0).getBounds(), player.getPosition()));
        }
        if (keycode == Input.Keys.SPACE) {
        	Vector2 direction = MathUtils.getMouseDirection(new Vector2(Gdx.input.getX(), Gdx.input.getY()), 
        			new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        	Vector2 velocity = new Vector2(direction).scl(ApolloSettings.MAX_BULLET_DISTANCE);
        	System.out.println(velocity);
        	match.onEvent(new PlayerShootEvent(Apollo.userId, new Vector2(player.getPosition()), new Vector2(velocity)));
        	
        	DebugRenderer.addLine(MathUtils.mapToScreen(new Vector2(player.getPosition()), ApolloSettings.TILE_SIZE),
        			MathUtils.mapToScreen(new Vector2(player.getPosition()).add(velocity), ApolloSettings.TILE_SIZE));

        	DebugRenderer.addLine(MathUtils.mapToScreen(new Vector2(player.getPosition()), ApolloSettings.TILE_SIZE),
        			MathUtils.mapToScreen(new Vector2(player.getPosition()).add(velocity), ApolloSettings.TILE_SIZE));
        	
        	//Apollo.send(bullet);
        }
        if (keycode == Input.Keys.NUM_1 && player.getArsenal().getStatuses().get(Powerup.SPEED).getRemaining() > 0) {
        	match.onEvent(new PlayerPowerupEvent(player.getId(), Powerup.SPEED, player.getArsenal().getStatuses().get(Powerup.SPEED)));
        }
        if (keycode == Input.Keys.NUM_2 && player.getArsenal().getStatuses().get(Powerup.DAMAGE).getRemaining() > 0) {
        	match.onEvent(new PlayerPowerupEvent(player.getId(), Powerup.DAMAGE, player.getArsenal().getStatuses().get(Powerup.DAMAGE)));
        }
        if (keycode == Input.Keys.NUM_3 && player.getArsenal().getStatuses().get(Powerup.RESILIENCE).getRemaining() > 0) {
        	match.onEvent(new PlayerPowerupEvent(player.getId(), Powerup.RESILIENCE, player.getArsenal().getStatuses().get(Powerup.RESILIENCE)));
        }
        if (keycode == Input.Keys.NUM_4 && player.getArsenal().getStatuses().get(Powerup.EXPLOSIVES).getRemaining() > 0) {
        	Vector2 direction = MathUtils.getMouseDirection(new Vector2(Gdx.input.getX(), Gdx.input.getY()),
        			new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        	RenderPath renderPath = RenderPath.createdProjectedArc(player.getPosition(), ApolloSettings.EXPLOSION_DISTANCE, 
        			new Vector2(player.getPosition()).add(direction.x * ApolloSettings.EXPLOSION_DISTANCE, direction.y * ApolloSettings.EXPLOSION_DISTANCE), ApolloSettings.EXPLOSION_PATH_INTERVALS);
        	match.onEvent(new PlayerExplosiveEvent(player.getId(), 
        			new Explosion(new Vector2(player.getPosition()).add(direction.x * ApolloSettings.EXPLOSION_DISTANCE, direction.y * ApolloSettings.EXPLOSION_DISTANCE), 
        					ApolloSettings.EXPLOSION_POWER, ApolloSettings.EXPLOSION_TIME, renderPath.getPoints(), ApolloSettings.EXPLOSION_TIME_TO_HIT, player.getId()), player.getPosition()));
        }
        return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	//private long startedCollecting;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {// Mouse down
		Player player = match.getPlayer(Apollo.userId);
		Apollo.debugMessage = "Mouse Down: " + button;
		if (button == Input.Buttons.LEFT) {
			ResourceRegion currentRegion = match.getResourceRegion(player);
			if (currentRegion != null) {
				match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.COLLECTING, false, player.getOrientation()));
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {// Mouse up
		Player player = match.getPlayer(Apollo.userId);
		Apollo.debugMessage = "Mouse Up: " + button;
		if (button == Input.Buttons.LEFT) {
			if (player.getState().equals(Player.State.COLLECTING)) {
				match.onEvent(new PlayerStateChangeEvent(player.getId(), Player.State.STANDING, true, player.getOrientation()));
			}
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
