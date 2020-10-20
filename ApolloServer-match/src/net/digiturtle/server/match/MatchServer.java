package net.digiturtle.server.match;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.SharedUtils;
import net.digiturtle.apollo.networking.UdpServer;
import net.digiturtle.apollo.packets.BulletPacket;
import net.digiturtle.apollo.packets.ClientConnectPacket;
import net.digiturtle.apollo.packets.MatchStartPacket;
import net.digiturtle.apollo.packets.MatchStatePacket;
import net.digiturtle.apollo.packets.PlayerStatePacket;

public class MatchServer {
	
	private static UdpServer server;
	private static FiberPool fiberPool;
	
	private static Random random;
	private static float[] teamBins;
	
	private static HashMap<UUID, PlayerStatePacket> playerStates;
	
	public static void main(String[] args) throws FileNotFoundException {
		random = new Random();
		
		teamBins = SharedUtils.generateBins(2);
		
		//System.setErr(new PrintStream(new FileOutputStream("debug.txt")));
		
		int numPlayers = Integer.parseInt(args[0]);
		
		fiberPool = new FiberPool(2);
		server = new UdpServer(4560);
		playerStates = new HashMap<>();
		server.listen((object, sender) -> {
			if (object instanceof ClientConnectPacket) {
				ClientConnectPacket packet = (ClientConnectPacket)object;
				PlayerStatePacket playerState = new PlayerStatePacket();
				playerState.uuid = packet.clientId;
				playerState.x = 0;
				playerState.y = 0;
				playerState.team = SharedUtils.assignBin(teamBins, random.nextFloat());
				playerStates.put(packet.clientId, playerState);
				
				if (playerStates.size() == numPlayers) {
					MatchStartPacket matchStart = new MatchStartPacket();
					matchStart.matchDefinition = DebugStuff.newMatchDefinition(2);
					
					//TODO FIXME more than 2 teams
					int team0 = (int) playerStates.values().stream().filter(player -> player.team == 0).count();
					int team1 = (int) playerStates.values().stream().filter(player -> player.team == 1).count();
					
					if (Math.abs(team1 - team0) > 1) {
						int shift = Math.abs(team1 - team0) / 2;
						if (team1 > team0) {
							int[] I = new int[] { 0 };
							playerStates.values().stream().filter(player -> player.team == 1).forEach(state -> {
								if (I[0] < shift) {
									state.team = 0;
									I[0]++;
								}
							});
						}
						else if (team0 > team1) {
							int[] I = new int[] { 0 };
							playerStates.values().stream().filter(player -> player.team == 0).forEach(state -> {
								if (I[0] < shift) {
									state.team = 1;
									I[0]++;
								}
							});
						}
					}
					
					matchStart.playerStates = playerStates.values().stream().collect(Collectors.toList()).toArray(len -> new PlayerStatePacket[len]);
					server.broadcast(matchStart);

					fiberPool.scheduleTask(25, () -> {
						MatchStatePacket matchState = new MatchStatePacket();
						matchState.playerStates = playerStates.values().stream().collect(Collectors.toList()).toArray(len -> new PlayerStatePacket[len]);
						//for (PlayerStatePacket playerState_ : matchState.playerStates)
						//	System.out.println("Sending " + playerState_.uuid + " <" + playerState_.x + "," + playerState_.y);
						server.broadcast(matchState);
					});
				}
			}
			if (object instanceof BulletPacket) {
				System.out.println("Received a bullet packet.");
				server.broadcast(object);
			}
			if (object instanceof PlayerStatePacket) {
				// TODO FIXME this needs security so you can't update other players
				PlayerStatePacket playerState = (PlayerStatePacket)object;
				playerStates.put(playerState.uuid, playerState);
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
