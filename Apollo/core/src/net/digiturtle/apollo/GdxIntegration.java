package net.digiturtle.apollo;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import net.digiturtle.apollo.match.IBody;
import net.digiturtle.apollo.match.IIntersector;
import net.digiturtle.apollo.match.ITiledMap;
import net.digiturtle.apollo.match.ITiledMapLoader;
import net.digiturtle.apollo.match.IWorld;

public class GdxIntegration {
	
	public static class GdxIntersector implements IIntersector {
		
		private com.badlogic.gdx.math.Vector2 gdxStart, gdxEnd, gdxCenter;
		
		public GdxIntersector () {
			gdxStart = new com.badlogic.gdx.math.Vector2();
			gdxEnd = new com.badlogic.gdx.math.Vector2();
			gdxCenter = new com.badlogic.gdx.math.Vector2();
		}

		@Override
		public boolean intersectSegmentCircle(Vector2 start, Vector2 end, Vector2 center, float radius) {
			gdxStart.set(start.x, start.y);
			gdxEnd.set(end.x, end.y);
			gdxCenter.set(center.x, center.y);
			return Intersector.intersectSegmentCircle(gdxStart, gdxEnd, gdxCenter, radius);
		}
		
	}
	
	public static class GdxTiledMap implements ITiledMap {
		
		public TiledMap tiledMap;
		
	}
	
	public static class GdxWorld implements IWorld {
		
		public World world;

		@Override
		public void step(float dt, int velocityIterations, int positionIterations) {
			world.step(dt, velocityIterations, positionIterations);
		}
		
	}
	
	public static class GdxTiledMapLoader implements ITiledMapLoader {

		@Override
		public ITiledMap load(String file) {
			GdxTiledMap map = new GdxTiledMap();
			map.tiledMap = new TmxMapLoader().load(file);
			return map;
		}

		@Override
		public IWorld createWorld(Vector2 gravity, boolean doSleep) {
			GdxWorld world = new GdxWorld();
			world.world = new World(new com.badlogic.gdx.math.Vector2(gravity.x, gravity.y), doSleep);
			return world;
		}
		
	}
	
	public static class GdxBody implements IBody {
		
		public Body body;
		
		private Vector2 linearVelocity, position;
		
		public GdxBody (GdxWorld world) {
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.DynamicBody;
			body = world.world.createBody(bodyDef);
		    PolygonShape polygonShape = new PolygonShape();
		    polygonShape.setAsBox(32/2, 32/2);
			Fixture fixture = body.createFixture(polygonShape, 1);
			fixture.setFriction(0.1f); 
			linearVelocity = new Vector2();
			position = new Vector2();
		}

		@Override
		public void setAngularVelocity(float velocity) {
			body.setAngularVelocity(velocity);
		}

		@Override
		public void setLinearVelocity(float vx, float vy) {
			body.setLinearVelocity(vx, vy);
		}

		@Override
		public void setLinearVelocity(Vector2 velocity) {
			body.setLinearVelocity(velocity.x, velocity.y);
		}

		@Override
		public Vector2 getLinearVelocity() {
			com.badlogic.gdx.math.Vector2 linearVelocity = body.getLinearVelocity();
			this.linearVelocity.set(linearVelocity.x, linearVelocity.y);
			return this.linearVelocity;
		}

		@Override
		public Vector2 getPosition() {
			com.badlogic.gdx.math.Vector2 position = body.getPosition();
			this.position.set(position.x, position.y);
			return this.position;
		}

		@Override
		public float getAngle() {
			return body.getAngle();
		}

		@Override
		public void setTransform(Vector2 position, float rotation) {
			body.setTransform(position.x, position.y, rotation);
		}
		
	}

}
