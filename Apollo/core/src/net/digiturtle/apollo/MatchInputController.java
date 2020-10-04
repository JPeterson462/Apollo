package net.digiturtle.apollo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class MatchInputController implements InputProcessor {
	
	private Match match;
	
	public MatchInputController(Match match) {
		this.match = match;
	}

	@Override
	public boolean keyDown(int keycode) {
		Player player = match.getPlayer();
        if(keycode == Input.Keys.A)
            match.getPlayer().changeOrientation(player.getOrientation() | Player.ORIENTATION_LEFT);
        if(keycode == Input.Keys.D)
            match.getPlayer().changeOrientation(player.getOrientation() | Player.ORIENTATION_RIGHT);
        if(keycode == Input.Keys.W)
            match.getPlayer().changeOrientation(player.getOrientation() | Player.ORIENTATION_UP);
        if(keycode == Input.Keys.S)
            match.getPlayer().changeOrientation(player.getOrientation() | Player.ORIENTATION_DOWN);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		Player player = match.getPlayer();
        if(keycode == Input.Keys.A)
            match.getPlayer().changeOrientation(player.getOrientation() & ~Player.ORIENTATION_LEFT);
        if(keycode == Input.Keys.D)
            match.getPlayer().changeOrientation(player.getOrientation() & ~Player.ORIENTATION_RIGHT);
        if(keycode == Input.Keys.W)
            match.getPlayer().changeOrientation(player.getOrientation() & ~Player.ORIENTATION_UP);
        if(keycode == Input.Keys.S)
            match.getPlayer().changeOrientation(player.getOrientation() & ~Player.ORIENTATION_DOWN);
        return false;
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
