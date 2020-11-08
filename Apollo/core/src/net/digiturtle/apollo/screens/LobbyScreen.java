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
import net.digiturtle.apollo.FiberPool;
import net.digiturtle.apollo.Lobby;
import net.digiturtle.apollo.Rectangle;
import net.digiturtle.apollo.User;
import net.digiturtle.apollo.graphics.TextRenderer;
import net.digiturtle.apollo.match.event.UserLobbiedEvent;
import net.digiturtle.apollo.match.event.UserLobbyEvent;
import net.digiturtle.apollo.match.event.UserLobbyQuery;
import net.digiturtle.apollo.match.event.UserLobbyQuery.LobbyResult;
import net.digiturtle.apollo.match.event.UserUpgradeEvent;
import net.digiturtle.apollo.match.event.UserUpgradeResponse;

public class LobbyScreen extends Screen {

	private Texture shopBgAll, matchBg, buttons, powerups, hud;
	private TextureRegion shopBg4, shopBg3, matchBg1;
	private TextureRegion[] buttonUpgrade, buttonJoin;
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private TextRenderer textRenderer;
	private TextureRegion[][] powerupRegions;
	private FiberPool threads;
	
	private int[] states;
	private Button[] allButtons;
	private ButtonInputController buttonController;
	
	private void onButton (String event) {
		String action = event.substring(0, event.indexOf('.'));
		String data = event.substring(event.indexOf('.') + 1);
		if (action.equalsIgnoreCase("Upgrade")) {
			switch (data) {
			case "Speed":
				if (Apollo.user.getSpeedPowerup() < ApolloSettings.SPEED_POWERUP_COUNT)
					Apollo.sendToMain(new UserUpgradeEvent(Apollo.user, ApolloSettings.SPEED_POWERUP));
				break;
			case "Damage":
				if (Apollo.user.getDamagePowerup() < ApolloSettings.DAMAGE_POWERUP_COUNT)
					Apollo.sendToMain(new UserUpgradeEvent(Apollo.user, ApolloSettings.DAMAGE_POWERUP));
				break;
			case "Resilience":
				if (Apollo.user.getResiliencePowerup() < ApolloSettings.RESILIENCE_POWERUP_COUNT)
					Apollo.sendToMain(new UserUpgradeEvent(Apollo.user, ApolloSettings.RESILIENCE_POWERUP));
				break;
			case "Explosive":
				if (Apollo.user.getExplosivesPowerup() < ApolloSettings.EXPLOSIVE_POWERUP_COUNT)
					Apollo.sendToMain(new UserUpgradeEvent(Apollo.user, ApolloSettings.EXPLOSIVE_POWERUP));
				break;
			}
			
		}
		if (action.equalsIgnoreCase("Match")) {
			int slot = Integer.parseInt(data);
			if (Apollo.lobbies[slot-1].getStatus().equals(Lobby.LobbyStatus.In_Lobby)) {
				Apollo.sendToMain(new UserLobbyEvent(Apollo.user, slot));
			}
		}
	}

