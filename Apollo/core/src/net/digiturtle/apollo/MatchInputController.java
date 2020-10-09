package net.digiturtle.apollo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

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
        if(keycode == Input.Keys.A)
            player.changeOrientation(player.getOrientation() | Player.ORIENTATION_LEFT);
        if(keycode == Input.Keys.D)
            player.changeOrientation(player.getOrientation() | Player.ORIENTATION_RIGHT);
        if(keycode == Input.Keys.W)
            player.changeOrientation(player.getOrientation() | Player.ORIENTATION_UP);
        if(keycode == Input.Keys.S)
            player.changeOrientation(player.getOrientation() | Player.ORIENTATION_DOWN);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		Player player = match.getPlayer(Apollo.userId);
		if (player == null) return false;
        if(keycode == Input.Keys.A)
            player.changeOrientation(player.getOrientation() & ~Player.ORIENTATION_LEFT);
        if(keycode == Input.Keys.D)
        	player.changeOrientation(player.getOrientation() & ~Player.ORIENTATION_RIGHT);
        if(keycode == Input.Keys.W)
        	player.changeOrientation(player.getOrientation() & ~Player.ORIENTATION_UP);
        if(keycode == Input.Keys.S)
            player.changeOrientation(player.getOrientation() & ~Player.ORIENTATION_DOWN);
        
        if (keycode == Input.Keys.GRAVE) {
        	System.out.println(player.getPosition());
        }
        if (keycode == Input.Keys.SPACE) {//FIXME store some stuff in constants
        	Vector2 mouse = new Vector2(Gdx.input.getX() - Gdx.graphics.getWidth()/2, Gdx.input.getY() - Gdx.graphics.getHeight()/2);
        	Vector2 mouseOnMap = MathUtils.mouseToMap(mouse, tileSize);//.scl(1f / 3f); 
        	//MathUtils.screenToMap(new Vector2(Gdx.input.getX(), Gdx.input.getY()), tileSize);
        	System.out.println(mouse + " Mouse: " + mouseOnMap + " / Player: " + player.getPosition());
        	Vector2 direction = mouseOnMap.sub(player.getPosition()).nor();
        	Vector2 velocity = new Vector2(direction).scl(1024);
        	BulletPacket bullet = new BulletPacket();
        	bullet.shooter = Apollo.userId;
        	bullet.x = player.getPosition().x;
        	bullet.y = player.getPosition().y;
        	bullet.vx = velocity.x;
        	bullet.vy = velocity.y;
        	Bullet b = match.addBullet(new Vector2(bullet.x, bullet.y), new Vector2(bullet.vx, bullet.vy));
        	Apollo.send(bullet);
        }
        return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
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
