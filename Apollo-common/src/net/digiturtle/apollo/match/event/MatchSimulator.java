package net.digiturtle.apollo.match.event;

import java.util.ArrayList;
import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Circle;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.definitions.TeamDefinition;
import net.digiturtle.apollo.match.Backpack;
import net.digiturtle.apollo.match.Bullet;
import net.digiturtle.apollo.match.DroppedBackpack;
import net.digiturtle.apollo.match.Explosion;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
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
				fxEngine.onExplosion();
			}
		}
		if (match.getWorld() != null) {
			match.getWorld().step(dt, 8, 3);
		}
		for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
			Team team = match.getTeams()[player.getValue().getTeam()];
			if (team.containsPoint(player.getValue().getPosition())) {
				if (!player.getValue().getBackpack().isEmpty()) {
					team.getBank().deposit(player.getValue().getBackpack());
					player.getValue().setBackpack(new Backpack());
				}
			}
			if (player.getValue().getVisualFX() != null) {
				player.getValue().getVisualFX().update(dt);
			}
		}
		for (ResourceRegion resourceRegion : match.getResourceRegions()) {
			resourceRegion.update(dt);
		}
		for (DroppedBackpack droppedBackpack : match.getDroppedBackpacks()) {
			droppedBackpack.update(dt);
		}
		for (java.util.Map.Entry<UUID, Player> player : match.getPlayersMap().entrySet()) {
			if (player.getValue().getState().equals(Player.State.COLLECTING)) {
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
		
		if (match.getTimeLeft() <= 0) {
			int[] points = new int[TeamDefinition.COLOR_COUNT];
			for (int i = 0; i < points.length && i < match.getTeams().length; i++) {
				points[i] = ApolloSettings.getResourceValue(match.getTeams()[i].getBank().getContents());
			}
			match.onEvent(new MatchOverEvent(points));
		}
	}

	public void processCollision (Object collider, Object impact) {
		if (collider instanceof Bullet && impact instanceof Player) {
			match.onEvent(new PlayerDamageEvent(((Player)impact).getId(), ((Bullet) collider).getShooter(), PlayerDamageEvent.DamageType.BULLET));
		}
		if (collider instanceof Player && impact instanceof ResourceRegion) {
			//System.out.println(impact + " was entered by " + ((Player)collider).getId());
		}
		if (collider instanceof Player && impact instanceof DroppedBackpack) {
			((Player) collider).getBackpack().deposit(((DroppedBackpack) impact).getBackpack());
		}
		if (collider instanceof Explosion && impact instanceof Player) {
			match.onEvent(new PlayerDamageEvent(((Player)impact).getId(), ((Explosion) collider).getCause(), PlayerDamageEvent.DamageType.EXPLOSIVE));
		}
	}
	
	@Override
	public void onEvent (Event event) {
		System.out.println("(" + Thread.currentThread().getId() + ") MatchSimulator onEvent: " + event);
		/*if (event instanceof MatchConnectEvent) {
			MatchConnectEvent matchConnectEvent = (MatchConnectEvent) event;
			Player player = new Player(matchConnectEvent.getUniqueIdentifier(), null, null);
			player.setState(Player.State.STANDING);
			player.setTeam(0);//
			match.addPlayer(player, null);
			System.out.println("NOW -- " + match.getPlayersMap());
		}*/
		System.out.println(match + " " + match.getPlayersMap());
		if (event instanceof PlayerDamageEvent) {
			PlayerDamageEvent playerDamageEvent = (PlayerDamageEvent) event;
			Player player = match.getPlayer(((PlayerDamageEvent) event).getPlayer());
			if (((PlayerDamageEvent) event).getDamageType().equals(PlayerDamageEvent.DamageType.BULLET)) {
				float damageFactor = 1, resilienceFactor = 1;
				if (match.getPlayer(playerDamageEvent.getCause()).getPowerupTimeLeft(Powerup.DAMAGE) > 0) {
					damageFactor = Powerup.DAMAGE.boosts[match.getPlayer(playerDamageEvent.getCause()).getArsenal().getStatuses().get(Powerup.DAMAGE).getLevel()];
				}
				if (player.getPowerupTimeLeft(Powerup.RESILIENCE) > 0) {
					damageFactor = Powerup.RESILIENCE.boosts[player.getArsenal().getStatuses().get(Powerup.RESILIENCE).getLevel()];
				}
				player.setHealth((int) (player.getHealth() - ApolloSettings.BULLET_DAMAGE * damageFactor / resilienceFactor));
				checkForDeath(player);
			}
			if (((PlayerDamageEvent) event).getDamageType().equals(PlayerDamageEvent.DamageType.EXPLOSIVE)) {
				float damageFactor = 1, resilienceFactor = 1;
				if (match.getPlayer(playerDamageEvent.getCause()).getPowerupTimeLeft(Powerup.DAMAGE) > 0) {
					damageFactor = Powerup.DAMAGE.boosts[match.getPlayer(playerDamageEvent.getCause()).getArsenal().getStatuses().get(Powerup.DAMAGE).getLevel()];
				}
				if (player.getPowerupTimeLeft(Powerup.RESILIENCE) > 0) {
					damageFactor = Powerup.RESILIENCE.boosts[player.getArsenal().getStatuses().get(Powerup.RESILIENCE).getLevel()];
				}
				int damage = 60;
				player.setHealth((int) (player.getHealth() - damage * damageFactor / resilienceFactor));
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
		if (event instanceof PlayerPowerupEvent) {
			PlayerPowerupEvent playerPowerupEvent = (PlayerPowerupEvent) event;
			Player player = match.getPlayer(playerPowerupEvent.getPlayer());
			player.engagePowerup(playerPowerupEvent.getPowerup());
			player.getArsenal().getStatuses().put(playerPowerupEvent.getPowerup(), playerPowerupEvent.getPowerupStatus().cloneAndUse());
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
			player.getArsenal().tryReset();
			player.clearPowerups();
		} else {
			fxEngine.addTintedDamage(player);
		}
	}

	private void onMuzzleFlash (Player player) {
		fxEngine.addMuzzleFlash(player);
	}
	
}
