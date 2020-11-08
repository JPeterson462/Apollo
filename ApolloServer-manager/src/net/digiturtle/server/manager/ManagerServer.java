package net.digiturtle.server.manager;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Lobby;
import net.digiturtle.apollo.User;
import net.digiturtle.apollo.match.Arsenal;
import net.digiturtle.apollo.match.Arsenal.Powerup;
import net.digiturtle.apollo.match.Arsenal.PowerupStatus;
import net.digiturtle.apollo.match.event.BatchArsenalQuery;
import net.digiturtle.apollo.match.event.MatchResultEvent;
import net.digiturtle.apollo.match.event.MatchStatusEvent;
import net.digiturtle.apollo.match.event.UserConnectedEvent;
import net.digiturtle.apollo.match.event.UserJoinEvent;
import net.digiturtle.apollo.match.event.UserLobbiedEvent;
import net.digiturtle.apollo.match.event.UserLobbyEvent;
import net.digiturtle.apollo.match.event.UserLobbyQuery;
import net.digiturtle.apollo.match.event.UserNotLobbiedEvent;
import net.digiturtle.apollo.match.event.UserUpgradeEvent;
import net.digiturtle.apollo.match.event.UserUpgradeResponse;
import net.digiturtle.apollo.networking.TcpServer;

public class ManagerServer {
	
	// Player connect workflow
	
	//: ManagerServer
	
	// LoginScreen:
	// UserJoinEvent(ProductKey) -> User
	
	// LobbyScreen:
	// UserUpgradeEvent(Powerup, User) -> _
	// UserLobbyEvent(LobbyId, User) -> _
	
	//: MatchServer
	
	// MatchLobbyScreen:
	// MatchConnectEvent(User) -> _
	//+ MatchOverEvent(--) -> _ 
	
	private static DataContext ctx;
	
	private static MatchLobby[] lobbies;
	
	private static MatchLobby findMatchLobbyFromIp(String ip, int port) {
		for (int i = 0; i < lobbies.length; i++) {
			MatchLobby lobby = lobbies[i];
			if (lobby.getIP().equals(ip) && lobby.getPort() == port) {
				return lobby;
			}
		}
		System.out.println("Lobby " + ip + ":" + port + " not found");
		return null;
	}
	
