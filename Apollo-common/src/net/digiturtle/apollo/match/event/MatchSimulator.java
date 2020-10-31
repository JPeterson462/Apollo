package net.digiturtle.apollo.match.event;

import java.util.ArrayList;
import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Circle;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.match.Backpack;
import net.digiturtle.apollo.match.Bullet;
import net.digiturtle.apollo.match.DroppedBackpack;
import net.digiturtle.apollo.match.Explosion;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.Resource;
import net.digiturtle.apollo.match.ResourceRegion;
import net.digiturtle.apollo.match.Team;
import net.digiturtle.apollo.match.VisualFXEngine;
import net.digiturtle.apollo.match.Arsenal.Powerup;

public class MatchSimulator implements IEventListener {
	
	private Match match;
	private VisualFXEngine fxEngine;
	
	public MatchSimulator (Match match, VisualFXEngine fxEngine) {
		this.match = match;
		this.fxEngine = fxEngine;
	}
	
	public void update (float dt) {
		match.setTimeLeft(match.getTimeLeft() - dt);
		for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
			player.getValue().restoreSpeed();
			player.getValue().update(dt);
		}
		for (int i = match.getBullets().size() - 1; i >= 0; i--) {
			ArrayList<Player> playersHit = new ArrayList<>();
			for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
				if (!match.getBullets().get(i).getShooter().equals(player.getKey())) {
					if (match.getIntersector().intersectSegmentCircle(match.getBullets().get(i).getPosition(), 
							new Vector2(match.getBullets().get(i).getPosition()).add(match.getBullets().get(i).getVelocity()), player.getValue().getPosition(), ApolloSettings.CHARACTER_SIZE/8)) {//FIXME magic number
						playersHit.add(player.getValue());
					}
				}
			}
			Bullet bullet = match.getBullets().get(i);
			if (playersHit.size() > 0) {
				Player firstToBeHit = playersHit.get(0);
				float distance = MathUtils.distanceSquared(firstToBeHit.getPosition(), bullet.getPosition());
				for (int j = 1; j < playersHit.size(); j++) {
					float newDistance = MathUtils.distanceSquared(playersHit.get(j).getPosition(), bullet.getPosition());
					if (newDistance < distance) {
						firstToBeHit = playersHit.get(j);
					}
				}
				if (match.canAllowFriendlyFire() || firstToBeHit.getTeam() != match.getPlayer(bullet.getShooter()).getTeam()) {
					processCollision(bullet, firstToBeHit);
				}
			}
		}
		match.getBullets().clear();
		for (int i = match.getDroppedBackpacks().size() - 1; i >= 0; i--) {
			for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
				Circle circle = new Circle();
				circle.set(player.getValue().getPosition(), ApolloSettings.CHARACTER_SIZE/8);//FIXME magic number /4?
				if (MathUtils.overlaps(match.getDroppedBackpacks().get(i).getBounds(), circle)) {
					processCollision(player.getValue(), match.getDroppedBackpacks().get(i));
					match.getDroppedBackpacks().remove(i);
					break;
				}
			}
		}
		for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
			Circle circle = new Circle();
			circle.set(player.getValue().getPosition(), ApolloSettings.CHARACTER_SIZE/8);//FIXME magic number
		}
		for (int i = match.getExplosions().size() - 1; i >= 0; i--) {
			Explosion explosion = match.getExplosions().get(i);
			explosion.update(dt);
			if (explosion.getTime() >= explosion.getLength()+explosion.getDelay()) {
				// process the collision when removing the explosion so it only happens once
				for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
					int radius = explosion.getPower()/2;
					if (MathUtils.distanceSquared(player.getValue().getPosition(), explosion.getPosition()) < radius*radius) {
						processCollision(explosion, player.getValue());
					}
				}
				match.getExplosions().remove(i);
			}
		}
		match.getWorld().step(dt, 8, 3);
		for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
			Team team = match.getTeams()[player.getValue().getTeam()];
			if (team.containsPoint(player.getValue().getPosition())) {
				if (!player.getValue().getBackpack().isEmpty()) {
					team.getBank().deposit(player.getValue().getBackpack());
					player.getValue().setBackpack(new Backpack());
				}
			}
			player.getValue().getVisualFX().update(dt);
		}
		for (ResourceRegion resourceRegion : match.getResourceRegions()) {
			resourceRegion.update(dt);
		}
		for (DroppedBackpack droppedBackpack : match.getDroppedBackpacks()) {
			droppedBackpack.update(dt);
		}
		for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
			if (player.getValue().getState().equals(Player.State.COLLECTING)) {
				System.out.println("Collecting " + player.getValue().getBackpack().getContents().get(Resource.COAL) + " vs " + match.getResourceRegions().get(0).getQuantity());
				ResourceRegion resourceRegion = match.getResourceRegion(player.getValue());
				// FIXME if the time intervals are inexact, should I store the dt - collected*x and handle in the next frame?
				if (resourceRegion != null) {
					int collected = resourceRegion.collect(dt);
					player.getValue().getBackpack().changeQuantity(resourceRegion.getResource(), collected);
				}
			} else {
				//Apollo.debugMessage = "Not Collecting" + getPlayer(Apollo.userId).getBackpack().getContents().get(Resource.COAL) + " vs " + resourceRegions.get(0).getQuantity();
				
			}
		}
	}

	public void processCollision (Object collider, Object impact) {
		if (collider instanceof Bullet && impact instanceof Player) {
			match.onEvent(new PlayerDamageEvent(((Player)impact).getId(), PlayerDamageEvent.DamageType.BULLET));
		}
		if (collider instanceof Player && impact instanceof ResourceRegion) {
			//System.out.println(impact + " was entered by " + ((Player)collider).getId());
		}
		if (collider instanceof Player && impact instanceof DroppedBackpack) {
			((Player) collider).getBackpack().deposit(((DroppedBackpack) impact).getBackpack());
		}
		if (collider instanceof Explosion && impact instanceof Player) {
			match.onEvent(new PlayerDamageEvent(((Player)impact).getId(), PlayerDamageEvent.DamageType.EXPLOSIVE));
		}
	}
	
	@Override
	public void onEvent (Event event) {
		System.out.println(event.getClass().getName());
		if (event instanceof PlayerDamageEvent) {
			Player player = match.getPlayer(((PlayerDamageEvent) event).getPlayer());
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
			Player player = match.getPlayer(((PlayerShootEvent) event).getPlayer());
			match.getBullets().add(new Bullet(playerShootEvent.getPosition(), playerShootEvent.getVelocity(), player.getId()));
			onMuzzleFlash(player);
		}
		if (event instanceof PlayerStateChangeEvent) {
			PlayerStateChangeEvent playerStateChangeEvent = (PlayerStateChangeEvent) event;
			Player player = match.getPlayer(playerStateChangeEvent.getPlayer());
			if (!playerStateChangeEvent.getState().equals(Player.State.STANDING) ||
					(playerStateChangeEvent.getState().equals(Player.State.STANDING) && playerStateChangeEvent.getOrientation() == 0)) {
				player.setState(playerStateChangeEvent.getState());
			}
			player.changeOrientation(playerStateChangeEvent.getOrientation());
			//FIXME use popState value
		}
		if (event instanceof PlayerExplosiveEvent) {
			PlayerExplosiveEvent playerExplosiveEvent = (PlayerExplosiveEvent) event;
			match.getExplosions().add(playerExplosiveEvent.getExplosion());
			Player player = match.getPlayer(playerExplosiveEvent.getPlayer());
			player.setTemporaryState(Player.State.THROWING, Player.State.THROWING.timePerFrame * Player.State.THROWING.numFrames);
			player.getArsenal().getStatuses().get(Powerup.EXPLOSIVES).setRemaining(player.getArsenal().getStatuses().get(Powerup.EXPLOSIVES).getRemaining() - 1);
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
