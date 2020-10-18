package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Match;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.Player;

public class HUDRenderer {
	
	private Match match;
	
	private TextureRegion topHud, bottomHud;
	private TextRenderer textRenderer;
	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	
	public HUDRenderer (Match match) {
		this.match = match;
	}
	
	public void create () {
		camera = new OrthographicCamera();
        camera.setToOrtho(true, Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/3);
        camera.update();
		textRenderer = new TextRenderer(camera);
		textRenderer.create();
		spriteBatch = new SpriteBatch();
		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		topHud = new TextureRegion(new Texture("HudHeaderV1.png"));
		bottomHud = new TextureRegion(new Texture("HudFooterV1.png"));
		topHud.flip(false, true);
		bottomHud.flip(false, true);
	}
	
	public void render () {
		Player player = match.getPlayer(Apollo.userId);
		
		spriteBatch.begin();
		spriteBatch.draw(topHud, 0, 0);
		spriteBatch.draw(bottomHud, 0, Gdx.graphics.getHeight()/3 - bottomHud.getRegionHeight());
		spriteBatch.end();
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(ApolloSettings.HEALTH_BAR_BOUNDS.x, ApolloSettings.HEALTH_BAR_BOUNDS.y, 
				ApolloSettings.HEALTH_BAR_BOUNDS.width * (float)player.getHealth()/ApolloSettings.PLAYER_HEALTH, ApolloSettings.HEALTH_BAR_BOUNDS.height);
		shapeRenderer.end();
		
		textRenderer.begin();
		String clockText = String.join(":", MathUtils.getClockTime(match.getTimeLeft(), 2));
		int width = (int) textRenderer.getTextWidth("00:00");
		textRenderer.text(clockText, (topHud.getRegionWidth() - width) / 2, 2, Color.WHITE);
		textRenderer.end();
	}

}