	public void onManagerPacket (Object object) {
		if (object instanceof UserLobbiedEvent) {
			UserLobbiedEvent userLobbied = (UserLobbiedEvent) object;
			Apollo.matchIp = userLobbied.getIP();
			Apollo.matchPort = userLobbied.getPort();
			Apollo.readyToJoin = true;
			Screen.set(ScreenId.MATCH_LOBBY);
		}
		if (object instanceof UserLobbyQuery.Response) {
			UserLobbyQuery.Response queryResponse = (UserLobbyQuery.Response) object;
			Lobby[] lobbies = new Lobby[queryResponse.lobbies.length];
			for (int i = 0; i < lobbies.length; i++) {
				LobbyResult src = queryResponse.lobbies[i];
				Lobby dst = new Lobby();
				dst.setPlayerCount(Integer.toString(src.playersConnected) + "/" + Integer.toString(src.playersPerTeam * src.teams));
				dst.setPlayerTotal(src.playersPerTeam + "v" + src.playersPerTeam + (src.teams > 2 ? ("v" + src.playersPerTeam) : ""));
				dst.setStatus(src.lobbyStatus);
				dst.setWorldName(src.worldName);
				lobbies[i] = dst;
			}
			Apollo.lobbies = lobbies;
		}
		if (object instanceof UserUpgradeResponse) {
			UserUpgradeResponse upgradeResponse = (UserUpgradeResponse) object;
			if (upgradeResponse.success) {
				User user = Apollo.user;
				int cost;
				switch (upgradeResponse.powerup) {
				case ApolloSettings.SPEED_POWERUP:
					cost = ApolloSettings.POWERUP_COSTS[upgradeResponse.powerup][user.getSpeedPowerup()-1];
					user.setSpeedPowerup(user.getSpeedPowerup() + 1);
					user.setCoins(user.getCoins() - cost);
				break;
				case ApolloSettings.DAMAGE_POWERUP:
					cost = ApolloSettings.POWERUP_COSTS[upgradeResponse.powerup][user.getDamagePowerup()-1];
					user.setDamagePowerup(user.getDamagePowerup() + 1);
					user.setCoins(user.getCoins() - cost);
					break;
				case ApolloSettings.RESILIENCE_POWERUP:
					cost = ApolloSettings.POWERUP_COSTS[upgradeResponse.powerup][user.getResiliencePowerup()-1];
					user.setResiliencePowerup(user.getResiliencePowerup() + 1);
					user.setCoins(user.getCoins() - cost);
					break;
				case ApolloSettings.EXPLOSIVE_POWERUP:
					cost = ApolloSettings.POWERUP_COSTS[upgradeResponse.powerup][user.getExplosivesPowerup()-1];
					user.setExplosivesPowerup(user.getExplosivesPowerup() + 1);
					user.setCoins(user.getCoins() - cost);
					break;
				}
			}
		}
	}
	
	@Override
	public void create () {
		threads = new FiberPool(1);
		threads.scheduleTask(1000, () -> {
			if (Screen.current == ScreenId.LOBBY) {
				Apollo.sendToMain(new UserLobbyQuery.Request());
			}
		});
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
		textRenderer = new TextRenderer(camera, false);
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
		User user = Apollo.user;
		states[0] = (user.getExplosivesPowerup() == ApolloSettings.EXPLOSIVE_POWERUP_COUNT || 
				user.getCoins() < ApolloSettings.POWERUP_COSTS[ApolloSettings.EXPLOSIVE_POWERUP][user.getExplosivesPowerup()-1]) ? Button.STATE_DOWN : states[0];
		states[1] = (user.getResiliencePowerup() == ApolloSettings.RESILIENCE_POWERUP_COUNT || 
				user.getCoins() < ApolloSettings.POWERUP_COSTS[ApolloSettings.RESILIENCE_POWERUP][user.getResiliencePowerup()-1]) ? Button.STATE_DOWN : states[1];
		states[2] = (user.getDamagePowerup() == ApolloSettings.DAMAGE_POWERUP_COUNT || 
				user.getCoins() < ApolloSettings.POWERUP_COSTS[ApolloSettings.DAMAGE_POWERUP][user.getDamagePowerup()-1]) ? Button.STATE_DOWN : states[2];
		states[3] = (user.getSpeedPowerup() == ApolloSettings.SPEED_POWERUP_COUNT || 
				user.getCoins() < ApolloSettings.POWERUP_COSTS[ApolloSettings.SPEED_POWERUP][user.getSpeedPowerup()-1]) ? Button.STATE_DOWN : states[3];
		
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
		
		for (int i = 1; i <= Apollo.lobbies.length; i++) {
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
		
		if (Apollo.lobbies.length == 0) {
			textRenderer.text("Fetching lobby statuses...", 10, (int) camera.viewportHeight - 10 - 8, Color.YELLOW);
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
