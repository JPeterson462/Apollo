package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.match.IRenderablePlayer;
import net.digiturtle.apollo.match.Player;

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
	
	public void render (Player basePlayer, IRenderablePlayer player, Vector2 playerPosition, float tileSize) {
		((VisualFX) basePlayer.getVisualFX()).apply(entityBatch);
        net.digiturtle.apollo.Vector2 position = MathUtils.mapToScreen(new net.digiturtle.apollo.Vector2(playerPosition.x, playerPosition.y), tileSize);
		entityBatch.draw(((RenderablePlayer) player).getCurrentTexture(), position.x, position.y);
		((VisualFX) basePlayer.getVisualFX()).unapply(entityBatch);
	}
	
	public void end () {
		entityBatch.end();
	}

}
