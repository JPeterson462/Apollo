package net.digiturtle.apollo.match;

import java.util.ArrayList;

import net.digiturtle.apollo.Rectangle;
import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.definitions.TeamDefinition;

public class Team {
	
	// Definition
	private Rectangle respawnPoint;
	
	// State
	private Backpack bank;
	
	private ArrayList<Player> players;
	
	public Team () {
		bank = new Backpack();
		players = new ArrayList<>();
	}
	
	public Team (TeamDefinition definition) {
		bank = new Backpack();
		players = new ArrayList<>();
		respawnPoint = new Rectangle(definition.respawnRegion[0], definition.respawnRegion[1], definition.respawnRegion[2], definition.respawnRegion[3]);
	}
	
	public Vector2 getRespawnPoint () {
		return new Vector2(respawnPoint.x + respawnPoint.width * 0.5f, respawnPoint.y + respawnPoint.height * 0.5f);
	}
	
	public void setRespawnPoint (Vector2 respawnPoint) {
		this.respawnPoint = new Rectangle(respawnPoint.x - 2.5f, respawnPoint.y - 2.5f, 5, 5);
	}
	
	public Backpack getBank () {
		return bank;
	}
	
	public ArrayList<Player> getPlayers () {
		return players;
	}
	
	public boolean containsPoint (Vector2 point) {
		return respawnPoint.contains(point);
	}
	
	public void respawn (Player player) {
		if (!players.contains(player)) {
			return; // Not on this team, shouldn't happen
		}
		player.getBackpack().reset();
		player.setPosition(getRespawnPoint());
	}
	

}
