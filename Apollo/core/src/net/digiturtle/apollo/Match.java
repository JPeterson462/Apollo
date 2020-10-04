package net.digiturtle.apollo;

import java.util.HashMap;
import java.util.UUID;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
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
	
	public Match() {
        tiledMap = new TmxMapLoader().load("sample.tmx");
        world = new World(new Vector2(0, 0), true);
        //float tileSize = tiledMap.getProperties().get("tilewidth", Integer.class); 
        players = new HashMap<>();
	}
	
	public void update(float dt) {
		world.step(dt, 8, 3);
	}
	
	public void addPlayer(Player player) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		Body body = world.createBody(bodyDef);
	    PolygonShape polygonShape = new PolygonShape();
	    polygonShape.setAsBox(32/2, 32/2);
		Fixture fixture = body.createFixture(polygonShape, 1);
		fixture.setFriction(0.1f);
		player.setBody(body);
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
	
	public Player getPlayer () {
		return players.values().iterator().next();//FIXME
	}
	
	public TiledMap getTiledMap () {
		return tiledMap;
	}

}
