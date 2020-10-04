package net.digiturtle.apollo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RenderablePlayer {
	
	private Texture playerTexture;
	private TextureRegion[] playerBase;
	private int frame;
	
	public RenderablePlayer () {
        playerTexture = new Texture("EntityTemplate.png");
        playerBase = new TextureRegion[8];
        playerBase[0] = new TextureRegion(playerTexture, 0, 0, 32, 32);
        playerBase[1] = new TextureRegion(playerTexture, 32, 0, 32, 32);
        playerBase[2] = new TextureRegion(playerTexture, 64, 0, 32, 32);
        playerBase[7] = new TextureRegion(playerTexture, 0, 32, 32, 32);
        playerBase[3] = new TextureRegion(playerTexture, 64, 32, 32, 32);
        playerBase[6] = new TextureRegion(playerTexture, 0, 64, 32, 32);
        playerBase[5] = new TextureRegion(playerTexture, 32, 64, 32, 32);
        playerBase[4] = new TextureRegion(playerTexture, 64, 64, 32, 32);
	}
	
	public void setFrame (int frame) {
		this.frame = frame;
	}
	
	public int getFrame () {
		return frame;
	}
	
	public TextureRegion getCurrentTexture () {
		return playerBase[frame];
	}

}
