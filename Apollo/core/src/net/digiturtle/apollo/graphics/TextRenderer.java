package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.digiturtle.apollo.ApolloSettings;

public class TextRenderer {
	
	private OrthographicCamera camera;
	private BitmapFont bitmapFont;
	private SpriteBatch spriteBatch;
	private Texture _testFont;
	
	public void create () {
		camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/3);
        //camera.zoom = 1f/3f;
        camera.update();
        bitmapFont = new BitmapFont(Gdx.files.internal(ApolloSettings.FONT_FACE + ".fnt"));
        spriteBatch = new SpriteBatch();
        //spriteBatch.setColor(Color.BLACK);
        //bitmapFont.setColor(Color.BLACK);
       // _testFont = new Texture(ApolloSettings.FONT_FACE + ".png");

        camera.update();
	}
	
	public void text (String content, int x, int y) {
		spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(Color.YELLOW);
		bitmapFont.setColor(Color.YELLOW);
		bitmapFont.draw(spriteBatch, content, x, y);
		
		//spriteBatch.draw(_testFont, 0, 0);
		
		spriteBatch.end();
	}

}