	public static void main (String[] args) throws SQLException, InterruptedException {
		// ABCD is the test product key
		ctx = new DataContext("test1.db");
	//	ctx.setupTables();
		
		lobbies = new MatchLobby[] {
			new MatchLobby("127.0.0.1", 4560, 2, 1),
			
			new MatchLobby("", -1, 2, 2).to(Lobby.LobbyStatus.Resetting),//Testing for other statuses
		};
		
		// TODO spin up match servers
		
		TcpServer server = new TcpServer(4720);
		server.listen((event, ip) -> {
			try {
				if (event instanceof MatchStatusEvent) {
					MatchStatusEvent matchStatus = (MatchStatusEvent) event;
					MatchLobby lobby = findMatchLobbyFromIp(matchStatus.ip, matchStatus.port);
					lobby.setStatus(matchStatus.status);
					if (matchStatus.status.equals(Lobby.LobbyStatus.Resetting)) {
						lobby.clear();
						// TODO
						// Stop the Match server JAR
						// Start the Match server JAR
						// Status = In_Lobby
					}
				}
				if (event instanceof UserJoinEvent) {
					User user = ((UserJoinEvent) event).getRequestedUser();
					User existing = ctx.findUser(user.getProductKey());
					// TODO verify product key
					
					server.send(new UserConnectedEvent(existing), ip);
				}
				if (event instanceof UserUpgradeEvent) {
					UserUpgradeEvent userUpgradeEvent = (UserUpgradeEvent) event;
					User user = userUpgradeEvent.getUser();
					int cost;
					switch (userUpgradeEvent.getPowerup()) {
					case ApolloSettings.SPEED_POWERUP:
						cost = ApolloSettings.POWERUP_COSTS[userUpgradeEvent.getPowerup()][user.getSpeedPowerup()-1];
						if (cost <= user.getCoins() && user.getSpeedPowerup() < ApolloSettings.SPEED_POWERUP_COUNT) {
							user.setSpeedPowerup(user.getSpeedPowerup() + 1);
							user.setCoins(user.getCoins() - cost);
							server.send(new UserUpgradeResponse(true, userUpgradeEvent.getPowerup()), ip);
						} else {
							server.send(new UserUpgradeResponse(false, userUpgradeEvent.getPowerup()), ip);
						}
						break;
					case ApolloSettings.DAMAGE_POWERUP:
						cost = ApolloSettings.POWERUP_COSTS[userUpgradeEvent.getPowerup()][user.getDamagePowerup()-1];
						if (cost <= user.getCoins() && user.getDamagePowerup() < ApolloSettings.DAMAGE_POWERUP_COUNT) {
							user.setDamagePowerup(user.getDamagePowerup() + 1);
							user.setCoins(user.getCoins() - cost);
							server.send(new UserUpgradeResponse(true, userUpgradeEvent.getPowerup()), ip);
						} else {
							server.send(new UserUpgradeResponse(false, userUpgradeEvent.getPowerup()), ip);
						}
						break;
					case ApolloSettings.RESILIENCE_POWERUP:
						cost = ApolloSettings.POWERUP_COSTS[userUpgradeEvent.getPowerup()][user.getResiliencePowerup()-1];
						if (cost <= user.getCoins() && user.getResiliencePowerup() < ApolloSettings.RESILIENCE_POWERUP_COUNT) {
							user.setResiliencePowerup(user.getResiliencePowerup() + 1);
							user.setCoins(user.getCoins() - cost);
							server.send(new UserUpgradeResponse(true, userUpgradeEvent.getPowerup()), ip);
						} else {
							server.send(new UserUpgradeResponse(false, userUpgradeEvent.getPowerup()), ip);
						}
						break;
					case ApolloSettings.EXPLOSIVE_POWERUP:
						cost = ApolloSettings.POWERUP_COSTS[userUpgradeEvent.getPowerup()][user.getExplosivesPowerup()-1];
						if (cost <= user.getCoins() && user.getExplosivesPowerup() < ApolloSettings.EXPLOSIVE_POWERUP_COUNT) {
							user.setExplosivesPowerup(user.getExplosivesPowerup() + 1);
							user.setCoins(user.getCoins() - cost);
							server.send(new UserUpgradeResponse(true, userUpgradeEvent.getPowerup()), ip);
						} else {
							server.send(new UserUpgradeResponse(false, userUpgradeEvent.getPowerup()), ip);
						}
						break;
					}
					ctx.updateUser(user);
				}
				if (event instanceof MatchResultEvent) {
					MatchResultEvent matchResult = (MatchResultEvent) event;
					for (Map.Entry<UUID, Integer> pointCount : matchResult.getPoints().entrySet()) {
						User user = ctx.getUser(pointCount.getKey());
						System.out.println(pointCount.getKey() + " " + user);
						user.setCoins(user.getCoins() + pointCount.getValue()/25);
						ctx.updateUser(user);
					}
				}
				if (event instanceof UserLobbyEvent) {
					UserLobbyEvent userLobby = (UserLobbyEvent) event;
					MatchLobby lobby = lobbies[userLobby.getLobby()-1];
					if (lobby.getStatus().equals(Lobby.LobbyStatus.In_Lobby)) {
						lobby.getConnections().put(userLobby.getUser().getId(), ip);
						server.send(new UserLobbiedEvent(lobby.getIP(), lobby.getPort()), ip);
					} else {
						server.send(new UserNotLobbiedEvent(), ip);
					}
					// MatchServer:MatchStartEvent = Active, MatchServer:MatchResultEvent = Resetting, JAR spun up = In_Lobby
				}
				if (event instanceof UserLobbyQuery.Request) {
					UserLobbyQuery.Response response = new UserLobbyQuery.Response();
					response.lobbies = new UserLobbyQuery.LobbyResult[lobbies.length];
					for (int i = 0; i < lobbies.length; i++) {
						UserLobbyQuery.LobbyResult lobbyResult = new UserLobbyQuery.LobbyResult();
						lobbyResult.lobbyStatus = lobbies[i].getStatus();
						lobbyResult.playersConnected = lobbies[i].getConnections().size();
						lobbyResult.playersPerTeam = lobbies[i].getPlayers();
						lobbyResult.teams = lobbies[i].getTeams();
						lobbyResult.worldName = "Alpha Planet";
						response.lobbies[i] = lobbyResult;
					}
					System.out.println("Lobbies: " + Arrays.toString(response.lobbies));
					server.send(response, ip);
				}
				if (event instanceof BatchArsenalQuery.Request) {
					HashMap<UUID, Arsenal> result = new HashMap<>();
					for (UUID player : ((BatchArsenalQuery.Request)event).ids) {
						User user = ctx.getUser(player);
						Arsenal arsenal = new Arsenal();
						arsenal.getStatuses().put(Powerup.DAMAGE, new PowerupStatus(user.getDamagePowerup(), Powerup.DAMAGE));
						arsenal.getStatuses().put(Powerup.SPEED, new PowerupStatus(user.getSpeedPowerup(), Powerup.SPEED));
						arsenal.getStatuses().put(Powerup.RESILIENCE, new PowerupStatus(user.getResiliencePowerup(), Powerup.RESILIENCE));
						arsenal.getStatuses().put(Powerup.EXPLOSIVES, new PowerupStatus(user.getExplosivesPowerup(), Powerup.EXPLOSIVES));
						result.put(player, arsenal);
					}
					BatchArsenalQuery.Response response = new BatchArsenalQuery.Response();
					response.arsenals = result;
					server.send(response, ip);
				}
			} catch (SQLException ex) {
				
			}
		});
		server.connect();
	}
	
}
