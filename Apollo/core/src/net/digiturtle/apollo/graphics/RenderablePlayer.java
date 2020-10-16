package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.digiturtle.apollo.ApolloSettings;

public class RenderablePlayer {
	
	private Texture playerTexture;
	private TextureRegion[] playerBase;
	private int frame;
	
	public RenderablePlayer () {
        playerTexture = new Texture("PlayerV3.png");
        playerBase = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
        	int[] region = ApolloSettings.getFrame(i, 0, ApolloSettings.PLAYER_FRAMES);
        	playerBase[i] = new TextureRegion(playerTexture, region[0], region[1], region[2], region[3]);
        }
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
