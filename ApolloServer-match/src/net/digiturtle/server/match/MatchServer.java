package net.digiturtle.server.match;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.UUID;

import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.IntersectorStub;
import net.digiturtle.apollo.TiledMapLoaderStub;
import net.digiturtle.apollo.VisualFXEngineStub;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.event.Event;
import net.digiturtle.apollo.match.event.MatchEvent;
import net.digiturtle.apollo.match.event.MatchManager;
import net.digiturtle.apollo.match.event.MatchOverEvent;
import net.digiturtle.apollo.match.event.MatchResultEvent;
import net.digiturtle.apollo.match.event.MatchSimulator;
import net.digiturtle.apollo.match.event.PlayerEvent;
import net.digiturtle.apollo.networking.TcpClient;
import net.digiturtle.apollo.networking.UdpServer;

public class MatchServer {
	
	private static UdpServer server;
	private static TcpClient managementClient;
	private static FiberPool fiberPool;
	
	//private static Random random;
	//private static float[] teamBins;
	
	//private static HashMap<UUID, PlayerStatePacket> playerStates;
	
	private static MatchManager matchManager;
	
	public static void main(String[] args) throws FileNotFoundException {
		MatchManager.eventDispatcher = (event) -> {
			if (event instanceof MatchOverEvent) {
				MatchOverEvent matchOver = (MatchOverEvent) event;
				MatchResultEvent matchResult = new MatchResultEvent();
				HashMap<UUID, Integer> pointsPerPlayer = new HashMap<>(); 
				Match match = matchManager.getMatch();
				for (Player player : match.getPlayers()) {
					pointsPerPlayer.put(player.getId(), matchOver.getScores()[player.getTeam()]);
				}
				matchResult.setPoints(pointsPerPlayer);
				managementClient.send(matchResult);
			}
			server.broadcast(event);
		};

		int numPlayers = Integer.parseInt(args[0]);
		
		matchManager = new MatchManager(numPlayers, 1, DebugStuff.newMatchDefinition(numPlayers), 
				new TiledMapLoaderStub(), new IntersectorStub(), new VisualFXEngineStub(),
				(match) -> {
					fiberPool.scheduleTask(20, () -> {
						((MatchSimulator) match.getEventListener()).update(20f / 1000f);
					});
				});
		
		//random = new Random();
		
		//teamBins = SharedUtils.generateBins(2);
		
		//System.setErr(new PrintStream(new FileOutputStream("debug.txt")));
		
		fiberPool = new FiberPool(4);
		server = new UdpServer(4560);
		//playerStates = new HashMap<>();
		server.listen((object, sender) -> {
			if (object instanceof MatchEvent) {
				matchManager.onEvent((Event) object);
			}
			if (object instanceof PlayerEvent) {
				server.forward(object, sender);
			}
		});
		managementClient = new TcpClient("localhost", 4720);
		managementClient.listen((object) -> {
			
		});
		
		fiberPool.scheduleTask(() -> {
			try {
				server.connect();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		fiberPool.scheduleTask(() -> {
			try {
				managementClient.connect();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		while (true) {
			try {
				Thread.sleep(60_000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
