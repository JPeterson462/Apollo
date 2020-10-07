package net.digiturtle.server.match;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.networking.UdpServer;
import net.digiturtle.apollo.packets.ClientConnectPacket;
import net.digiturtle.apollo.packets.MatchStartPacket;
import net.digiturtle.apollo.packets.PlayerStatePacket;

public class MatchServer {
	
	private static UdpServer server;
	private static FiberPool fiberPool;
	
	private static HashMap<UUID, PlayerStatePacket> playerStates;
	
	public static void main(String[] args) {
		int numPlayers = Integer.parseInt(args[0]);
		
		fiberPool = new FiberPool(1);
		server = new UdpServer(4560);
		playerStates = new HashMap<>();
		server.listen((object, sender) -> {
			if (object instanceof ClientConnectPacket) {
				ClientConnectPacket packet = (ClientConnectPacket)object;
				PlayerStatePacket playerState = new PlayerStatePacket();
				playerState.uuid = packet.clientId;
				playerState.x = 0;
				playerState.y = 0;
				playerStates.put(packet.clientId, playerState);
				
				if (playerStates.size() == numPlayers) {
					MatchStartPacket matchStart = new MatchStartPacket();
					matchStart.playerStates = playerStates.values().stream().collect(Collectors.toList()).toArray(len -> new PlayerStatePacket[len]);
					server.broadcast(matchStart);
				}
			}
			System.out.println(object + " from " + sender);
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
