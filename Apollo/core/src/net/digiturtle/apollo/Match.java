package net.digiturtle.apollo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import net.digiturtle.apollo.graphics.TimedEffect;
import net.digiturtle.apollo.graphics.TimedTextureEffect;
import net.digiturtle.apollo.graphics.TintEffect;

public class Match {
	
	private TiledMap tiledMap;
	private World world;
	private HashMap<UUID, Player> players;
	private ArrayList<Bullet> bullets;
	private ArrayList<ResourceRegion> resourceRegions;
	private ArrayList<DroppedBackpack> droppedBackpacks;
	private Vector2[] respawns;
	private float lengthSeconds, totalTimeSeconds;
	
	public Match () {
        tiledMap = new TmxMapLoader().load("sample.tmx");
        world = new World(new Vector2(0, 0), true);
        //float tileSize = tiledMap.getProperties().get("tilewidth", Integer.class); 
        players = new HashMap<>();
        bullets = new ArrayList<Bullet>();
        resourceRegions = new ArrayList<>();
        droppedBackpacks = new ArrayList<>();
        
        //FIXME
        respawns = new Vector2[] {
        	new Vector2(256, 0), new Vector2(0, -256)
        };
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
			for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
				if (!bullets.get(i).getShooter().equals(player.getKey())) {
					if (Intersector.intersectSegmentCircle(bullets.get(i).getPosition(), 
							new Vector2(bullets.get(i).getPosition()).add(bullets.get(i).getVelocity()), player.getValue().getPosition(), ApolloSettings.CHARACTER_SIZE/2)) {
						processCollision(bullets.get(i), player.getValue());
						break;
					}//FIXME bullets should only collide with the first player if two are in the direction
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
		world.step(dt, 8, 3);
		for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
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
				Apollo.debugMessage = "Collecting " + getPlayer(Apollo.userId).getBackpack().getContents().get(Resource.COAL) + " vs " + resourceRegions.get(0).getQuantity();
				ResourceRegion resourceRegion = this.getResourceRegion(player.getValue());
				// FIXME if the time intervals are inexact, should I store the dt - collected*x and handle in the next frame?
				if (resourceRegion != null) {
					int collected = resourceRegion.collect(dt);
					player.getValue().getBackpack().changeQuantity(resourceRegion.getResource(), collected);
				}
			} else {
				Apollo.debugMessage = "Not Collecting" + getPlayer(Apollo.userId).getBackpack().getContents().get(Resource.COAL) + " vs " + resourceRegions.get(0).getQuantity();
				
			}
		}
	}
	
	public void processCollision (Object collider, Object impact) {
		if (collider instanceof Bullet && impact instanceof Player) {
			System.out.println(impact + " was shot!");
			Player player = (Player)impact;
			player.setHealth(player.getHealth() - ApolloSettings.BULLET_DAMAGE);
			if (player.getHealth() <= 0) {
				// Respawn
				Backpack backpack = player.getBackpack();
				player.setBackpack(new Backpack());
				DroppedBackpack droppedBackpack = new DroppedBackpack();
				droppedBackpack.setBackpack(backpack);
				droppedBackpack.setPosition(new Vector2(player.getPosition()));
				droppedBackpacks.add(droppedBackpack);
				player.setPosition(respawns[player.getTeam()]);
				player.setHealth(ApolloSettings.PLAYER_HEALTH);
			} else {
				TintEffect effect = new TintEffect(Color.RED);
				effect.setLength(.25f);
				player.getVisualFX().addEffect(effect);
				System.out.println("Adding " + effect + " to " + player.getId());
			}
		}
		if (collider instanceof Player && impact instanceof ResourceRegion) {
			//System.out.println(impact + " was entered by " + ((Player)collider).getId());
		}
		if (collider instanceof Player && impact instanceof DroppedBackpack) {
			((Player) collider).getBackpack().deposit(((DroppedBackpack) impact).getBackpack());
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
	
	public void addDroppedBackpack (Backpack backpack, Vector2 position) {
		DroppedBackpack droppedBackpack = new DroppedBackpack();
		droppedBackpack.setBackpack(backpack);
		droppedBackpack.setPosition(position);
		droppedBackpacks.add(droppedBackpack);
	}
	
	public ArrayList<Bullet> getBullets () {
		return bullets;
	}

	public Bullet addBullet (Vector2 position, Vector2 velocity, UUID shooter) {
		System.out.println("Someone fired a bullet.");
		Bullet bullet = new Bullet(position, velocity, shooter);
		bullets.add(bullet);
		onMuzzleFlash(shooter);
		return bullet;
	}
	
	public Bullet addBullet (Vector2 position, Vector2 velocity) {
		System.out.println("Firing bullet.");
		Bullet bullet = new Bullet(position, velocity, Apollo.userId);
		bullets.add(bullet);
		onMuzzleFlash(Apollo.userId);
		return bullet;
	}
	
	private void onMuzzleFlash (UUID uuid) {
		Player player = getPlayer(uuid);
		TimedEffect effect = new TimedTextureEffect("PlayerV4_MuzzleFlash.png", 
				() -> MathUtils.mapToScreen(player.getPosition(), ApolloSettings.TILE_SIZE), 
				32, 32, player.getRenderablePlayer().getFrame());
		effect.setLength(4f / 60f);
		player.getVisualFX().addEffect(effect);
	}
	
	public void addPlayer (Player player, boolean simulated) {
		if (simulated) {
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.DynamicBody;
			Body body = world.createBody(bodyDef);
		    PolygonShape polygonShape = new PolygonShape();
		    polygonShape.setAsBox(32/2, 32/2);
			Fixture fixture = body.createFixture(polygonShape, 1);
			fixture.setFriction(0.1f);
			player.setBody(body);
		}
		players.put(player.getId(), player);
	}
	
	public Collection<Player> getPlayers () {
		return players.values();
	}
	
	public Player getPlayer (UUID uuid) {
		return players.get(uuid);
	}
	
	public TiledMap getTiledMap () {
		return tiledMap;
	}

}
