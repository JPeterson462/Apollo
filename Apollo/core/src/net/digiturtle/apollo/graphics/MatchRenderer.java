package net.digiturtle.apollo.graphics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.DroppedBackpack;
import net.digiturtle.apollo.Match;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.Player;
import net.digiturtle.apollo.Resource;
import net.digiturtle.apollo.ResourceRegion;

public class MatchRenderer {

	private OrthographicCamera camera;
	private TiledMapRenderer tiledMapRenderer;
	private PlayerRenderer playerRenderer;
	private BulletsRenderer bulletsRenderer;
	private HUDRenderer hudRenderer;
	
	private SpriteBatch spriteBatch;
	private Texture droppedBackpack;

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
        hudRenderer = new HUDRenderer(match);
        hudRenderer.create();
        
        DebugRenderer.create();
        
        spriteBatch = new SpriteBatch();
        droppedBackpack = new Texture("BackpackV1.png");
        
        for (Resource resource : Resource.values()) {
        	resource.create();
        }
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

        camera.translate(0, 80);//FIXME magic number
        
        camera.update();
        
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        
        for (ResourceRegion resourceRegion : match.getResourceRegions()) {
        	Vector2 hotspotPosition = MathUtils.mapToScreen(resourceRegion.getPosition(), ApolloSettings.TILE_SIZE);
        	int state = (int) (((float)resourceRegion.getQuantity()/resourceRegion.getCapacity()) / (1f/resourceRegion .getResource().getNumberOfStates()));
        	spriteBatch.draw(resourceRegion.getResource().getRegionTexture(resourceRegion.getResource().getNumberOfStates() - 1 - state), hotspotPosition.x, hotspotPosition.y);
        }
        
        spriteBatch.end();

        camera.translate(0, -80);
        
        camera.update();
        
        camera.translate(32, 32);

        camera.update();
        
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        
        for (DroppedBackpack droppedBackpack : match.getDroppedBackpacks()) {
        	Vector2 backpackPosition = MathUtils.mapToScreen(droppedBackpack.getPosition(), ApolloSettings.TILE_SIZE);
        	spriteBatch.draw(this.droppedBackpack, backpackPosition.x, backpackPosition.y);
        }
        
        spriteBatch.end();

        camera.translate(-32, -32);
        
        camera.update();
        
        DebugRenderer.render(camera);
        
        camera.translate(ApolloSettings.CHARACTER_SIZE/2, ApolloSettings.CHARACTER_SIZE/2);
        
        camera.update();
        
    	playerRenderer.begin(camera);
    	ArrayList<Player> playersToRender = new ArrayList<Player>(match.getPlayers());
    	// TODO only render players in view
    	playersToRender.sort((p1, p2) -> -Float.compare(p1.getPosition().y, p2.getPosition().y));
		for (Player player : playersToRender) {
			playerRenderer.render(player, player.getRenderablePlayer(), player.getPosition(), ApolloSettings.TILE_SIZE);
		}
		playerRenderer.end();
        
        camera.translate(-ApolloSettings.CHARACTER_SIZE/2, -ApolloSettings.CHARACTER_SIZE/2);

        camera.translate(-position.x, -position.y);
        
        camera.translate(w/2, h/2);
        
        hudRenderer.render();
	}

}
