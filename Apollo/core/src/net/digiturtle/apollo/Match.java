package net.digiturtle.apollo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Match {
	
	private TiledMap tiledMap;
	private World world;
	private HashMap<UUID, Player> players;
	private ArrayList<Bullet> bullets;
	
	public Match() {
        tiledMap = new TmxMapLoader().load("sample.tmx");
        world = new World(new Vector2(0, 0), true);
        //float tileSize = tiledMap.getProperties().get("tilewidth", Integer.class); 
        players = new HashMap<>();
        bullets = new ArrayList<Bullet>();
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
							new Vector2(bullets.get(i).getPosition()).add(bullets.get(i).getVelocity()), player.getValue().getPosition(), 16)) {
						System.out.println(player.getKey() + " was shot! (" + bullets.size() + ")");
						
						break;
					}
				}
			}
		}
		bullets.clear();
		world.step(dt, 8, 3);
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
	
	/*private Body createBodyFromMapObject (MapObject object, float tileSize) {
		//Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bodyDef);
        Fixture fixture = body.createFixture(getShapeFromRectangle(rectangle, tileSize), 0);
        fixture.setFriction(0.1F);
        body.setTransform(getTransformedCenterForRectangle(rectangle, tileSize), 0);
        return body;
	}
	
	private Shape getShapeFromRectangle (Rectangle rectangle, float tileSize) {
	    PolygonShape polygonShape = new PolygonShape();
	    polygonShape.setAsBox(rectangle.width*0.5F/ tileSize,rectangle.height*0.5F/ tileSize);
	    return polygonShape;
	}
	
	private Vector2 getTransformedCenterForRectangle (Rectangle rectangle, float tileSize){
	    Vector2 center = new Vector2();
	    rectangle.getCenter(center);
	    return center.scl(1/tileSize);
	}*/
	
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
