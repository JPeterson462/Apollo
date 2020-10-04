package net.digiturtle.apollo;

import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Apollo extends ApplicationAdapter {
	
	private MatchRenderer matchRenderer;
	private Match match;
	private UUID userId;
	
	@Override
	public void create () {
		userId = UUID.randomUUID();
		match = new Match();
		match.addPlayer(new Player(userId));
		matchRenderer = new MatchRenderer(match);
		matchRenderer.create();
        Gdx.input.setInputProcessor(new MatchInputController(match));
	}

	@Override
	public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        matchRenderer.render();
        match.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
	}

}
