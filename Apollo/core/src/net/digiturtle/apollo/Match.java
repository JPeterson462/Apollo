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

import net.digiturtle.apollo.graphics.TintEffect;

public class Match {
	
	private TiledMap tiledMap;
	private World world;
	private HashMap<UUID, Player> players;
	private ArrayList<Bullet> bullets;
	private ArrayList<ResourceRegion> resourceRegions;
	private Vector2[] respawns;
	
	public Match() {
        tiledMap = new TmxMapLoader().load("sample.tmx");
        world = new World(new Vector2(0, 0), true);
        //float tileSize = tiledMap.getProperties().get("tilewidth", Integer.class); 
        players = new HashMap<>();
        bullets = new ArrayList<Bullet>();
        resourceRegions = new ArrayList<>();
        
        //FIXME
        respawns = new Vector2[] {
        	new Vector2(256, 0), new Vector2(0, -256)
        };
        ResourceRegion hotspot1 = new ResourceRegion(Resource.COAL);
        hotspot1.setPosition(new Vector2(128, -128));//FIXME this is rendering at an unexpected location
        hotspot1.setSize(new Vector2(64, 64));
        hotspot1.setCapacity(1000);
        hotspot1.setCollectionRate(0.01f);
        hotspot1.setRegenerationRate(0.02f);
        hotspot1.setQuantity(1000);
        resourceRegions.add(hotspot1);
	}
	
	public void update (float dt) {
		for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
			if (player.getValue().getBody() == null) {
				player.getValue().update(dt);
			}
		}
		for (int i = bullets.size() - 1; i >= 0; i--) {
			for (java.util.Map.Entry<UUID, Player> player : players.entrySet()) {
				if (!bullets.get(i).getShooter().equals(player.getKey())) {
					if (Intersector.intersectSegmentCircle(bullets.get(i).getPosition(), 
							new Vector2(bullets.get(i).getPosition()).add(bullets.get(i).getVelocity()), player.getValue().getPosition(), ApolloSettings.CHARACTER_SIZE/2)) {
						processCollision(bullets.get(i), player.getValue());
						break;
					}
				}
			}
		}
		bullets.clear();
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
	}
	
	public void processCollision (Object collider, Object impact) {
		if (collider instanceof Bullet && impact instanceof Player) {
			System.out.println(impact + " was shot!");
			Player player = (Player)impact;
			player.setHealth(player.getHealth() - 20);
			if (player.getHealth() <= 0) {
				// Respawn
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
	}
	
	public ResourceRegion getResourceRegion (Player player) {
		Circle circle = new Circle();
		circle.set(player.getPosition(), ApolloSettings.CHARACTER_SIZE/2);
		for (ResourceRegion resourceRegion : resourceRegions) {
			//FIXME should probably not iterate through EVERY spot
			if (resourceRegion.getBounds().contains(circle)) {
				return resourceRegion;
			}
		}
		return null;
	}
	
	public ArrayList<ResourceRegion> getResourceRegions () {
		return resourceRegions;
	}
	
	public ArrayList<Bullet> getBullets () {
		return bullets;
	}

	public Bullet addBullet (Vector2 position, Vector2 velocity, UUID shooter) {
		System.out.println("Someone fired a bullet.");
		Bullet bullet = new Bullet(position, velocity, shooter);
		bullets.add(bullet);
		return bullet;
	}
	
	public Bullet addBullet (Vector2 position, Vector2 velocity) {
		System.out.println("Firing bullet.");
		Bullet bullet = new Bullet(position, velocity, Apollo.userId);
		bullets.add(bullet);
		return bullet;
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
