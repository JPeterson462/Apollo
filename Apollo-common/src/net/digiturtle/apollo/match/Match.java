package net.digiturtle.apollo.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Circle;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.Vector2;
import net.digiturtle.apollo.definitions.MatchDefinition;
import net.digiturtle.apollo.definitions.ResourceRegionDefinition;
import net.digiturtle.apollo.match.event.IEventListener;
import net.digiturtle.apollo.match.event.MatchSimulator;
import net.digiturtle.apollo.match.event.PlayerDamageEvent;
import net.digiturtle.apollo.match.event.PlayerShootEvent;

public class Match {
	
	private IIntersector intersector;
	private ITiledMap tiledMap;
	private IWorld world;
	private HashMap<UUID, Player> players;
	private ArrayList<Bullet> bullets;
	private ArrayList<ResourceRegion> resourceRegions;
	private ArrayList<DroppedBackpack> droppedBackpacks;
	private ArrayList<Explosion> explosions;
	private float lengthSeconds, totalTimeSeconds;
	private Team[] teams;
	//private Random random;
	private boolean allowFriendlyFire;
	private IEventListener eventListener;
	
	public Match () {
		
	}
	
	public Match (ITiledMapLoader loader, IIntersector intersector, VisualFXEngine fxEngine) {
		eventListener = new MatchSimulator(this, fxEngine);
		this.intersector = intersector;
		//random = new Random();
        tiledMap = loader.load("sample.tmx");
        world = loader.createWorld(new Vector2(0, 0), true);
        //float tileSize = tiledMap.getProperties().get("tilewidth", Integer.class); 
        players = new HashMap<>();
        bullets = new ArrayList<Bullet>();
        resourceRegions = new ArrayList<>();
        droppedBackpacks = new ArrayList<>();
        allowFriendlyFire = false;
        explosions = new ArrayList<>();
        
        //FIXME
        ResourceRegion hotspot1 = new ResourceRegion(Resource.COAL);
        hotspot1.setPosition(new Vector2(128, -128));
        hotspot1.setSize(new Vector2(128, 128));//FIXME resource collection only works in the first 64x64
        hotspot1.setCapacity(1000);
        hotspot1.setCollectionRate(0.01f);
        hotspot1.setRegenerationRate(0.05f);
        hotspot1.setQuantity(1000);
        resourceRegions.add(hotspot1);
        addDroppedBackpack(new Backpack(), new Vector2(128, -256));
        lengthSeconds = totalTimeSeconds = 5*60;
	}
	
	public void load (MatchDefinition definition, ITiledMapLoader loader, IIntersector intersector, VisualFXEngine fxEngine) {
		eventListener = new MatchSimulator(this, fxEngine);
		this.intersector = intersector; 
		//random = new Random();
        tiledMap = loader.load(definition.tiledMapFile);
        world = loader.createWorld(new Vector2(0, 0), true);
        players = new HashMap<>();
        bullets = new ArrayList<Bullet>();
        resourceRegions = new ArrayList<>();
        droppedBackpacks = new ArrayList<>();
        for (ResourceRegionDefinition regionDefinition : definition.resourceRegions) {
        	resourceRegions.add(new ResourceRegion(regionDefinition));
        }
        teams = new Team[definition.teams.length];
        for (int i = 0; i < teams.length; i++) {
        	teams[i] = new Team(definition.teams[i]);
        }
        lengthSeconds = totalTimeSeconds = definition.lengthSeconds;
        allowFriendlyFire = definition.allowFriendlyFire;
        explosions = new ArrayList<>();
	}
	
	public IWorld getWorld () {
		return world;
	}
	
	public Team[] getTeams () {
		return teams;
	}
	
	public float getTotalTime () {
		return totalTimeSeconds;
	}
	
	public float getTimeLeft () {
		return Math.max(0, lengthSeconds);
	}
	
