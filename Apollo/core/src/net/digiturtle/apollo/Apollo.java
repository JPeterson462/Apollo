package net.digiturtle.apollo;

import java.util.Map;
import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.World;

import net.digiturtle.apollo.GdxIntegration.GdxWorld;
import net.digiturtle.apollo.graphics.ApolloVisualFXEngine;
import net.digiturtle.apollo.graphics.MatchRenderer;
import net.digiturtle.apollo.graphics.RenderablePlayer;
import net.digiturtle.apollo.graphics.VisualFX;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.Resource;
import net.digiturtle.apollo.match.event.Event;
import net.digiturtle.apollo.match.event.MatchConnectEvent;
import net.digiturtle.apollo.match.event.MatchSimulator;
import net.digiturtle.apollo.match.event.MatchStartEvent;
import net.digiturtle.apollo.match.event.PlayerEvent;
import net.digiturtle.apollo.networking.UdpClient;
import net.digiturtle.apollo.packets.MatchStatePacket;
import net.digiturtle.apollo.packets.PlayerStatePacket;

public class Apollo extends ApplicationAdapter {
	
	public static String debugMessage = "";
	
	private MatchRenderer matchRenderer;
	private Match match;
	private MatchSimulator matchSimulator;
	public static final UUID userId = UUID.randomUUID();
	
	private static UdpClient client;
	private FiberPool fiberPool;
	
	public static void send(Object object) {
		client.send(object);
	}
	
	@Override
	public void create () {
		Match.eventForwarder = Apollo::send;
		Match.isClient = (uuid) -> uuid != null && uuid.equals(Apollo.userId);
		client = new UdpClient("localhost", 4560);
		client.listen(this::onPacket);
		fiberPool = new FiberPool(2);
		
		World.setVelocityThreshold(2);
		
		match = new Match(new GdxIntegration.GdxTiledMapLoader(), new GdxIntegration.GdxIntersector(), new ApolloVisualFXEngine());
		matchRenderer = new MatchRenderer(match, new ApolloVisualFXEngine());
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
		if (object instanceof MatchStartEvent) {
			MatchStartEvent matchStartEvent = (MatchStartEvent)object;
			Gdx.app.postRunnable(() -> {
				match.load(matchStartEvent.getMatchDefinition(), new GdxIntegration.GdxTiledMapLoader(), new GdxIntegration.GdxIntersector(), new ApolloVisualFXEngine());
	
				for (Player basePlayer : matchStartEvent.getPlayers()) {
					Player player = new Player(basePlayer.getId(), new VisualFX(), new RenderablePlayer(basePlayer.getTeam())); //match.getPlayer(playerState.uuid);
					player.setTeam(basePlayer.getTeam());
					match.addPlayer(player, basePlayer.getId().equals(Apollo.userId) ? new GdxIntegration.GdxBody((GdxWorld) match.getWorld()) : null);
					if (player.getBody() != null) {
						player.getBody().setTransform(basePlayer.getPosition(), player.getAngle());
						player.getBody().setAngularVelocity(0);
						player.getBody().setLinearVelocity(basePlayer.getVelocity());
					} else {
						player.relocate(basePlayer.getPosition(), basePlayer.getVelocity());
						// FIXME set player orientation
					}
				}
				
				match.respawnAllPlayers();
				
				matchSimulator = new MatchSimulator(match, new ApolloVisualFXEngine());
			});
		}
		if (object instanceof PlayerEvent) {
			Event event = (Event) object;
			event.setRemote(true);
			match.onEvent(event);
		}
		//System.out.println(object);
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
					player.relocate(new net.digiturtle.apollo.Vector2(playerState.x, playerState.y), 
							new net.digiturtle.apollo.Vector2(playerState.vx, playerState.vy));
					if (playerState.orientation == null) { //FIXME
						playerState.orientation = Player.Direction.UP.name();
					}
					player.setDirection(Player.Direction.valueOf(playerState.orientation));
					player.setState(playerState.state != null ? Player.State.valueOf(playerState.state) : Player.State.STANDING);
					player.getBackpack().reset();
					if (playerState.backpack != null && playerState.backpack.contents != null) {
						for (Map.Entry<String, Integer> item : playerState.backpack.contents.entrySet()) {
							player.getBackpack().changeQuantity(Resource.valueOf(item.getKey()), item.getValue());
						}
					}
				}
			}
		}
	}

	private boolean _sentConnect = false;
	
	@Override
	public void render () {
		//if (matchSimulator != null) System.out.println(Math.sqrt(match.getPlayer(Apollo.userId).getVelocity().len2()) + " versus " + ApolloSettings.PLAYER_SPEED);
		{
			if (!_sentConnect) {
				_sentConnect = true;
				client.send(new MatchConnectEvent(Apollo.userId));
			}
		}
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        matchRenderer.render();
        if (matchSimulator != null) {
        	matchSimulator.update(Gdx.graphics.getDeltaTime());
        }
	}
	
	@Override
	public void dispose () {
	}

}
