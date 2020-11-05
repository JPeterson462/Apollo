package net.digiturtle.apollo;

import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import net.digiturtle.apollo.Lobby.LobbyStatus;
import net.digiturtle.apollo.screens.Screen;
import net.digiturtle.apollo.screens.Screen.ScreenId;

public class Apollo extends ApplicationAdapter {
	
	public static String debugMessage = "";
	
	public static final UUID userId = UUID.randomUUID();
	public static User user;
	public static Lobby[] lobbies;
	
	private static FiberPool mainPool;
	
	public static void send(Object object) {
		Screen.get().send(object);
	}
	
	@Override
	public void create () {
		mainPool = new FiberPool(1);
		mainPool.scheduleTask(1000, () -> {
			System.out.println("RAM (MB): " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));
		});
		user = new User();
		user.setCoins(1000);
		user.setDamagePowerup(3);
		user.setExplosivesPowerup(2);
		user.setSpeedPowerup(4);
		user.setResiliencePowerup(1);
		lobbies = new Lobby[7];
		for (int i = 0; i < 7; i++) {
			Lobby lobby = new Lobby();
			lobby.setPlayerCount((i%2==0) ? "1/2" : "1/4");
			lobby.setPlayerTotal((i%2==0) ? "1v1" : "2v2");
			lobby.setStatus((i%2==0) ? LobbyStatus.In_Lobby : (i==6)?LobbyStatus.Resetting : LobbyStatus.Active);
			lobby.setWorldName((i%2==0) ? "Alpha Planet" : "Beta Planet");
			lobbies[i] = lobby;
		}
		//FIXME use real values for User,Lobby
		Screen.createAll();
		Screen.set(ScreenId.LOGIN);
	}
	
	public void onPacket(Object object) {
		Screen screen = Screen.get();
		screen.onPacket(object);
	}

	@Override
	public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Screen screen = Screen.get();
		screen.render();
	}
	
	@Override
	public void dispose () {
	}

}
