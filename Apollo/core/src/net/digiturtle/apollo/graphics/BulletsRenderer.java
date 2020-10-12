package net.digiturtle.apollo.graphics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.digiturtle.apollo.Bullet;

public class BulletsRenderer {
	
	private SpriteBatch entityBatch;
	private Texture testBullet;
	
	public void create () {
		entityBatch = new SpriteBatch();
		testBullet = new Texture("TestBullet.png");
	}
	
	public void render (Camera camera, ArrayList<Bullet> bullets, float tileSize) {
		//entityBatch.begin();
		//entityBatch.setProjectionMatrix(camera.combined);
		// FIXME TODO
		//entityBatch.end();
	}

}
