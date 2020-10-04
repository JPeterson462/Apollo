package net.digiturtle.apollo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class MatchRenderer {

	private OrthographicCamera camera;
	private TiledMapRenderer tiledMapRenderer;
	private PlayerRenderer playerRenderer;

	private RenderablePlayer player1;
	
	private Match match;
	
	public MatchRenderer(Match match) {
		this.match = match;
	}
	
	public void create () {
		float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();
        tiledMapRenderer = new IsometricTiledMapRenderer(match.getTiledMap());
        playerRenderer = new PlayerRenderer();
        playerRenderer.create();
	}
	
	public void render () {
		float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        Vector2 position = MathUtils.mapToScreen(match.getPlayer().getPosition(), 256);
		camera.translate(-w/2, -h/2, 0);
		camera.translate(-position.x, -position.y, 0);
		camera.zoom /= 3;
		camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
		camera.zoom *= 3;

		camera.translate(position.x, position.y, 0);
		camera.update();
		playerRenderer.begin(camera);
		playerRenderer.render(match.getPlayer().getRenderablePlayer());
		playerRenderer.end();
		camera.translate(-position.x, -position.y, 0);
        
		camera.translate(position.x, position.y, 0);
		camera.translate(w/2, h/2, 0);
	}

}
