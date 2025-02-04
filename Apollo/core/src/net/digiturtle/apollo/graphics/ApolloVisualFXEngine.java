package net.digiturtle.apollo.graphics;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.Sounds;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.VisualFXEngine;

import com.badlogic.gdx.math.Vector2;

public class ApolloVisualFXEngine implements VisualFXEngine {

	@Override
	public void addTintedDamage(Player player) {
		TimedEffect effect = new TimedTextureEffect("PlayerDamage.png", 
				() -> {
					net.digiturtle.apollo.Vector2 pos = MathUtils.mapToScreen(player.getPosition(), ApolloSettings.TILE_SIZE);
					return new Vector2(pos.x, pos.y);
				}, 
				32, 32, ((RenderablePlayer) player.getRenderablePlayer()).getFrame());
		effect.setLength(3 * 4f / 60f);
		((VisualFX) player.getVisualFX()).addEffect(effect);
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
		Sounds.pistol.play(ApolloSettings.VOLUME * .5f); // This sound is particularly loud, play at 50% normal volume
	}
	
	@Override
	public void onExplosion () {
		Sounds.grenade.play(ApolloSettings.VOLUME);
	}

}
