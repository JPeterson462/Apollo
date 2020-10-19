package net.digiturtle.apollo.graphics;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TimedTextureEffect extends TimedEffect {
	
	private Texture texture;
	private String texturePath;
	private Supplier<Vector2> position;
	private int width, height, frame;
	private TextureRegion region;
	
	public TimedTextureEffect (String texturePath, Supplier<Vector2> position, int width, int height, int frame) {
		this.texturePath = texturePath;
		this.position = position;
		this.width = width;
		this.height = height;
		this.frame = frame;
	}

	@Override
	public void apply(SpriteBatch spriteBatch) {

	}

	@Override
	public void unapply(SpriteBatch spriteBatch) {
		if (texture == null) {
			texture = new Texture(texturePath);
			region = new TextureRegion(texture, frame * width, 0, width, height);
		}
		// Render above the player
		Vector2 pos = position.get();
		spriteBatch.draw(region, pos.x, pos.y);
	}

}
