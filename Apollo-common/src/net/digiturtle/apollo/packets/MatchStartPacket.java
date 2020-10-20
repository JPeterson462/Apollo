package net.digiturtle.apollo.packets;

import net.digiturtle.apollo.definitions.MatchDefinition;

public class MatchStartPacket {
	
	public PlayerStatePacket[] playerStates;
	
	@SuppressWarnings("exports")
	public MatchDefinition matchDefinition;

}
