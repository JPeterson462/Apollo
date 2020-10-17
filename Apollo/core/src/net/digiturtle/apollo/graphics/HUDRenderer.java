package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.graphics.Color;

import net.digiturtle.apollo.Match;
import net.digiturtle.apollo.MathUtils;

public class HUDRenderer {
	
	private Match match;
	
	private TextRenderer textRenderer;
	
	public HUDRenderer (Match match) {
		this.match = match;
	}
	
	public void create () {
		textRenderer = new TextRenderer();
		textRenderer.create();
	}
	
	public void render () {
		textRenderer.begin();
		String[] clock = MathUtils.getClockTime(match.getTimeLeft(), 2);
		textRenderer.text(clock[0] + ":" + clock[1], 5, 5, Color.WHITE);
		textRenderer.end();
	}

}
