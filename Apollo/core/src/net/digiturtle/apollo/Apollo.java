package net.digiturtle.apollo;

import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import net.digiturtle.apollo.screens.Screen;
import net.digiturtle.apollo.screens.Screen.ScreenId;

public class Apollo extends ApplicationAdapter {
	
	public static String debugMessage = "";
	
	public static final UUID userId = UUID.randomUUID();
	
	public static void send(Object object) {
		Screen.get().send(object);
	}
	
	@Override
	public void create () {
		Screen.createAll();
		Screen.set(ScreenId.LOGIN);
	}
	
	public void onPacket(Object object) {
		Screen screen = Screen.get();
		screen.onPacket(object);
	}

	@Override
	public void render () {
		Screen screen = Screen.get();
		screen.render();
	}
	
	@Override
	public void dispose () {
	}

}
