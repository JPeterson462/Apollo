package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.match.Explosion;

public class ExplosionRenderer {
	
	private SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	private TextureRegion[] explosion1regions;
	private Texture explosion1;//60
	
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
	}
	
	public void begin () {
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(camera.combined);
	}
	
	public void render (Explosion explosion) {
		net.digiturtle.apollo.Vector2 position = MathUtils.mapToScreen(explosion.getPosition(), ApolloSettings.TILE_SIZE);
		if (explosion.getPower() == 60) {
			spriteBatch.draw(explosion1regions[(int) (8 * explosion.getTime() / explosion.getLength())], position.x, position.y);
		}
	}
	
	public void end () {
		spriteBatch.end();
	}

}
