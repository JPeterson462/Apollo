package net.digiturtle.apollo.graphics;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.VisualFXEngine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ApolloVisualFXEngine implements VisualFXEngine {

	@Override
	public void addTintedDamage(Player player) {
		TintEffect effect = new TintEffect(Color.RED);
		effect.setLength(.25f);
		((VisualFX) player.getVisualFX()).addEffect(effect);
		System.out.println("Adding " + effect + " to " + player.getId());
	}

	@Override
	public void addMuzzleFlash(Player player) {
		TimedEffect effect = new TimedTextureEffect("PlayerV4_MuzzleFlash.png", 
				() -> {
					net.digiturtle.apollo.Vector2 pos = MathUtils.mapToScreen(player.getPosition(), ApolloSettings.TILE_SIZE);
					return new Vector2(pos.x, pos.y);
				}, 
				32, 32, ((RenderablePlayer) player.getRenderablePlayer()).getFrame());
		effect.setLength(4f / 60f);
		((VisualFX) player.getVisualFX()).addEffect(effect);
	}

}
