package net.digiturtle.apollo;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class ButtonInputController implements InputProcessor {
	
	private int[] states;
	private Button[] buttons;
	private int current = -1;
	private Consumer<String> eventHandler;
	
	public ButtonInputController (Button[] buttons, int[] states, Consumer<String> eventHandler) {
		this.buttons = buttons;
		this.states = states;
		this.eventHandler = eventHandler;
	}

	@Override public boolean keyDown (int keycode) { return false; }
	@Override public boolean keyUp (int keycode) { 
		if (keycode == Input.Keys.GRAVE) System.out.println(Gdx.input.getX()/3 + "," + Gdx.input.getY()/3 + " -- " + buttons[4].bounds);
		return false; }
	@Override public boolean keyTyped (char character) { return false; }
	@Override public boolean touchDragged (int screenX, int screenY, int pointer) { return false; }
	@Override public boolean scrolled (int amount) { return false; }
	
	private boolean mouseInButton (Rectangle rectangle, int screenX, int screenY) {
		screenX /= 3;
		screenY /= 3;
		return rectangle.contains(screenX, screenY);
	}
	
	private int getButton (int screenX, int screenY) {
		for (int i = 0; i < buttons.length; i++) {
			if (mouseInButton(buttons[i].bounds, screenX, screenY)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		int now = getButton(screenX, screenY);
		if (now != -1 && button == Input.Buttons.LEFT) {
			states[now] = Button.STATE_DOWN;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		int now = getButton(screenX, screenY);
		if (now != -1 && button == Input.Buttons.LEFT) {
			eventHandler.accept(buttons[now].event);
			states[now] = Button.STATE_HOVER;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		if (screenX < 0 || screenY < 0) {
			return false;
		}
		int now = getButton(screenX, screenY);
		if (now != current) { // for simplicity, one or the other is -1 (just for how my buttons are laid out)
			int btn = Math.max(now, current);
			states[btn] = now == -1 ? Button.STATE_DEFAULT : Math.max(states[btn], Button.STATE_HOVER);
			current = now;
			return true;
		}
		return false;
	}

}
