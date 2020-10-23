package net.digiturtle.apollo.match.event;

import net.digiturtle.apollo.match.Player;

public class PlayerDamageEvent extends Event {
	
	public enum DamageType {
		BULLET,
		EXPLOSIVE
	}
	
	private Player player;
	
	private DamageType damageType;
	
	public PlayerDamageEvent () {
		
	}
	
	public PlayerDamageEvent (Player player, DamageType damageType) {
		this.player = player;
		this.damageType = damageType;
	}

	public Player getPlayer () {
		return player;
	}

	public void setPlayer (Player player) {
		this.player = player;
	}

	public DamageType getDamageType () {
		return damageType;
	}

	public void setDamageType (DamageType damageType) {
		this.damageType = damageType;
	}

}
