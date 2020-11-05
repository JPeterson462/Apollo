package net.digiturtle.server.manager;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import net.digiturtle.apollo.Lobby.LobbyStatus;

public class MatchLobby {
	
	private HashMap<UUID, InetSocketAddress> connections;
	private String ip;
	private int port, teams, players;
	private LobbyStatus status;
	
	public MatchLobby (String ip, int port, int teams, int players) {
		connections = new HashMap<>();
		this.ip = ip;
		this.port = port;
		this.teams = teams;
		this.players = players;
		status = LobbyStatus.In_Lobby;
	}

	public HashMap<UUID, InetSocketAddress> getConnections () {
		return connections;
	}

	public String getIP () {
		return ip;
	}

	public void setIP (String ip) {
		this.ip = ip;
	}

	public int getPort () {
		return port;
	}

	public void setPort (int port) {
		this.port = port;
	}

	public int getTeams () {
		return teams;
	}

	public void setTeams (int teams) {
		this.teams = teams;
	}

	public int getPlayers () {
		return players;
	}

	public void setPlayers (int players) {
		this.players = players;
	}

	public LobbyStatus getStatus () {
		return status;
	}

	public void setStatus (LobbyStatus status) {
		this.status = status;
	}

}
