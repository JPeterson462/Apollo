package net.digiturtle.apollo;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class Team {
	
	// Definition
	private Vector2 respawnPoint; //FIXME should probably be a region
	
	// State
	private Backpack bank;
	
	private ArrayList<Player> players;
	
	public Team () {
		bank = new Backpack();
		players = new ArrayList<>();
	}
	
	public Vector2 getRespawnPoint () {
		return respawnPoint;
	}
	
	public void setRespawnPoint (Vector2 respawnPoint) {
		this.respawnPoint = respawnPoint;
	}
	
	public Backpack getBank () {
		return bank;
	}
	
	public ArrayList<Player> getPlayers () {
		return players;
	}
	
	public void respawn (Player player) {
		if (!players.contains(player)) {
			return; // Not on this team, shouldn't happen
		}
		player.getBackpack().reset();
		player.setPosition(getRespawnPoint());
	}
	

}
