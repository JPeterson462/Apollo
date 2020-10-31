package net.digiturtle.apollo.match.event;

import java.util.UUID;

public class PlayerDamageEvent extends PlayerEvent implements ILocalEvent {
	
	public enum DamageType {
		BULLET,
		EXPLOSIVE
	}
	
	private DamageType damageType;
	private UUID cause;
	
	public PlayerDamageEvent () {
		
	}
	
	public PlayerDamageEvent (UUID player, UUID cause, DamageType damageType) {
		super(player);
		this.cause = cause;
		this.damageType = damageType;
	}
	
	public UUID getCause () {
		return cause;
	}
	
	public void setCause (UUID cause) {
		this.cause = cause;
	}

	public DamageType getDamageType () {
		return damageType;
	}

	public void setDamageType (DamageType damageType) {
		this.damageType = damageType;
	}

}
