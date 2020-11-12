package net.digiturtle.apollo.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.graphics.TextRenderer;
import net.digiturtle.apollo.match.event.MatchConnectEvent;
import net.digiturtle.apollo.match.event.MatchStartEvent;
import net.digiturtle.apollo.networking.UdpClient;

public class MatchLobbyScreen extends Screen {
	
	private TextRenderer textRenderer;
	private OrthographicCamera camera;
	private boolean _sentConnect;
	
	@Override
	public void create () {
		camera = createCamera();
		textRenderer = new TextRenderer(camera, false);
		textRenderer.create();
	}

	@Override
	public void render () {
		if (Apollo.readyToJoin) {
			_sentConnect = false;
			Apollo.client = new UdpClient(Apollo.matchIp, Apollo.matchPort);
			System.out.println("Connecting to match server -- " + Apollo.matchIp + ":" + Apollo.matchPort);
			Apollo.client.listen(this::onPacket);
			
			Apollo.matchPool.scheduleTask(() -> {
				try {
	        		Apollo.client.connect();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			
			Apollo.readyToJoin = false;
		}
		{
			Apollo.send("What's up??");
			if (!_sentConnect) {
				_sentConnect = true;
				Apollo.client.send(new MatchConnectEvent(Apollo.userId));
			}
		}
		//Screen.set(ScreenId.MATCH);
		textRenderer.begin();
		textRenderer.text("Waiting for match to start...", 20, 20, Color.GREEN);
		textRenderer.end();
	}

	@Override
	public void onPacket (Object object) {
		if (object instanceof MatchStartEvent) {
			Screen.set(ScreenId.MATCH);
			Screen.get().onPacket(object);
		} else {//FIXME why do packets have to get manually pushed to MATCH?
			System.out.println("**** UNHANDLED: " + object);
			Screen.set(ScreenId.MATCH);
			Screen.get().onPacket(object);
		}
	}

	public InputProcessor getInputProcessor () {
		return null;
	}

}
