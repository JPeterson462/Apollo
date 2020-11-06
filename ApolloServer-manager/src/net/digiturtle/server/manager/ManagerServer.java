package net.digiturtle.server.manager;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.User;
import net.digiturtle.apollo.match.event.MatchResultEvent;
import net.digiturtle.apollo.match.event.UserConnectedEvent;
import net.digiturtle.apollo.match.event.UserJoinEvent;
import net.digiturtle.apollo.match.event.UserLobbiedEvent;
import net.digiturtle.apollo.match.event.UserLobbyEvent;
import net.digiturtle.apollo.match.event.UserLobbyQuery;
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
	
	public static void main (String[] args) throws SQLException, InterruptedException {
		// ABCD is the test product key
		ctx = new DataContext("test1.db");
	//	ctx.setupTables();
		
		lobbies = new MatchLobby[] {
			new MatchLobby("localhost", 4560, 2, 1)
		};
		
		TcpServer server = new TcpServer(4720);
		server.listen((event, ip) -> {
			try {
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
						user.setCoins(user.getCoins() + pointCount.getValue()/25);
						ctx.updateUser(user);
					}
				}
				if (event instanceof UserLobbyEvent) {
					UserLobbyEvent userLobby = (UserLobbyEvent) event;
					MatchLobby lobby = lobbies[userLobby.getLobby()-1];
					lobby.getConnections().put(userLobby.getUser().getId(), ip);
					server.send(new UserLobbiedEvent(lobby.getIP(), lobby.getPort()), ip);
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
						response.lobbies[i] = lobbyResult;
					}
					server.send(response, ip);
				}
			} catch (SQLException ex) {
				
			}
		});
		server.connect();
	}
	
}
