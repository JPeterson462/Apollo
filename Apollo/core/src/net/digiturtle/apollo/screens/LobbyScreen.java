package net.digiturtle.apollo.screens;

import java.util.Arrays;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.Button;
import net.digiturtle.apollo.ButtonInputController;
import net.digiturtle.apollo.Rectangle;
import net.digiturtle.apollo.graphics.TextRenderer;

public class LobbyScreen extends Screen {

	private Texture shopBgAll, matchBg, buttons, powerups, hud;
	private TextureRegion shopBg4, shopBg3, matchBg1;
	private TextureRegion[] buttonUpgrade, buttonJoin;
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private TextRenderer textRenderer;
	private TextureRegion[][] powerupRegions;
	
	private int[] states;
	private Button[] allButtons;
	private ButtonInputController buttonController;
	
	private void onButton (String event) {
		String action = event.substring(0, event.indexOf('.'));
		String data = event.substring(event.indexOf('.') + 1);
		if (action.equalsIgnoreCase("Upgrade")) {
			switch (data) {
			case "Speed":
				break;
			case "Damage":
				break;
			case "Resilience":
				break;
			case "Explosive":
				break;
			}
			
		}
		if (action.equalsIgnoreCase("Match")) {
			int slot = Integer.parseInt(data);
			
		}
	}

