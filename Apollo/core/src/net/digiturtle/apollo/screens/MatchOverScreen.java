package net.digiturtle.apollo.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.Button;
import net.digiturtle.apollo.ButtonInputController;
import net.digiturtle.apollo.definitions.TeamDefinition;
import net.digiturtle.apollo.graphics.TextRenderer;

public class MatchOverScreen extends Screen {
	
	private TextRenderer textRenderer;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private ButtonInputController buttons;
	private int[] states;

	@Override
	public void create () {
		states = new int[] {
			Button.STATE_DOWN,
		};
		camera = createCamera();
		shapeRenderer = new ShapeRenderer();
		textRenderer = new TextRenderer(camera, false);
		textRenderer.create();
		buttons = new ButtonInputController(new Button[] {
			new Button(new net.digiturtle.apollo.Rectangle((int) (camera.viewportWidth - 120)/2, camera.viewportHeight - 30 - 20, 128, 20), "BackToLobby")
		}, states, this::onEvent);
	}
	
	private void onEvent (String name) {
		if (name.equalsIgnoreCase("BackToLobby")) {
			Screen.set(ScreenId.LOBBY);
		}
	}

	@Override
	public void render () {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(camera.combined);

		boolean redTeamWon = Apollo.teamCounts[TeamDefinition.COLOR_RED] > Apollo.teamCounts[TeamDefinition.COLOR_BLUE];

		if (Apollo.numberOfTeams <= 2) {
			
			shapeRenderer.setColor(redTeamWon ? Color.RED : Color.BLUE);
			shapeRenderer.rect((camera.viewportWidth - 100)/2, camera.viewportHeight - 60 - 30, 100, 22);

			shapeRenderer.setColor(redTeamWon ? Color.BLUE : Color.RED);
			shapeRenderer.rect((camera.viewportWidth - 100)/2, camera.viewportHeight - 90 - 30, 100, 22);

		}
		
		Color buttonColor = new Color();
		switch (states[0]) {
		case Button.STATE_DEFAULT:
			buttonColor.set(.7f, .7f, .7f, 1);
			break;
		case Button.STATE_HOVER:
			buttonColor.set(.6f, .6f, .6f, 1);
			break;
		case Button.STATE_DOWN:
			buttonColor.set(.5f, .5f, .5f, 1);
			break;
		}
		
		shapeRenderer.setColor(buttonColor);
		shapeRenderer.rect((int) (camera.viewportWidth - 120)/2, 30, 120, 20);
		
		//shapeRenderer.setColor(Color.GREEN);
		//shapeRenderer.rect((camera.viewportWidth - 100)/2, camera.viewportHeight - 120, 100, 22);
		
		shapeRenderer.end();
	
		textRenderer.begin();
		
		textRenderer.text("CONTINUE", (int) (camera.viewportWidth - 120)/2 + (int)(120-textRenderer.getTextWidth("CONTINUE"))/2, 30 + 8 + 6, Color.BLACK);
		
		if (Apollo.numberOfTeams <= 2) {
			
			String title = Apollo.weWon ? "WINNER" : "LOSER";
			
			textRenderer.text(title, (int) (camera.viewportWidth - textRenderer.getTextWidth(title))/2, (int)camera.viewportHeight - 30, Color.YELLOW);
			
			String slot1 = Integer.toString(redTeamWon ? Apollo.teamCounts[TeamDefinition.COLOR_RED] : Apollo.teamCounts[TeamDefinition.COLOR_BLUE]);
			String slot2 = Integer.toString(redTeamWon ? Apollo.teamCounts[TeamDefinition.COLOR_BLUE] : Apollo.teamCounts[TeamDefinition.COLOR_RED]);
			
			textRenderer.text(slot1, (int) (camera.viewportWidth - 100)/2 + (int) (100-textRenderer.getTextWidth(slot1))/2, (int)camera.viewportHeight - 60 - 30 + 8 + 7, Color.WHITE);			
			textRenderer.text(slot2, (int) (camera.viewportWidth - 100)/2 + (int) (100-textRenderer.getTextWidth(slot2))/2, (int)camera.viewportHeight - 90 - 30 + 8 + 7, Color.WHITE);
			
		}
		
		//textRenderer.text("Count: " + Apollo.teamCounts[0] + " | " + Apollo.teamCounts[1] + " >> " + Apollo.numberOfTeams, 10, 10, Color.BLUE);
		
		textRenderer.end();
	}

	@Override
	public void onPacket (Object object) {
		
	}

	public InputProcessor getInputProcessor () {
		return buttons;
	}

}
