package net.digiturtle.apollo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IEffect {
	
	public void apply (SpriteBatch spriteBatch);
	
	public void update (float dt);
	
	public void unapply (SpriteBatch spriteBatch);

}