	@Override
	public void create () {
		hud = new Texture("LobbyHud.png");
		shopBgAll = new Texture("ShopBG.png");
		matchBg = new Texture("MatchSlot.png");
		shopBg4 = new TextureRegion(shopBgAll, 0, 0, 120, 54);
		shopBg3 = new TextureRegion(shopBgAll, 120, 0, 120, 54);
		buttons = new Texture("Buttons1.png");
		buttonUpgrade = new TextureRegion[3];
		buttonJoin = new TextureRegion[3];
		for (int i = 0; i < 3; i++) {
			buttonUpgrade[i] = new TextureRegion(buttons, 0, i * 11, 50, 11);
			buttonJoin[i] = new TextureRegion(buttons, 50, i * 11, 11, 11);
		}
		spriteBatch = new SpriteBatch();
		camera = createCamera();
		textRenderer = new TextRenderer(camera);
		textRenderer.create();
		
		powerups = new Texture("Powerups.png");
		powerupRegions = new TextureRegion[4][4];
		powerupRegions[ApolloSettings.SPEED_POWERUP] = new TextureRegion[ApolloSettings.SPEED_POWERUP_COUNT];
		for (int i = 0; i < ApolloSettings.SPEED_POWERUP_COUNT; i++) {
			powerupRegions[ApolloSettings.SPEED_POWERUP][i] = new TextureRegion(powerups, ApolloSettings.POWERUP_BOUNDS[ApolloSettings.SPEED_POWERUP][i * 4 + 0],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.SPEED_POWERUP][i * 4 + 1],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.SPEED_POWERUP][i * 4 + 2],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.SPEED_POWERUP][i * 4 + 3]);
			//powerupRegions[ApolloSettings.SPEED_POWERUP][i].flip(false, true);
		}
		powerupRegions[ApolloSettings.DAMAGE_POWERUP] = new TextureRegion[ApolloSettings.DAMAGE_POWERUP_COUNT];
		for (int i = 0; i < ApolloSettings.DAMAGE_POWERUP_COUNT; i++) {
			powerupRegions[ApolloSettings.DAMAGE_POWERUP][i] = new TextureRegion(powerups, ApolloSettings.POWERUP_BOUNDS[ApolloSettings.DAMAGE_POWERUP][i * 4 + 0],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.DAMAGE_POWERUP][i * 4 + 1],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.DAMAGE_POWERUP][i * 4 + 2],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.DAMAGE_POWERUP][i * 4 + 3]);
			//powerupRegions[ApolloSettings.DAMAGE_POWERUP][i].flip(false, true);
		}
		powerupRegions[ApolloSettings.RESILIENCE_POWERUP] = new TextureRegion[ApolloSettings.RESILIENCE_POWERUP_COUNT];
		for (int i = 0; i < ApolloSettings.RESILIENCE_POWERUP_COUNT; i++) {
			powerupRegions[ApolloSettings.RESILIENCE_POWERUP][i] = new TextureRegion(powerups, ApolloSettings.POWERUP_BOUNDS[ApolloSettings.RESILIENCE_POWERUP][i * 4 + 0],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.RESILIENCE_POWERUP][i * 4 + 1],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.RESILIENCE_POWERUP][i * 4 + 2],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.RESILIENCE_POWERUP][i * 4 + 3]);
			//powerupRegions[ApolloSettings.RESILIENCE_POWERUP][i].flip(false, true);
		}
		powerupRegions[ApolloSettings.EXPLOSIVE_POWERUP] = new TextureRegion[ApolloSettings.EXPLOSIVE_POWERUP_COUNT];
		for (int i = 0; i < ApolloSettings.EXPLOSIVE_POWERUP_COUNT; i++) {
			powerupRegions[ApolloSettings.EXPLOSIVE_POWERUP][i] = new TextureRegion(powerups, ApolloSettings.POWERUP_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP][i * 4 + 0],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP][i * 4 + 1],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP][i * 4 + 2],
					ApolloSettings.POWERUP_BOUNDS[ApolloSettings.EXPLOSIVE_POWERUP][i * 4 + 3]);
			//powerupRegions[ApolloSettings.EXPLOSIVE_POWERUP][i].flip(false, true);
		}
		
		states = new int[11];
		allButtons = new Button[11];
		Arrays.fill(states, Button.STATE_DEFAULT);
		allButtons[0] = new Button(new Rectangle(35 + camera.viewportWidth - 120 - 5, 37 + camera.viewportHeight - 54 - 5, 50, 11), "Upgrade.Explosive");
		allButtons[1] = new Button(new Rectangle(35 + camera.viewportWidth - 120 - 5, 37 + camera.viewportHeight - 54 - 5 - 54 - 5, 50, 11), "Upgrade.Resilience");
		allButtons[2] = new Button(new Rectangle(35 + camera.viewportWidth - 120 - 5, 37 + camera.viewportHeight - 54 - 5 - 54 - 5 - 54 - 5, 50, 11), "Upgrade.Damage");
		allButtons[3] = new Button(new Rectangle(35 + camera.viewportWidth - 120 - 5, 37 + camera.viewportHeight - 54 - 5 - 54 - 5 - 54 - 5 - 54 - 5, 50, 11), "Upgrade.Speed");
		for (int i = 1; i <= 7; i++) {
			allButtons[i + 3] = new Button(new Rectangle(5 + 160 - 14, 20 + (i-1)*33, 11, 11), "Match." + i);
		}
		buttonController = new ButtonInputController(allButtons, states, this::onButton);
	}

	@Override
	public void render () {
		//Screen.set(ScreenId.MATCH);
		spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        
        spriteBatch.draw(hud, 182, 216);
		
		spriteBatch.draw(shopBg4, camera.viewportWidth - 120 - 5, camera.viewportHeight - 54 - 5);
		spriteBatch.draw(buttonUpgrade[states[3]], 35 + camera.viewportWidth - 120 - 5, 7 + camera.viewportHeight - 54 - 5);
		for (int i = 0; i < Apollo.user.getSpeedPowerup(); i++) {
			spriteBatch.draw(powerupRegions[ApolloSettings.SPEED_POWERUP][i], 8 + i*28 + camera.viewportWidth - 120 - 5, 25 + camera.viewportHeight - 54 - 5);
		}

		spriteBatch.draw(shopBg4, camera.viewportWidth - 120 - 5, camera.viewportHeight - 54 - 5 - 54 - 5);
		spriteBatch.draw(buttonUpgrade[states[2]], 35 + camera.viewportWidth - 120 - 5, 7 + camera.viewportHeight - 54 - 5 - 54 - 5);
		for (int i = 0; i < Apollo.user.getDamagePowerup(); i++) {
			spriteBatch.draw(powerupRegions[ApolloSettings.DAMAGE_POWERUP][i], 8 + i*28 + camera.viewportWidth - 120 - 5, 25 + camera.viewportHeight - 54 - 5 - 54 - 5);
		}
		
		spriteBatch.draw(shopBg4, camera.viewportWidth - 120 - 5, camera.viewportHeight - 54 - 5 - 54 - 5 - 54 - 5);
		spriteBatch.draw(buttonUpgrade[states[1]], 35 + camera.viewportWidth - 120 - 5, 7 + camera.viewportHeight - 54 - 5 - 54 - 5 - 54 - 5);
		for (int i = 0; i < Apollo.user.getResiliencePowerup(); i++) {
			spriteBatch.draw(powerupRegions[ApolloSettings.RESILIENCE_POWERUP][i], 8 + i*28 + camera.viewportWidth - 120 - 5, 25 + camera.viewportHeight - 54 - 5 - 54 - 5 - 54 - 5);
		}

		spriteBatch.draw(shopBg3, camera.viewportWidth - 120 - 5, camera.viewportHeight - 54 - 5 - 54 - 5 - 54 - 5 - 54 - 5);
		spriteBatch.draw(buttonUpgrade[states[0]], 35 + camera.viewportWidth - 120 - 5, 7 + camera.viewportHeight - 54 - 5 - 54 - 5 - 54 - 5 - 54 - 5);
		for (int i = 0; i < Apollo.user.getExplosivesPowerup(); i++) {
			spriteBatch.draw(powerupRegions[ApolloSettings.EXPLOSIVE_POWERUP][i], 8+14 + i*28 + camera.viewportWidth - 120 - 5, 25 + camera.viewportHeight - 54 - 5 - 54 - 5 - 54 - 5 - 54 - 5);
		}
		
		for (int i = 1; i <= 7; i++) {
			spriteBatch.draw(matchBg, 5, camera.viewportHeight - i * (28 + 5));
			spriteBatch.draw(buttonJoin[states[i + 3]], 5 + 160 - 14, 3 + camera.viewportHeight - i * (28 + 5));
		}
		
		spriteBatch.end();
		
		textRenderer.begin();

		for (int i = 1; i <= Apollo.lobbies.length; i++) {
			textRenderer.text(Apollo.lobbies[i-1].getWorldName(), 8, (int) camera.viewportHeight - i * (28 + 5) + 28-3, Color.YELLOW);
			
			textRenderer.text(Apollo.lobbies[i-1].getStatus().text(), 8, (int) camera.viewportHeight - i * (28 + 5) + 28-3 - 12, Color.GREEN);
			
			String XvY = Apollo.lobbies[i-1].getPlayerTotal();
			textRenderer.text(XvY, 5 + 160-35 - (int)textRenderer.getTextWidth(XvY) - 3, (int) camera.viewportHeight - i * (28 + 5) + 28-3, Color.BLUE);
			
			String X_Y = Apollo.lobbies[i-1].getPlayerCount();
			textRenderer.text(X_Y, 5 + 160-35 - (int)textRenderer.getTextWidth(X_Y) - 3, (int) camera.viewportHeight - i * (28 + 5) + 28-3 - 12, Color.BLUE);
		}

        String coins = Integer.toString(Apollo.user.getCoins());
        textRenderer.text(coins, 182 + (100 - (int)textRenderer.getTextWidth(coins))/2, 236, Color.WHITE);
		
		textRenderer.end();
	}

	@Override
	public void onPacket (Object object) {
		
	}

	public InputProcessor getInputProcessor () {
		return buttonController;
	}

}
