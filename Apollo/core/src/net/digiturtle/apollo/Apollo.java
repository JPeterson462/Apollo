package net.digiturtle.apollo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import net.digiturtle.apollo.networking.TcpClient;
import net.digiturtle.apollo.networking.UdpClient;
import net.digiturtle.apollo.screens.Screen;
import net.digiturtle.apollo.screens.Screen.ScreenId;

public class Apollo extends ApplicationAdapter {
	
	public static String debugMessage = "";
	
	public static UUID userId;
	public static User user;
	public static Lobby[] lobbies;
	
	public static int[] teamCounts;
	public static int numberOfTeams;
	public static boolean weWon, readyToJoin;
	
	public static String matchIp;
	public static int matchPort;
	
	private static FiberPool mainPool;
	public static FiberPool matchPool;
	
	private static TcpClient managerClient;
	public static UdpClient client;
	
	public static String productKey;
	
	public static void send (Object object) {
		Screen.get().send(object);
	}
	
	public static void sendToMain (Object object) {
		managerClient.send(object);
	}
	
	@Override
	public void create () {
		try {
			System.setOut(new PrintStream(new FileOutputStream("log_" + Apollo.productKey + ".out.txt", true)));
			System.setErr(new PrintStream(new FileOutputStream("log_" + Apollo.productKey + ".err.txt", true)));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Sounds.create();
		matchPool = new FiberPool(1);
		managerClient = new TcpClient("localhost", 4720);
		managerClient.listen((object) -> {
			Screen.get().onManagerPacket(object);
		});
		mainPool = new FiberPool(2);
		mainPool.scheduleTask(() -> {
			try {
				managerClient.connect();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		mainPool.scheduleTask(1000, () -> {
			System.out.println("RAM (MB): " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));
		});
		user = new User();
		lobbies = new Lobby[0];
		Screen.createAll();
		Screen.set(ScreenId.LOGIN);
	}
	
	public void onPacket(Object object) {
		Screen screen = Screen.get();
		screen.onPacket(object);
	}

	@Override
	public void render () {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Screen screen = Screen.get();
		//System.out.println("Now: " + screen);
		screen.render();
	}
	
	@Override
	public void dispose () {
	}

}
