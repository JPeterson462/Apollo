package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.definitions.MatchDefinition;
import net.digiturtle.apollo.match.Player;

public class MatchStartEvent extends MatchEvent {
	
	private MatchDefinition matchDefinition;
	
	private Player[] players;
	
	public MatchStartEvent () {
		
	}
	
	public MatchStartEvent (MatchDefinition matchDefinition, Player[] players) {
		this.matchDefinition = matchDefinition;
		this.players = players;
	}
	
	public MatchDefinition getMatchDefinition () {
		return matchDefinition;
	}
	
	public void setMatchDefinition (MatchDefinition matchDefinition) {
		this.matchDefinition = matchDefinition;
	}
	
	public Player[] getPlayers () {
		return players;
	}
	
	public void setPlayers (Player[] players) {
		this.players = players;
	}

}
