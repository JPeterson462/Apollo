package net.digiturtle.apollo.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.graphics.TextRenderer;

public class MatchOverScreen extends Screen {
	
	private TextRenderer textRenderer;
	private OrthographicCamera camera;

	@Override
	public void create () {
		camera = createCamera();
		textRenderer = new TextRenderer(camera, false);
		textRenderer.create();
		
	}

	@Override
	public void render () {
		textRenderer.begin();
		
		textRenderer.text("Count: " + Apollo.teamCounts[0] + " | " + Apollo.teamCounts[1], 10, 10, Color.BLUE);
		
		textRenderer.end();
	}

	@Override
	public void onPacket (Object object) {
		
	}

}
