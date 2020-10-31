package net.digiturtle.apollo.match.event;

import java.util.UUID;

import net.digiturtle.apollo.match.Arsenal.Powerup;
import net.digiturtle.apollo.match.Arsenal.PowerupStatus;

public class PlayerPowerupEvent extends PlayerEvent {

	private Powerup powerup;
	private PowerupStatus powerupStatus;
	
	public PlayerPowerupEvent () {
		
	}
	
	public PlayerPowerupEvent (UUID player, Powerup powerup, PowerupStatus powerupStatus) {
		super(player);
		this.powerup = powerup;
		this.powerupStatus = powerupStatus;
	}

	public Powerup getPowerup () {
		return powerup;
	}

	public void setPowerup (Powerup powerup) {
		this.powerup = powerup;
	}

	public PowerupStatus getPowerupStatus () {
		return powerupStatus;
	}

	public void setPowerupStatus (PowerupStatus powerupStatus) {
		this.powerupStatus = powerupStatus;
	}
	
}
