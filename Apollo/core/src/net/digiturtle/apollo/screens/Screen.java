package net.digiturtle.apollo.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.digiturtle.apollo.StubInputController;

public abstract class Screen {
	
	public enum ScreenId {
		LOGIN,
		LOBBY,
		MATCH_LOBBY,
		MATCH,
		MATCH_OVER,
		
		;
	}
	
	private static HashMap<ScreenId, Screen> screens;
	private static ScreenId current;
	private static InputProcessor stubInputProcessor = new StubInputController();
	
	public static void createAll () {
		screens = new HashMap<>();
		screens.put(ScreenId.LOGIN, new LoginScreen());
		screens.put(ScreenId.LOBBY, new LobbyScreen());
		screens.put(ScreenId.MATCH_LOBBY, new MatchLobbyScreen());
		screens.put(ScreenId.MATCH, new MatchScreen());
		screens.put(ScreenId.MATCH_OVER, new MatchOverScreen());
		for (Screen screen : screens.values()) {
			screen.create();
		}
	}
	
	public static void set (ScreenId id) {
		current = id;
		Screen next = get();
		if (next.getInputProcessor() != null) {
			Gdx.input.setInputProcessor(next.getInputProcessor());
		} else {
			Gdx.input.setInputProcessor(stubInputProcessor);
		}
	}
	
	public static Screen get () {
		return screens.get(current);
	}
	
	public abstract void create ();
	
	public abstract void render ();
	
	public void send (Object object) {
		
	}
	
	protected OrthographicCamera createCamera () {
		float w = Gdx.graphics.getWidth()/3;
        float h = Gdx.graphics.getHeight()/3;
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();
        return camera;
	}
	
	public abstract void onPacket (Object object);
	
	public InputProcessor getInputProcessor () {
		return null;
	}

}
