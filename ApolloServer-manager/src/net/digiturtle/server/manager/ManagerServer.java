package net.digiturtle.server.manager;

import java.sql.SQLException;

import net.digiturtle.apollo.User;

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
	
	public static void main (String[] args) throws SQLException {
		DataContext ctx = new DataContext("test1.db");
	//	ctx.setupTables();
		User user = ctx.findUser("ABCD");
		System.out.println(user);
		user.setCoins(user.getCoins()+10000);
		user.setExplosivesPowerup(2);
		ctx.updateUser(user);
		user = ctx.findUser("ABCD");
		System.out.println(user);
	}
	
}
