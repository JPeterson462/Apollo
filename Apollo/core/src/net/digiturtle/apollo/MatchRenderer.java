package net.digiturtle.apollo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class MatchRenderer {

	private OrthographicCamera camera;
	private TiledMapRenderer tiledMapRenderer;
	private PlayerRenderer playerRenderer;
	private BulletsRenderer bulletsRenderer;
	
	private SpriteBatch spriteBatch;
	private Texture testHotspot;

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
        bulletsRenderer = new BulletsRenderer();
        bulletsRenderer.create();
        DebugRenderer.create();
        
        //FIXME
        Hotspot testH = match.getHotspots().get(0);
        DebugRenderer.addLine(
        		MathUtils.mapToScreen(testH.getPosition(), ApolloSettings.TILE_SIZE), 
        		MathUtils.mapToScreen(new Vector2(testH.getPosition()).add(testH.getSize().x, 0), ApolloSettings.TILE_SIZE));
        DebugRenderer.addLine(
        		MathUtils.mapToScreen(testH.getPosition(), ApolloSettings.TILE_SIZE), 
        		MathUtils.mapToScreen(new Vector2(testH.getPosition()).add(0, testH.getSize().y), ApolloSettings.TILE_SIZE));
        
        spriteBatch = new SpriteBatch();
        testHotspot = new Texture("TestHotspotIsometric.png");
	}
	
	public void render () {
		if (match.getPlayers().size() == 0) return;
		
		float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        camera.translate(-w/2, -h/2);
        
        Vector2 position = MathUtils.mapToScreen(match.getPlayer(Apollo.userId).getPosition(), ApolloSettings.TILE_SIZE);
        
        camera.translate(position.x, position.y);
        
        float testval = ApolloSettings.TILE_SIZE/2;
        camera.translate(0, testval);//FIXME this is tile size * .5 ?? maybe not
        
        camera.zoom = 1/3f;
        
        camera.update();
        
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        camera.translate(0, -testval);//FIXME this is tile size * .5 ?? maybe not
        
        camera.update();
        
        bulletsRenderer.render(camera, match.getBullets(), ApolloSettings.TILE_SIZE);

        camera.translate(0, 48);//FIXME magic number
        
        camera.update();
        
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        for (Hotspot hotspot : match.getHotspots()) {
        	Vector2 hotspotPosition = MathUtils.mapToScreen(hotspot.getPosition(), ApolloSettings.TILE_SIZE);
        	spriteBatch.draw(testHotspot, hotspotPosition.x, hotspotPosition.y);
        }
        spriteBatch.end();

        camera.translate(0, -48);
        
        camera.update();

        DebugRenderer.render(camera);
        
        camera.translate(ApolloSettings.CHARACTER_SIZE/2, ApolloSettings.CHARACTER_SIZE/2);
        
        camera.update();
        
    	playerRenderer.begin(camera);
		for (Player player : match.getPlayers()) {
			playerRenderer.render(player.getRenderablePlayer(), player.getPosition(), ApolloSettings.TILE_SIZE);
		}
		playerRenderer.end();
        
        camera.translate(-ApolloSettings.CHARACTER_SIZE/2, -ApolloSettings.CHARACTER_SIZE/2);

        camera.translate(-position.x, -position.y);
        
        camera.translate(w/2, h/2);
        
	}

}
