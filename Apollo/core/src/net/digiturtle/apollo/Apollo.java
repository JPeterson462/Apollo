package net.digiturtle.apollo;

import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;

import net.digiturtle.apollo.networking.UdpClient;
import net.digiturtle.apollo.packets.ClientConnectPacket;
import net.digiturtle.apollo.packets.MatchStartPacket;
import net.digiturtle.apollo.packets.PlayerStatePacket;

public class Apollo extends ApplicationAdapter {
	
	private MatchRenderer matchRenderer;
	private Match match;
	private UUID userId;
	
	private UdpClient client;
	private FiberPool fiberPool;
	
	@Override
	public void create () {
		client = new UdpClient("localhost", 4560);
		client.listen(this::onPacket);
		fiberPool = new FiberPool(1);
		
		userId = UUID.randomUUID();
		match = new Match();
		match.addPlayer(new Player(userId));
		matchRenderer = new MatchRenderer(match);
		matchRenderer.create();
        Gdx.input.setInputProcessor(new MatchInputController(match));
        
        fiberPool.scheduleTask(() -> {
        	try {
				client.connect();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        });
	}
	
	public void onPacket(Object object) {
		if (object instanceof MatchStartPacket) {
			MatchStartPacket matchStart = (MatchStartPacket)object;
			for (PlayerStatePacket playerState : matchStart.playerStates) {
				Player player = match.getPlayer(playerState.uuid);
				player.getBody().setTransform(new Vector2(playerState.x, playerState.y), playerState.theta);
				player.getBody().setAngularVelocity(playerState.vtheta);
				player.getBody().setLinearVelocity(playerState.vx, playerState.vy);
			}
		}
	}

	private boolean _sentConnect = false;
	
	@Override
	public void render () {
		{
			if (!_sentConnect) {
				_sentConnect = true;
				ClientConnectPacket packet = new ClientConnectPacket();
				packet.clientId = userId;
				client.send(packet);
			}
		}
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        matchRenderer.render();
        match.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
	}

}
