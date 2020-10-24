package net.digiturtle.apollo.match.event;

import java.util.UUID;

public class PlayerDamageEvent extends Event {
	
	public enum DamageType {
		BULLET,
		EXPLOSIVE
	}
	
	private UUID player;
	
	private DamageType damageType;
	
	public PlayerDamageEvent () {
		
	}
	
	public PlayerDamageEvent (UUID player, DamageType damageType) {
		this.player = player;
		this.damageType = damageType;
	}

	public UUID getPlayer () {
		return player;
	}

	public void setPlayer (UUID player) {
		this.player = player;
	}

	public DamageType getDamageType () {
		return damageType;
	}

	public void setDamageType (DamageType damageType) {
		this.damageType = damageType;
	}

}
