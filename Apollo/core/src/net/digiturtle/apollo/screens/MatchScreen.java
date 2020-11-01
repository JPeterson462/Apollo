package net.digiturtle.apollo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.World;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.GdxIntegration;
import net.digiturtle.apollo.MatchInputController;
import net.digiturtle.apollo.GdxIntegration.GdxWorld;
import net.digiturtle.apollo.graphics.ApolloVisualFXEngine;
import net.digiturtle.apollo.graphics.MatchRenderer;
import net.digiturtle.apollo.graphics.RenderablePlayer;
import net.digiturtle.apollo.graphics.VisualFX;
import net.digiturtle.apollo.match.Arsenal;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.Arsenal.Powerup;
import net.digiturtle.apollo.match.Arsenal.PowerupStatus;
import net.digiturtle.apollo.match.event.Event;
import net.digiturtle.apollo.match.event.MatchConnectEvent;
import net.digiturtle.apollo.match.event.MatchSimulator;
import net.digiturtle.apollo.match.event.MatchStartEvent;
import net.digiturtle.apollo.match.event.PlayerEvent;
import net.digiturtle.apollo.networking.UdpClient;

public class MatchScreen extends Screen {

	private MatchRenderer matchRenderer;
	private Match match;
	private MatchSimulator matchSimulator;

	private static UdpClient client;
	private FiberPool fiberPool;
	
	private InputProcessor inputProcessor;
	
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
		
        inputProcessor = new MatchInputController(match);
        
        fiberPool.scheduleTask(() -> {
        	try {
				client.connect();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        });
	}

	@Override
	public void send (Object object) {
		client.send(object);
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
	public void onPacket (Object object) {
		if (object instanceof MatchStartEvent) {
			MatchStartEvent matchStartEvent = (MatchStartEvent)object;
			Gdx.app.postRunnable(() -> {
				match.load(matchStartEvent.getMatchDefinition(), new GdxIntegration.GdxTiledMapLoader(), new GdxIntegration.GdxIntersector(), new ApolloVisualFXEngine());
	
				for (Player basePlayer : matchStartEvent.getPlayers()) {
					Player player = new Player(basePlayer.getId(), new VisualFX(), new RenderablePlayer(basePlayer.getTeam())); //match.getPlayer(playerState.uuid);
					player.setTeam(basePlayer.getTeam());
					
					//FIXME
					Arsenal arsenal = new Arsenal();
					arsenal.getStatuses().put(Powerup.DAMAGE, new PowerupStatus(3, Powerup.DAMAGE));
					arsenal.getStatuses().put(Powerup.RESILIENCE, new PowerupStatus(4, Powerup.RESILIENCE));
					arsenal.getStatuses().put(Powerup.SPEED, new PowerupStatus(1, Powerup.SPEED));
					arsenal.getStatuses().put(Powerup.EXPLOSIVES, new PowerupStatus(2, Powerup.EXPLOSIVES));
					player.setArsenal(arsenal);
					
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
	}

	public InputProcessor getInputProcessor () {
		return inputProcessor;
	}


}
