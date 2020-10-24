package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.match.Player;

public class PlayerStateChangeEvent extends Event {
	
	private Player player;
	
	private Player.State state;
	
	private boolean popState;
	
	private int orientation;
	
	public PlayerStateChangeEvent () {
		
	}
	
	public PlayerStateChangeEvent (Player player, Player.State state, boolean popState, int orientation) {
		this.player = player;
		this.state = state;
		this.popState = popState;
		this.orientation = orientation;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player.State getState() {
		return state;
	}

	public void setState(Player.State state) {
		this.state = state;
	}

	public boolean isPopState() {
		return popState;
	}

	public void setPopState(boolean popState) {
		this.popState = popState;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

}
