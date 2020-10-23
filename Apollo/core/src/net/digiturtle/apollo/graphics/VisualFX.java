package net.digiturtle.apollo.graphics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.digiturtle.apollo.match.IVisualFX;

public class VisualFX implements IEffect, IVisualFX {
	
	private ArrayList<IEffect> effects;
	
	public VisualFX () {
		effects = new ArrayList<>();
	}
	
	public ArrayList<IEffect> getEffects () {
		return effects;
	}
	
	public void addEffect (IEffect effect) {
		effects.add(effect);
	}
	
	public void apply (SpriteBatch spriteBatch) {
		for (int i = 0; i < effects.size(); i++) {
			effects.get(i).apply(spriteBatch);
		}
	}
	
	public void unapply (SpriteBatch spriteBatch) {
		for (int i = effects.size() - 1; i >= 0; i--) {
			effects.get(i).unapply(spriteBatch);
			IEffect effect = effects.get(i);
			if (effect instanceof TimedEffect && !((TimedEffect) effect).isActive()) {
				effects.remove(i);
			}
		}
	}
	
	public void update (float dt) {
		for (int i = 0; i < effects.size(); i++) {
			effects.get(i).update(dt);
		}
	}

}
