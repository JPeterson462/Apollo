package net.digiturtle.apollo;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlayerRenderer {
	
	private SpriteBatch entityBatch;
	
	public void create () {
		entityBatch = new SpriteBatch();
	}
	
	public void begin (Camera camera) {
		entityBatch.begin();
		entityBatch.setProjectionMatrix(camera.combined);
		entityBatch.setColor(Color.WHITE);
	}
	
	public void render (Player basePlayer, RenderablePlayer player, Vector2 playerPosition, float tileSize) {
		basePlayer.getVisualFX().apply(entityBatch);
        Vector2 position = MathUtils.mapToScreen(playerPosition, tileSize);
		entityBatch.draw(player.getCurrentTexture(), position.x, position.y);
		basePlayer.getVisualFX().unapply(entityBatch);
	}
	
	public void end () {
		entityBatch.end();
	}

}
