package net.digiturtle.apollo.match.event;

import java.util.UUID;

public class PlayerDamageEvent extends PlayerEvent implements ILocalEvent {
	
	public enum DamageType {
		BULLET,
		EXPLOSIVE
	}
	
	private DamageType damageType;
	
	public PlayerDamageEvent () {
		
	}
	
	public PlayerDamageEvent (UUID player, DamageType damageType) {
		super(player);
		this.damageType = damageType;
	}

	public DamageType getDamageType () {
		return damageType;
	}

	public void setDamageType (DamageType damageType) {
		this.damageType = damageType;
	}

}
