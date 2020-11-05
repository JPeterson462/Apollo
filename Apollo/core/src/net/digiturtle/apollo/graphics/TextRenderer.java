package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.digiturtle.apollo.ApolloSettings;

public class TextRenderer {
	
	private OrthographicCamera camera;
	private BitmapFont bitmapFont;
	private SpriteBatch spriteBatch;
	private boolean flipped;
	
	public TextRenderer(OrthographicCamera camera, boolean flipped) {
		this.camera = camera;
		this.flipped = flipped;
	}
	
	public void create () {
		//camera = new OrthographicCamera();
        //camera.setToOrtho(true, Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/3);
        //camera.update();
        bitmapFont = new BitmapFont(Gdx.files.internal(ApolloSettings.FONT_FACE + ".fnt"), Gdx.files.internal(ApolloSettings.FONT_FACE + ".png"), flipped);
        spriteBatch = new SpriteBatch();
    }
	
	public void begin () {
		spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
    }
	
	public float getTextWidth (String content) {
		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(bitmapFont, content);
		return glyphLayout.width;
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
