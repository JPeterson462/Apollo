package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.digiturtle.apollo.ApolloSettings;

public class TextRenderer {
	
	private OrthographicCamera camera;
	private BitmapFont bitmapFont;
	private SpriteBatch spriteBatch;
	
	public void create () {
		camera = new OrthographicCamera();
        camera.setToOrtho(true, Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/3);
        camera.update();
        bitmapFont = new BitmapFont(Gdx.files.internal(ApolloSettings.FONT_FACE + ".fnt"), Gdx.files.internal(ApolloSettings.FONT_FACE + ".png"), true);
        spriteBatch = new SpriteBatch();
        camera.update();
	}
	
	public void begin () {
		spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
    }
	
	public void text (String content, int x, int y, Color color) {
	    spriteBatch.setColor(color);
		bitmapFont.setColor(color);
		bitmapFont.draw(spriteBatch, content, x, y);
	}
	
	public void end () {
		spriteBatch.end();
	}

}