	public void update (float dt) {
		lengthSeconds -= dt;
		for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
			//if (player.getValue().getBody() == null) {
				player.getValue().update(dt);
			//}
		}
		for (int i = bullets.size() - 1; i >= 0; i--) {
			ArrayList<Player> playersHit = new ArrayList<>();
			for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
				if (!bullets.get(i).getShooter().equals(player.getKey())) {
					if (intersector.intersectSegmentCircle(bullets.get(i).getPosition(), 
							new Vector2(bullets.get(i).getPosition()).add(bullets.get(i).getVelocity()), player.getValue().getPosition(), ApolloSettings.CHARACTER_SIZE/2)) {
						//processCollision(bullets.get(i), player.getValue());
						//break;
						playersHit.add(player.getValue());
					}
				}
			}
			Bullet bullet = bullets.get(i);
			if (playersHit.size() > 0) {
				Player firstToBeHit = playersHit.get(0);
				float distance = MathUtils.distanceSquared(firstToBeHit.getPosition(), bullet.getPosition());
				for (int j = 1; j < playersHit.size(); j++) {
					float newDistance = MathUtils.distanceSquared(playersHit.get(j).getPosition(), bullet.getPosition());
					if (newDistance < distance) {
						firstToBeHit = playersHit.get(j);
					}
				}
				if (allowFriendlyFire || firstToBeHit.getTeam() != getPlayer(bullet.getShooter()).getTeam()) {
					processCollision(bullet, firstToBeHit);
				}
			}
		}
		bullets.clear();
		for (int i = droppedBackpacks.size() - 1; i >= 0; i--) {
			for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
				Circle circle = new Circle();
				circle.set(player.getValue().getPosition(), ApolloSettings.CHARACTER_SIZE/2);
				if (MathUtils.overlaps(droppedBackpacks.get(i).getBounds(), circle)) {
					processCollision(player.getValue(), droppedBackpacks.get(i));
					droppedBackpacks.remove(i);
					break;
				}
			}
		}
		for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
			Circle circle = new Circle();
			circle.set(player.getValue().getPosition(), ApolloSettings.CHARACTER_SIZE/2);
		}
		for (int i = explosions.size() - 1; i >= 0; i--) {
			Explosion explosion = explosions.get(i);
			explosion.update(dt);
			if (explosion.getTime() >= explosion.getLength()) {
				// process the collision when removing the explosion so it only happens once
				for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
					int radius = explosion.getPower()/2;
					if (MathUtils.distanceSquared(player.getValue().getPosition(), explosion.getPosition()) < radius*radius) {
						processCollision(explosion, player.getValue());
					}
				}
				explosions.remove(i);
			}
		}
		world.step(dt, 8, 3);
		for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
			Team team = teams[player.getValue().getTeam()];
			if (team.containsPoint(player.getValue().getPosition())) {
				if (!player.getValue().getBackpack().isEmpty()) {
					team.getBank().deposit(player.getValue().getBackpack());
					player.getValue().setBackpack(new Backpack());
				}
			}
			player.getValue().getVisualFX().update(dt);
		}
		for (ResourceRegion resourceRegion : resourceRegions) {
			resourceRegion.update(dt);
		}
		for (DroppedBackpack droppedBackpack : droppedBackpacks) {
			droppedBackpack.update(dt);
		}
		for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
			if (player.getValue().getState().equals(Player.State.COLLECTING)) {
				System.out.println("Collecting " + player.getValue().getBackpack().getContents().get(Resource.COAL) + " vs " + resourceRegions.get(0).getQuantity());
				ResourceRegion resourceRegion = this.getResourceRegion(player.getValue());
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
	
	public void respawnAllPlayers () {
		for (Player player : players.values()) {
			respawnPlayer(player);
		}
	}
	
	public void respawnPlayer (Player player) {
		player.setPosition(teams[player.getTeam()].getRespawnPoint());
	}
	
	public void processCollision (Object collider, Object impact) {
		if (collider instanceof Bullet && impact instanceof Player) {
			eventListener.onEvent(new PlayerDamageEvent((Player)impact, PlayerDamageEvent.DamageType.BULLET));
		}
		if (collider instanceof Player && impact instanceof ResourceRegion) {
			//System.out.println(impact + " was entered by " + ((Player)collider).getId());
		}
		if (collider instanceof Player && impact instanceof DroppedBackpack) {
			((Player) collider).getBackpack().deposit(((DroppedBackpack) impact).getBackpack());
		}
		if (collider instanceof Explosion && impact instanceof Player) {
			eventListener.onEvent(new PlayerDamageEvent((Player)impact, PlayerDamageEvent.DamageType.EXPLOSIVE));
		}
	}
	
	public ResourceRegion getResourceRegion (Player player) {
		Circle circle = new Circle();
		circle.set(player.getPosition(), ApolloSettings.CHARACTER_SIZE/2);
		for (ResourceRegion resourceRegion : resourceRegions) {
			//FIXME should probably not iterate through EVERY spot
			if (MathUtils.overlaps(resourceRegion.getBounds(), circle)) {
				return resourceRegion;
			}
		}
		return null;
	}
	
	public ArrayList<ResourceRegion> getResourceRegions () {
		return resourceRegions;
	}
	
	public ArrayList<DroppedBackpack> getDroppedBackpacks () {
		return droppedBackpacks;
	}
	
	public ArrayList<Explosion> getExplosions () {
		return explosions;
	}
	
	public void addDroppedBackpack (Backpack backpack, Vector2 position) {
		DroppedBackpack droppedBackpack = new DroppedBackpack();
		droppedBackpack.setBackpack(backpack);
		droppedBackpack.setPosition(position);
		droppedBackpacks.add(droppedBackpack);
	}
	
	public ArrayList<Bullet> getBullets () {
		return bullets;
	}

	public void addBullet (Vector2 position, Vector2 velocity, UUID shooter) {
		/*System.out.println("Someone fired a bullet.");
		Bullet bullet = new Bullet(position, velocity, shooter);
		bullets.add(bullet);
		onMuzzleFlash(shooter);
		return bullet;*/
		eventListener.onEvent(new PlayerShootEvent(getPlayer(shooter), position, velocity));
	}
	
	public void addPlayer (Player player, IBody body) {
		//if (simulated) {
			/*BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.DynamicBody;
			Body body = world.createBody(bodyDef);
		    PolygonShape polygonShape = new PolygonShape();
		    polygonShape.setAsBox(32/2, 32/2);
			Fixture fixture = body.createFixture(polygonShape, 1);
			fixture.setFriction(0.1f);*/
			player.setBody(body);
		//}
		players.put(player.getId(), player);
	}
	
	public Collection<Player> getPlayers () {
		return players.values();
	}
	
	public Player getPlayer (UUID uuid) {
		return players.get(uuid);
	}
	
	public ITiledMap getTiledMap () {
		return tiledMap;
	}

}
