package net.digiturtle.apollo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.ApolloSettings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Apollo.productKey = "ABCD" + (Math.random() < 0.5 ? "E" : "F") + ((int) (Math.random() * 10));
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Apollo " + ApolloSettings.VERSION;
		config.width = 1278;
		config.height = 720;
		new LwjglApplication(new Apollo(), config);
	}
}
