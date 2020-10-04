package net.digiturtle.apollo;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public class PlayerRenderer {
	
	private SpriteBatch entityBatch;
	
	public void create () {
		entityBatch = new SpriteBatch();
	}
	
	public void begin (Camera camera) {
		entityBatch.begin();
		entityBatch.setProjectionMatrix(camera.combined);
		entityBatch.setTransformMatrix(new Matrix4().setToScaling(3, 3, 1));
	}
	
	public void render (RenderablePlayer player) {
		entityBatch.draw(player.getCurrentTexture(), 0, 0);
	}
	
	public void end () {
		entityBatch.end();
	}

}
