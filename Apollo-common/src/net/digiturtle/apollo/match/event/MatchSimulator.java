package net.digiturtle.apollo.match.event;

import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.match.Backpack;
import net.digiturtle.apollo.match.Bullet;
import net.digiturtle.apollo.match.DroppedBackpack;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.VisualFXEngine;

public class MatchSimulator implements IEventListener {
	
	private Match match;
	private VisualFXEngine fxEngine;
	
	public MatchSimulator (Match match, VisualFXEngine fxEngine) {
		this.match = match;
		this.fxEngine = fxEngine;
	}

	@Override
	public void onEvent (Event event) {
		if (event instanceof PlayerDamageEvent) {
			Player player = ((PlayerDamageEvent) event).getPlayer();
			if (((PlayerDamageEvent) event).getDamageType().equals(PlayerDamageEvent.DamageType.BULLET)) {
				player.setHealth(player.getHealth() - ApolloSettings.BULLET_DAMAGE);
				checkForDeath(player);
			}
			if (((PlayerDamageEvent) event).getDamageType().equals(PlayerDamageEvent.DamageType.EXPLOSIVE)) {
				int damage = 60;
				player.setHealth(player.getHealth() - damage);
				checkForDeath(player);
			}
		}
		if (event instanceof PlayerShootEvent) {
			PlayerShootEvent playerShootEvent = (PlayerShootEvent) event;
			Player player = ((PlayerShootEvent) event).getPlayer();
			match.getBullets().add(new Bullet(playerShootEvent.getPosition(), playerShootEvent.getVelocity(), player.getId()));
			onMuzzleFlash(player);
		}
	}

	private void checkForDeath (Player player) {
		if (player.getHealth() <= 0) {
			// Respawn
			Backpack backpack = player.getBackpack();
			player.setBackpack(new Backpack());
			DroppedBackpack droppedBackpack = new DroppedBackpack();
			droppedBackpack.setBackpack(backpack);
			droppedBackpack.setPosition(new Vector2(player.getPosition()));
			match.getDroppedBackpacks().add(droppedBackpack);
			match.respawnPlayer(player);
			player.setHealth(ApolloSettings.PLAYER_HEALTH);
		} else {
			fxEngine.addTintedDamage(player);
		}
	}

	private void onMuzzleFlash (Player player) {
		fxEngine.addMuzzleFlash(player);
	}
	
}
