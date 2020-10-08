package net.digiturtle.apollo;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BulletsRenderer {
	
	private SpriteBatch entityBatch;
	private Texture testBullet;
	
	public void create () {
		entityBatch = new SpriteBatch();
		testBullet = new Texture("TestBullet.png");
	}
	
	public void render (Camera camera, ArrayList<Bullet> bullets, float tileSize) {
		entityBatch.begin();
		entityBatch.setProjectionMatrix(camera.combined);
		for (int i = 0; i < bullets.size(); i++) {
			Vector2 position = MathUtils.mapToScreen(bullets.get(i).getPosition(), tileSize);
			entityBatch.draw(testBullet, position.x - 1, position.y - 1);
		}
		entityBatch.end();
	}

}
