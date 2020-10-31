package net.digiturtle.apollo.graphics;

import java.util.HashMap;

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
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.definitions.TeamDefinition;
import net.digiturtle.apollo.match.Arsenal.Powerup;
import net.digiturtle.apollo.match.Arsenal.PowerupStatus;
import net.digiturtle.apollo.match.Match;
import net.digiturtle.apollo.match.Player;
import net.digiturtle.apollo.match.Resource;

public class HUDRenderer {
	
	private Match match;
	
	private TextureRegion topHud, bottomHud;
	private TextRenderer textRenderer;
	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private Texture powerups, bars;
	private TextureRegion[][] powerupRegions, barRegions;
	
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
		bars = new Texture("ArsenalBars.png");
		barRegions = new TextureRegion[2][10];
		for (int i = 0; i < 10; i++) {
			barRegions[0][i] = new TextureRegion(bars, i * 6, 0, 4, 20);
			barRegions[0][i].flip(false, true);
			barRegions[1][i] = new TextureRegion(bars, i * 6 + 4, 0, 2, 10);
			barRegions[1][i].flip(false, true);
		}
		powerups = new Texture("Powerups.png");
		powerupRegions = new TextureRegion[4][4];
		powerupRegions[ApolloSettings.SPEED_POWERUP] = new TextureRegion[ApolloSettings.SPEED_POWERUP_COUNT];
		for (int i = 0; i < ApolloSettings.SPEED_POWERUP_COUNT; i++) {
			powerupRegions[ApolloSettings.SPEED_POWERUP][i] = new TextureRegion(powerups, ApolloSettings.POWERUP_BOUNDS[ApolloSettings.SPEED_POWERUP][i * 4 + 0],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.SPEED_POWERUP][i * 4 + 1],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.SPEED_POWERUP][i * 4 + 2],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.SPEED_POWERUP][i * 4 + 3]);
			powerupRegions[ApolloSettings.SPEED_POWERUP][i].flip(false, true);
		}
		powerupRegions[ApolloSettings.DAMAGE_POWERUP] = new TextureRegion[ApolloSettings.DAMAGE_POWERUP_COUNT];
		for (int i = 0; i < ApolloSettings.DAMAGE_POWERUP_COUNT; i++) {
			powerupRegions[ApolloSettings.DAMAGE_POWERUP][i] = new TextureRegion(powerups, ApolloSettings.POWERUP_BOUNDS[ApolloSettings.DAMAGE_POWERUP][i * 4 + 0],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.DAMAGE_POWERUP][i * 4 + 1],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.DAMAGE_POWERUP][i * 4 + 2],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.DAMAGE_POWERUP][i * 4 + 3]);
			powerupRegions[ApolloSettings.DAMAGE_POWERUP][i].flip(false, true);
		}
		powerupRegions[ApolloSettings.RESILIENCE_POWERUP] = new TextureRegion[ApolloSettings.RESILIENCE_POWERUP_COUNT];
		for (int i = 0; i < ApolloSettings.RESILIENCE_POWERUP_COUNT; i++) {
			powerupRegions[ApolloSettings.RESILIENCE_POWERUP][i] = new TextureRegion(powerups, ApolloSettings.POWERUP_BOUNDS[ApolloSettings.RESILIENCE_POWERUP][i * 4 + 0],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.RESILIENCE_POWERUP][i * 4 + 1],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.RESILIENCE_POWERUP][i * 4 + 2],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.RESILIENCE_POWERUP][i * 4 + 3]);
			powerupRegions[ApolloSettings.RESILIENCE_POWERUP][i].flip(false, true);
		}
		powerupRegions[ApolloSettings.EXPLOSIVE_POWERUP] = new TextureRegion[ApolloSettings.EXPLOSIVE_POWERUP_COUNT];
		for (int i = 0; i < ApolloSettings.EXPLOSIVE_POWERUP_COUNT; i++) {
			powerupRegions[ApolloSettings.EXPLOSIVE_POWERUP][i] = new TextureRegion(powerups, ApolloSettings.POWERUP_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP][i * 4 + 0],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP][i * 4 + 1],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP][i * 4 + 2],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP][i * 4 + 3]);
			powerupRegions[ApolloSettings.EXPLOSIVE_POWERUP][i].flip(false, true);
		}
		
	}
	
	public void render () {
		Player player = match.getPlayer(Apollo.userId);
		
		spriteBatch.begin();
		spriteBatch.draw(topHud, 0, 0);
		spriteBatch.draw(bottomHud, 0, Gdx.graphics.getHeight()/3 - bottomHud.getRegionHeight());
		
		if (player.getArsenal() != null && player.getArsenal().getStatuses() != null && player.getArsenal().getStatuses().size() == Powerup.values().length) {
			HashMap<Powerup, PowerupStatus> statuses = player.getArsenal().getStatuses();
			
			// Powerup uses
			if (statuses.get(Powerup.SPEED).getRemaining() > 0) {
				spriteBatch.draw(powerupRegions[ApolloSettings.SPEED_POWERUP][statuses.get(Powerup.SPEED).getLevel()-1],
						ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.SPEED_POWERUP_ARSENAL_SLOT][0], 
						Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.SPEED_POWERUP_ARSENAL_SLOT][1] - 6);
			}
			
			if (statuses.get(Powerup.DAMAGE).getRemaining() > 0) {
				spriteBatch.draw(powerupRegions[ApolloSettings.DAMAGE_POWERUP][statuses.get(Powerup.DAMAGE).getLevel()-1],
					ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.DAMAGE_POWERUP_ARSENAL_SLOT][0], 
					Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.DAMAGE_POWERUP_ARSENAL_SLOT][1] - 6);
			}
			
			if (statuses.get(Powerup.EXPLOSIVES).getRemaining() > 0) {
				spriteBatch.draw(powerupRegions[ApolloSettings.EXPLOSIVE_POWERUP][statuses.get(Powerup.EXPLOSIVES).getRemaining()-1],
					ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP_ARSENAL_SLOT][0], 
					Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP_ARSENAL_SLOT][1] - 6);
			}
			
			if (statuses.get(Powerup.RESILIENCE).getRemaining() > 0) {
				spriteBatch.draw(powerupRegions[ApolloSettings.RESILIENCE_POWERUP][statuses.get(Powerup.RESILIENCE).getLevel()-1],
					ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.RESILIENCE_POWERUP_ARSENAL_SLOT][0], 
					Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.RESILIENCE_POWERUP_ARSENAL_SLOT][1] - 6);
			}
			
			// Powerup timings
			if (player.getPowerupTimeLeft(Powerup.DAMAGE) > 0) {
				int level = (int) (10 * (float)player.getPowerupTimeLeft(Powerup.DAMAGE) / Powerup.DAMAGE.time);
				spriteBatch.draw(barRegions[1][10 - 1 - Math.min(level, 10 - 1)],
						ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.DAMAGE_POWERUP_SLOT][0], 
						Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.DAMAGE_POWERUP_SLOT][1] + 16);
			}
			if (player.getPowerupTimeLeft(Powerup.SPEED) > 0) {
				int level = (int) (10 * (float)player.getPowerupTimeLeft(Powerup.SPEED) / Powerup.SPEED.time);
				spriteBatch.draw(barRegions[1][10 - 1 - Math.min(level, 10 - 1)],
						ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.SPEED_POWERUP_SLOT][0], 
						Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.SPEED_POWERUP_SLOT][1] + 16);
			}
			if (player.getPowerupTimeLeft(Powerup.RESILIENCE) > 0) {
				int level = (int) (10 * (float)player.getPowerupTimeLeft(Powerup.RESILIENCE) / Powerup.RESILIENCE.time);
				spriteBatch.draw(barRegions[1][10 - 1 - Math.min(level, 10 - 1)],
						ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.RESILIENCE_POWERUP_SLOT][0], 
						Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.RESILIENCE_POWERUP_SLOT][1] + 16);
			}
		}
		
		if (player.getBackpack() != null) {
			int coal = player.getBackpack().getContents().get(Resource.COAL);
			
			int level = (int) ((float)coal / (float)50);
			spriteBatch.draw(barRegions[0][10 - 1 - Math.min(level, 10 - 1)],
					ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.COAL_BACKPACK_SLOT][0], 
					Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.COAL_BACKPACK_SLOT][1] - 4);
			
			int sheetMetal = player.getBackpack().getContents().get(Resource.SHEET_METAL);
			
			level = (int) ((float)sheetMetal / (float)50);
			spriteBatch.draw(barRegions[0][10 - 1 - Math.min(level, 10 - 1)],
					ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.SHEET_METAL_BACKPACK_SLOT][0], 
					Gdx.graphics.getHeight()/3 - ApolloSettings.ARSENAL_BOUNDS[ApolloSettings.SHEET_METAL_BACKPACK_SLOT][1] - 4);
			
		}
		
		spriteBatch.end();
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(ApolloSettings.HEALTH_BAR_BOUNDS.x, ApolloSettings.HEALTH_BAR_BOUNDS.y, 
				ApolloSettings.HEALTH_BAR_BOUNDS.width * (float)player.getHealth()/ApolloSettings.PLAYER_HEALTH, ApolloSettings.HEALTH_BAR_BOUNDS.height);
		shapeRenderer.end();

		int[] points = new int[TeamDefinition.COLOR_COUNT];
		int total = 0;
		for (int i = 0; i < points.length && i < match.getTeams().length; i++) {
			points[i] = ApolloSettings.getResourceValue(match.getTeams()[i].getBank().getContents());
			total += points[i];
		}
		total = Math.max(total, 1);
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(ApolloSettings.RED_BAR_BOUNDS[0], ApolloSettings.RED_BAR_BOUNDS[1], 
				ApolloSettings.RED_BAR_BOUNDS[2] * (float)points[TeamDefinition.COLOR_RED]/total, ApolloSettings.RED_BAR_BOUNDS[3]);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.BLUE);
		shapeRenderer.rect(ApolloSettings.BLUE_BAR_BOUNDS[0], ApolloSettings.BLUE_BAR_BOUNDS[1], 
				ApolloSettings.BLUE_BAR_BOUNDS[2] * (float)points[TeamDefinition.COLOR_BLUE]/total, ApolloSettings.BLUE_BAR_BOUNDS[3]);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.rect(ApolloSettings.GREEN_BAR_BOUNDS[0], ApolloSettings.GREEN_BAR_BOUNDS[1], 
				ApolloSettings.GREEN_BAR_BOUNDS[2] * (float)points[TeamDefinition.COLOR_GREEN]/total, ApolloSettings.GREEN_BAR_BOUNDS[3]);
		shapeRenderer.end();

		textRenderer.begin();
		String clockText = String.join(":", MathUtils.getClockTime(match.getTimeLeft(), 2));
		int width = (int) textRenderer.getTextWidth("00:00");
		textRenderer.text(clockText, (topHud.getRegionWidth() - width) / 2, 2, Color.WHITE);
		
		textRenderer.text(Apollo.debugMessage, 0, 100, Color.YELLOW);
		
		textRenderer.end();
	}

}
