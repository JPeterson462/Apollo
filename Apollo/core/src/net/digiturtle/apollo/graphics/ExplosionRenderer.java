package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.RenderPath;
import net.digiturtle.apollo.match.Explosion;

public class ExplosionRenderer {
	
	private SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	private TextureRegion[] explosion1regions;
	private Texture explosion1, explosion1projectile;//60
	
	public ExplosionRenderer (OrthographicCamera camera) {
		this.camera = camera;
	}
	
	public void create () {
		spriteBatch = new SpriteBatch();
		explosion1 = new Texture("ExplosionV1.png");
		explosion1regions = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			explosion1regions[i] = new TextureRegion(explosion1, i * 256, 0, 256, 128);
		}
		explosion1projectile = new Texture("GrenadeV1.png");
	}
	
	public void begin () {
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(camera.combined);
	}
	
	public void render (Explosion explosion) {
		net.digiturtle.apollo.Vector2 position = MathUtils.mapToScreen(explosion.getPosition(), ApolloSettings.TILE_SIZE);
		if (explosion.getPower() == ApolloSettings.EXPLOSION_POWER) {
			if (explosion.getTime() < ApolloSettings.EXPLOSIVE_THROW_DELAY) {
				// wait for the player throwing animation
			}
			else if (explosion.getTime() < explosion.getDelay()+ApolloSettings.EXPLOSIVE_THROW_DELAY) {
				RenderPath renderPath = new RenderPath(explosion.getPath());
				position = MathUtils.mapToScreen(renderPath.getPointAt(((explosion.getTime()-ApolloSettings.EXPLOSIVE_THROW_DELAY))/explosion.getDelay()), ApolloSettings.TILE_SIZE);
				position.x += ApolloSettings.TILE_SIZE/2;
				position.y += ApolloSettings.TILE_SIZE/4;
				
				position.y += ApolloSettings.CHARACTER_SIZE/2;
				
				spriteBatch.draw(explosion1projectile, position.x, position.y);
			} else {
				spriteBatch.draw(explosion1regions[(int) (8 * (explosion.getTime()-explosion.getDelay()-ApolloSettings.EXPLOSIVE_THROW_DELAY) / explosion.getLength())], position.x, position.y);
			}
		}
	}
	
	public void end () {
		spriteBatch.end();
	}

}
