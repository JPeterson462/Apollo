package net.digiturtle.apollo;

import java.util.UUID;

public class User {
	
	private UUID id;
	private String productKey;

	private int speedPowerup, damagePowerup, resiliencePowerup, explosivesPowerup, coins;
	
	public String toString () {
		return "User[{" + id + "}, " + coins + ", " + productKey + "][" + speedPowerup + ", " + damagePowerup + ", " + resiliencePowerup + ", " + explosivesPowerup + "]";
	}

	public UUID getId () {
		return id;
	}

	public void setId (UUID id) {
		this.id = id;
	}

	public String getProductKey () {
		return productKey;
	}

	public void setProductKey (String productKey) {
		this.productKey = productKey;
	}

	public int getSpeedPowerup () {
		return speedPowerup;
	}

	public void setSpeedPowerup (int speedPowerup) {
		this.speedPowerup = speedPowerup;
	}

	public int getDamagePowerup () {
		return damagePowerup;
	}

	public void setDamagePowerup (int damagePowerup) {
		this.damagePowerup = damagePowerup;
	}

	public int getResiliencePowerup () {
		return resiliencePowerup;
	}

	public void setResiliencePowerup (int resiliencePowerup) {
		this.resiliencePowerup = resiliencePowerup;
	}

	public int getExplosivesPowerup () {
		return explosivesPowerup;
	}

	public void setExplosivesPowerup (int explosivesPowerup) {
		this.explosivesPowerup = explosivesPowerup;
	}

	public int getCoins () {
		return coins;
	}

	public void setCoins (int coins) {
		this.coins = coins;
	}
	
}
