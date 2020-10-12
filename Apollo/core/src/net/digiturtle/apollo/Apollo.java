package net.digiturtle.apollo;

import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;

import net.digiturtle.apollo.graphics.MatchRenderer;
import net.digiturtle.apollo.networking.UdpClient;
import net.digiturtle.apollo.packets.BulletPacket;
import net.digiturtle.apollo.packets.ClientConnectPacket;
import net.digiturtle.apollo.packets.MatchStartPacket;
import net.digiturtle.apollo.packets.MatchStatePacket;
import net.digiturtle.apollo.packets.PlayerStatePacket;

public class Apollo extends ApplicationAdapter {
	
	private MatchRenderer matchRenderer;
	private Match match;
	public static final UUID userId = UUID.randomUUID();
	
	private static UdpClient client;
	private FiberPool fiberPool;
	
	public static void send(Object object) {
		client.send(object);
	}
	
	@Override
	public void create () {
		client = new UdpClient("localhost", 4560);
		client.listen(this::onPacket);
		fiberPool = new FiberPool(2);
		
		match = new Match();
		matchRenderer = new MatchRenderer(match);
		matchRenderer.create();
        Gdx.input.setInputProcessor(new MatchInputController(match, ApolloSettings.TILE_SIZE));
        
        fiberPool.scheduleTask(() -> {
        	try {
				client.connect();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        });
	}
	
	public void onPacket(Object object) {
		//System.out.println(object);
		if (object instanceof MatchStartPacket) {
			Gdx.app.postRunnable(() -> {
				// Initialize on UI thread, FIXME to avoid race conditions
				MatchStartPacket matchStart = (MatchStartPacket)object;
				for (PlayerStatePacket playerState : matchStart.playerStates) {
					Player player = new Player(playerState.uuid); //match.getPlayer(playerState.uuid);
					player.setTeam(playerState.team);
					match.addPlayer(player, playerState.uuid.equals(Apollo.userId));
					if (player.getBody() != null) {
						player.getBody().setTransform(new Vector2(playerState.x, playerState.y), playerState.theta);
						player.getBody().setAngularVelocity(playerState.vtheta);
						player.getBody().setLinearVelocity(playerState.vx, playerState.vy);
					} else {
						player.relocate(new Vector2(playerState.x, playerState.y), new Vector2(playerState.vx, playerState.vy));
						// FIXME set player orientation
					}
				}
				fiberPool.scheduleTask(25, () -> {
					PlayerStatePacket playerState = new PlayerStatePacket();
					Player player = match.getPlayer(Apollo.userId);
					playerState.uuid = Apollo.userId;
					playerState.x = player.getPosition().x;
					playerState.y = player.getPosition().y;
					playerState.orientation = player.getDirection().name();
					playerState.vx = player.getVelocity().x;
					playerState.vy = player.getVelocity().y;
					client.send(playerState);
				});
			});
		}
		if (object instanceof MatchStatePacket) {
			MatchStatePacket matchState = (MatchStatePacket)object;
			for (PlayerStatePacket playerState : matchState.playerStates) {
				if (playerState.uuid.equals(Apollo.userId)) {
					continue;
				}
				Player player = match.getPlayer(playerState.uuid);
				if (player == null) {
					continue;//FIXME shouldn't need
				}
				if (player.getBody() == null) {
					player.relocate(new Vector2(playerState.x, playerState.y), new Vector2(playerState.vx, playerState.vy));
					if (playerState.orientation == null) { //FIXME
						playerState.orientation = Player.Direction.UP.name();
					}
					player.setDirection(Player.Direction.valueOf(playerState.orientation));
				}
			}
		}
		if (object instanceof BulletPacket) {
			System.out.println("Received a bullet packet.");
			BulletPacket bullet = (BulletPacket)object;//FIXME need to correct for latency by including time stamp
			if (!Apollo.userId.equals(bullet.shooter)) {
				match.addBullet(new Vector2(bullet.x, bullet.y), new Vector2(bullet.vx, bullet.vy), bullet.shooter);
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
