package net.digiturtle.server.match;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.IntersectorStub;
import net.digiturtle.apollo.Lobby;
import net.digiturtle.apollo.TiledMapLoaderStub;
import net.digiturtle.apollo.VisualFXEngineStub;
import net.digiturtle.apollo.definitions.TeamDefinition;
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
	private static FiberPool fiberPool, matchFibers;
	
	private static int numTeams, numPlayers;
	
	//private static Random random;
	//private static float[] teamBins;
	
	//private static HashMap<UUID, PlayerStatePacket> playerStates;
	
	private static MatchManager matchManager;
	
	private static void setupMatchManager () {
		System.out.println("Setting up Match Server...");
		if (matchManager != null) {
			matchFibers.stopTask(0);
			matchFibers.reset();
		}
		matchManager = new MatchManager(numTeams, numPlayers, DebugStuff.newMatchDefinition(numTeams), 
				new TiledMapLoaderStub(), new IntersectorStub(), new VisualFXEngineStub(),
				(match) -> {
					matchFibers.scheduleTask(20, () -> {
						((MatchSimulator) match.getEventListener()).update(20f / 1000f);
						if (match.getTimeLeft() <= 0) {
							int[] points = new int[TeamDefinition.COLOR_COUNT];
							for (int i = 0; i < points.length && i < match.getTeams().length; i++) {
								points[i] = ApolloSettings.getResourceValue(match.getTeams()[i].getBank().getContents());
							}
							matchManager.onEvent(new MatchOverEvent(points));
						}
					});
				});
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		matchFibers = new FiberPool(1);
		try {
			System.setOut(new PrintStream(new FileOutputStream("log.out.txt", true)));
			System.setErr(new PrintStream(new FileOutputStream("log.err.txt", true)));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		if (args.length < 6) {
			System.out.println("Usage: MatchServer <match-ip> <match-port> <manager-ip> <manager-port> <num-teams> <num-players>");
			System.exit(-1);
		}
		String matchIp = args[0]; //"127.0.0.1";
		int matchPort = Integer.parseInt(args[1]); //4560;
		String managerIp = args[2]; //"127.0.0.1";
		int managerPort = Integer.parseInt(args[3]); //4720;
		numTeams = Integer.parseInt(args[4]); //2;
		numPlayers = Integer.parseInt(args[5]); //1;
		System.out.println("Match Server: " + matchIp + ":" + matchPort);
		System.out.println("Manager Server: " + managerIp + ":" + managerPort);
		System.out.println(numTeams + " teams, each with " + numPlayers + " player(s)");
		MatchManager.eventDispatcher = (event) -> {
			server.broadcast(event);
			System.out.println("Broadcasting " + event);
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
				
				System.out.println(match.getTeams() + " << TEAMS");
				
				if (match.getTeams() != null) {
					matchResult.teams = match.getTeams().length;
					matchResult.teamCounts = matchOver.getScores();
					matchResult.setPoints(pointsPerPlayer);
					managementClient.send(matchResult);
					server.broadcast(matchResult);
					
					managementClient.send(new MatchStatusEvent(Lobby.LobbyStatus.Resetting, matchIp, matchPort));
					
					System.out.println("========================= MATCH =========================");
					
					setupMatchManager();
					
					managementClient.send(new MatchStatusEvent(Lobby.LobbyStatus.In_Lobby, matchIp, matchPort));
				}
			}
		};
		MatchManager.managerDispatcher = (object) -> {
			managementClient.send(object);
		};

		setupMatchManager();
		
		//random = new Random();
		
		//teamBins = SharedUtils.generateBins(2);
		
		//System.setErr(new PrintStream(new FileOutputStream("debug.txt")));
		
		fiberPool = new FiberPool(4);
		server = new UdpServer(matchPort);
		//playerStates = new HashMap<>();
		server.listen((object, sender) -> {
			if (object instanceof Event) {
				((Event) object).setRemote(true);
			}
			if (object instanceof MatchOverEvent) {
				// Ignore from clients, handle on server
			}
			else if (object instanceof MatchEvent) {
				System.out.println("[NEW PACKET]\nMatchServer server.listen: " + object);
				matchManager.onEvent((Event) object);
			}
			else if (object instanceof PlayerEvent) {
				System.out.println("[NEW PACKET]\nMatchServer server.listen: " + object);
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

		managementClient.send(new MatchStatusEvent(Lobby.LobbyStatus.In_Lobby, matchIp, matchPort));
		
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
