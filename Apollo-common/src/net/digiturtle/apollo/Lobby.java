package net.digiturtle.apollo;

public class Lobby {
	
	private String worldName, playerTotal, playerCount;
	private LobbyStatus status;
	
	public enum LobbyStatus {
		In_Lobby,
		Active,
		Resetting;
		
		public String text () {
			return name().replace('_', ' ');
		}
	}

	public String getWorldName () {
		return worldName;
	}

	public void setWorldName (String worldName) {
		this.worldName = worldName;
	}

	public String getPlayerTotal () {
		return playerTotal;
	}

	public void setPlayerTotal (String playerTotal) {
		this.playerTotal = playerTotal;
	}

	public String getPlayerCount () {
		return playerCount;
	}

	public void setPlayerCount (String playerCount) {
		this.playerCount = playerCount;
	}

	public LobbyStatus getStatus () {
		return status;
	}

	public void setStatus (LobbyStatus status) {
		this.status = status;
	}

}
