package net.digiturtle.server.match;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.UUID;

import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.IntersectorStub;
import net.digiturtle.apollo.Lobby;
import net.digiturtle.apollo.TiledMapLoaderStub;
import net.digiturtle.apollo.VisualFXEngineStub;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.event.BatchArsenalQuery;
import net.digiturtle.apollo.match.event.Event;
import net.digiturtle.apollo.match.event.MatchEvent;
import net.digiturtle.apollo.match.event.MatchManager;
import net.digiturtle.apollo.match.event.MatchOverEvent;
import net.digiturtle.apollo.match.event.MatchResultEvent;
import net.digiturtle.apollo.match.event.MatchSimulator;
import net.digiturtle.apollo.match.event.MatchStartEvent;
import net.digiturtle.apollo.match.event.MatchStatusEvent;
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
		if (args.length < 6) {
			System.out.println("Usage: MatchServer <match-ip> <match-port> <manager-ip> <manager-port> <num-teams> <num-players>");
			System.exit(-1);
		}
		String matchIp = args[0]; //"127.0.0.1";
		int matchPort = Integer.parseInt(args[1]); //4560;
		String managerIp = args[2]; //"127.0.0.1";
		int managerPort = Integer.parseInt(args[3]); //4720;
		int numTeams = Integer.parseInt(args[4]); //2;
		int numPlayers = Integer.parseInt(args[5]); //1;
		MatchManager.eventDispatcher = (event) -> {
			server.broadcast(event);
			if (event instanceof MatchStartEvent) {
				managementClient.send(new MatchStatusEvent(Lobby.LobbyStatus.Active, matchIp, matchPort));
			}
			if (event instanceof MatchOverEvent) {
				System.out.println("Sending MatchOverEvent to clients (" + ((Event) event).isRemote() + ") " + matchManager.getMatch());
				MatchOverEvent matchOver = (MatchOverEvent) event;
				MatchResultEvent matchResult = new MatchResultEvent();
				HashMap<UUID, Integer> pointsPerPlayer = new HashMap<>(); 
				Match match = matchManager.getMatch();
				System.out.println(match.getPlayers() + " " + java.util.Arrays.toString(matchOver.getScores()) + " " + match.getPlayersMap());
				for (Player player : match.getPlayers()) {
					pointsPerPlayer.put(player.getId(), matchOver.getScores()[player.getTeam()]);
				}
				matchResult.teams = match.getTeams().length;
				matchResult.teamCounts = matchOver.getScores();
				matchResult.setPoints(pointsPerPlayer);
				managementClient.send(matchResult);
				server.broadcast(matchResult);
				managementClient.send(new MatchStatusEvent(Lobby.LobbyStatus.Resetting, matchIp, matchPort));
			}
		};
		MatchManager.managerDispatcher = (object) -> {
			managementClient.send(object);
		};

		matchManager = new MatchManager(numTeams, numPlayers, DebugStuff.newMatchDefinition(numTeams), 
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
		server = new UdpServer(matchPort);
		//playerStates = new HashMap<>();
		server.listen((object, sender) -> {
			System.out.println("[NEW PACKET]\nMatchServer server.listen: " + object);
			if (object instanceof Event) {
				((Event) object).setRemote(true);
			}
			if (object instanceof MatchEvent) {
				matchManager.onEvent((Event) object);
			}
			if (object instanceof PlayerEvent) {
				matchManager.onEvent((Event) object);
				server.forward(object, sender);
			}
		});
		managementClient = new TcpClient(managerIp, managerPort);
		managementClient.listen((object) -> {
			System.out.println("Incoming: " + object);
			if (object instanceof BatchArsenalQuery.Response) {
				matchManager.onArsenalResult((BatchArsenalQuery.Response) object);
			}
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
