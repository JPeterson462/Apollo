package net.digiturtle.apollo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TintEffect extends TimedEffect {
	
	private Color newColor;
	
	public TintEffect(Color color) {
		newColor = color;
	}

	@Override
	public void apply(SpriteBatch spriteBatch) {
		if (isActive()) {
			spriteBatch.setColor(newColor);
		}
	}

	@Override
	public void unapply(SpriteBatch spriteBatch) {
		if (isActive()) {
			spriteBatch.setColor(Color.WHITE);
		}
	}

}
