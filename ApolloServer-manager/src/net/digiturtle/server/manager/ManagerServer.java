package net.digiturtle.server.manager;

import java.sql.SQLException;

import net.digiturtle.apollo.User;

public class ManagerServer {
	
	// Player connect workflow
	
	// 1. Client starts up, sends UserJoinEvent to ManagerServer
	// 2. Client navigates to lobby/upgrade screen
	// 3. Client:
		// A. Buys an upgrade sending a UserUpgradeEvent to ManagerServer
			// Check the user balance, perform the transaction, and send a UserUpgradeResponseEvent to the player
		// B. Joins an INACTIVE match, sending a UserMatchSelectEvent to ManagerServer, who responds with an IP/port

	public static void main (String[] args) throws SQLException {
		DataContext ctx = new DataContext("test1.db");
	//	ctx.setupTables();
		User user = ctx.findUser("ABCD");
		System.out.println(user);
	}
	
}
