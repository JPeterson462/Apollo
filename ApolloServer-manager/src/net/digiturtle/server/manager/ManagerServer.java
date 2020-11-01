package net.digiturtle.server.manager;

public class ManagerServer {
	
	// Player connect workflow
	
	// 1. Client starts up, sends UserJoinEvent to ManagerServer
	// 2. Client navigates to lobby/upgrade screen
	// 3. Client:
		// A. Buys an upgrade sending a UserUpgradeEvent to ManagerServer
			// Check the user balance, perform the transaction, and send a UserUpgradeResponseEvent to the player
		// B. Joins an INACTIVE match, sending a UserMatchSelectEvent to ManagerServer, who responds with an IP/port

	public static void main (String[] args) {
		
	}
	
}
