package net.digiturtle.apollo.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.Button;
import net.digiturtle.apollo.ButtonInputController;
import net.digiturtle.apollo.Rectangle;
import net.digiturtle.apollo.User;
import net.digiturtle.apollo.graphics.TextRenderer;
import net.digiturtle.apollo.match.event.UserConnectedEvent;
import net.digiturtle.apollo.match.event.UserJoinEvent;

public class LoginScreen extends Screen {
	
	private TextRenderer textRenderer;
	private ShapeRenderer shapeRenderer;
	private int height, width;
	private OrthographicCamera camera;
	
	private ButtonInputController buttons;
	private int[] states;

	@Override
	public void create () {
		camera = createCamera();
		height = (int) camera.viewportHeight;
		width = (int) camera.viewportWidth;
		textRenderer = new TextRenderer(camera, false);
		textRenderer.create();
		shapeRenderer = new ShapeRenderer();
		states = new int[] {
				Button.STATE_DEFAULT
		};
		buttons = new ButtonInputController(new Button[] {
			new Button(new Rectangle(width - 10 - 60, height - 10 - 20, 60, 20), "Login")
		}, states, this::onButton);
	}
	
	private void onButton (String event) {
		if (event.equalsIgnoreCase("Login")) {
			User user = new User();
			user.setProductKey(Apollo.productKey);
			Apollo.sendToMain(new UserJoinEvent(user));
		}
	}

	@Override
	public void render () {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		switch (states[0]) {
		case Button.STATE_DEFAULT:
			shapeRenderer.setColor(0, .8f, 0, 1);
			break;
		case Button.STATE_HOVER:
			shapeRenderer.setColor(0, .65f, 0, 1);
			break;
		case Button.STATE_DOWN:
			shapeRenderer.setColor(0, .5f, 0, 1);
			break;
		}
		shapeRenderer.rect(width - 10 - 60, 10, 60, 20);
		
		shapeRenderer.end();
		
		textRenderer.begin();
		
		textRenderer.text("Controls:", 10, height - 10, Color.GOLDENROD);
		textRenderer.text("Number Keys 1-4 >> Enable powerups", 10, height - 10 - 16, Color.WHITE);
		textRenderer.text("WASD >> Movement controls", 10, height - 10 - 2*16, Color.WHITE);
		textRenderer.text("Spacebar >> Shoot in the direction of the mouse", 10, height - 10 - 3*16, Color.WHITE);
		textRenderer.text("Left mouse button >> Hold to collect resources\nin a marked region", 10, height - 10 - 4*16, Color.WHITE);
		
		textRenderer.text("Instructions:", 10, height - 10 - 6*16, Color.GOLDENROD);
		textRenderer.text("Upgrade powerups from the lobby screen or join a match that\nis 'In Lobby'", 10, height - 10 - 7*16, Color.WHITE);
		
		textRenderer.text("Product Key: " + Apollo.productKey, 10, 10 + 6, Color.GREEN);
		
		textRenderer.text("JOIN", width - 10 - 60 + (int)(60-textRenderer.getTextWidth("JOIN"))/2, 10 + 8 + 6, Color.BLACK);
		
		textRenderer.end();
		
		// button in the bottom right to play
		// print product key in the bottom left
		// prompt for the product key if not found
	}
	
	public void onManagerPacket (Object object) {
		if (object instanceof UserConnectedEvent) {
			Apollo.user = ((UserConnectedEvent) object).getUser();
			Apollo.userId = Apollo.user.getId();
			Screen.set(ScreenId.LOBBY);
		}
	}

	@Override
	public void onPacket (Object object) {
		
	}
	
	public InputProcessor getInputProcessor () {
		return buttons;
	}

}
