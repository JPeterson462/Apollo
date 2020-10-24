package net.digiturtle.apollo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import net.digiturtle.apollo.graphics.DebugRenderer;
import net.digiturtle.apollo.match.Explosion;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.ResourceRegion;
import net.digiturtle.apollo.match.event.PlayerExplosiveEvent;
import net.digiturtle.apollo.match.event.PlayerShootEvent;
import net.digiturtle.apollo.match.event.PlayerStateChangeEvent;
import net.digiturtle.apollo.packets.BulletPacket;

public class MatchInputController implements InputProcessor {
	
	private Match match;
	
	private float tileSize;
	
	public MatchInputController(Match match, float tileSize) {
		this.match = match;
		this.tileSize = tileSize;
	}

	@Override
	public boolean keyDown(int keycode) {
		Player player = match.getPlayer(Apollo.userId);
		if (player == null) return false;
        if(keycode == Input.Keys.A) {
        	match.onEvent(new PlayerStateChangeEvent(player, Player.State.WALKING, false, player.getOrientation() | Player.ORIENTATION_LEFT));
        }
        if(keycode == Input.Keys.D) {
            match.onEvent(new PlayerStateChangeEvent(player, Player.State.WALKING, false, player.getOrientation() | Player.ORIENTATION_RIGHT));
        }
        if(keycode == Input.Keys.W) {
            match.onEvent(new PlayerStateChangeEvent(player, Player.State.WALKING, false, player.getOrientation() | Player.ORIENTATION_UP));
        }
        if(keycode == Input.Keys.S) {
            match.onEvent(new PlayerStateChangeEvent(player, Player.State.WALKING, false, player.getOrientation() | Player.ORIENTATION_DOWN));
        }
        	
        return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		Player player = match.getPlayer(Apollo.userId);
		if (player == null) return false;
        if(keycode == Input.Keys.A) {
            match.onEvent(new PlayerStateChangeEvent(player, Player.State.STANDING, false, player.getOrientation() & ~Player.ORIENTATION_LEFT));
        }
        if(keycode == Input.Keys.D) {
        	match.onEvent(new PlayerStateChangeEvent(player, Player.State.STANDING, false, player.getOrientation() & ~Player.ORIENTATION_RIGHT));
        }
        if(keycode == Input.Keys.W) {
        	match.onEvent(new PlayerStateChangeEvent(player, Player.State.STANDING, false, player.getOrientation() & ~Player.ORIENTATION_UP));
        }
        if(keycode == Input.Keys.S) {
            match.onEvent(new PlayerStateChangeEvent(player, Player.State.STANDING, false, player.getOrientation() & ~Player.ORIENTATION_DOWN));
        }
        
        if (keycode == Input.Keys.GRAVE) {
        	System.out.println(player.getPosition() + " | " + 
        				MathUtils.rectangleToString(match.getResourceRegions().get(0).getBounds()) + " | " + 
        				match.getResourceRegions().get(0).getBounds().contains(player.getPosition()) + " | " + 
        				MathUtils.testRectanglePoint(match.getResourceRegions().get(0).getBounds(), player.getPosition()));
        }
        if (keycode == Input.Keys.SPACE) {
        	Vector2 mouse = new Vector2(Gdx.input.getX() - Gdx.graphics.getWidth()/2, -(Gdx.input.getY() - Gdx.graphics.getHeight()/2));
        	Vector2 mouseOnMap = MathUtils.screenToMap(mouse, tileSize);
        	Vector2 direction = mouseOnMap.nor();
        	Vector2 velocity = new Vector2(direction).scl(ApolloSettings.MAX_BULLET_DISTANCE);
        	System.out.println(velocity);
        	BulletPacket bullet = new BulletPacket();
        	bullet.shooter = Apollo.userId;
        	bullet.x = player.getPosition().x;
        	bullet.y = player.getPosition().y;
        	bullet.vx = velocity.x;
        	bullet.vy = velocity.y;
        	match.onEvent(new PlayerShootEvent(match.getPlayer(Apollo.userId), new Vector2(bullet.x, bullet.y), new Vector2(bullet.vx, bullet.vy)));
        	
        	DebugRenderer.addLine(MathUtils.mapToScreen(new Vector2(bullet.x, bullet.y), ApolloSettings.TILE_SIZE),
        			MathUtils.mapToScreen(new Vector2(bullet.x + bullet.vx, bullet.y + bullet.vy), ApolloSettings.TILE_SIZE));

        	DebugRenderer.addLine(MathUtils.mapToScreen(new Vector2(bullet.x, bullet.y), ApolloSettings.TILE_SIZE),
        			MathUtils.mapToScreen(new Vector2(bullet.x + bullet.vx, bullet.y + bullet.vy), ApolloSettings.TILE_SIZE));
        	
        	Apollo.send(bullet);
        }
        if (keycode == Input.Keys.NUM_1) {
        	match.onEvent(new PlayerExplosiveEvent(player, new Explosion(new Vector2(player.getPosition()), 60, .4f), player.getPosition()));
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
				match.onEvent(new PlayerStateChangeEvent(player, Player.State.COLLECTING, false, player.getOrientation()));
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
				match.onEvent(new PlayerStateChangeEvent(player, Player.State.STANDING, true, player.getOrientation()));
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
