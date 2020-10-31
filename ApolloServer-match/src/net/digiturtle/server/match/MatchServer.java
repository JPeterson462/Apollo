package net.digiturtle.server.match;

import java.io.FileNotFoundException;
import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.match.event.Event;
import net.digiturtle.apollo.match.event.MatchEvent;
import net.digiturtle.apollo.match.event.MatchManager;
import net.digiturtle.apollo.match.event.MatchOverEvent;
import net.digiturtle.apollo.match.event.MatchSimulator;
import net.digiturtle.apollo.match.event.PlayerEvent;
import net.digiturtle.apollo.networking.UdpServer;

public class MatchServer {
	
	private static UdpServer server;
	private static FiberPool fiberPool;
	
	//private static Random random;
	//private static float[] teamBins;
	
	//private static HashMap<UUID, PlayerStatePacket> playerStates;
	
	private static MatchManager matchManager;
	
	public static void main(String[] args) throws FileNotFoundException {
		MatchManager.eventDispatcher = (event) -> {
			if (event instanceof MatchOverEvent) {
				
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
		
		fiberPool = new FiberPool(3);
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
		fiberPool.scheduleTask(() -> {
			try {
				server.connect();
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
