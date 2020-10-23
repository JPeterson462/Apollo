package net.digiturtle.apollo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.digiturtle.apollo.ApolloSettings;
import net.digiturtle.apollo.MathUtils;
import net.digiturtle.apollo.definitions.TeamDefinition;
import net.digiturtle.apollo.match.IRenderablePlayer;
import net.digiturtle.apollo.match.Player;

public class RenderablePlayer implements IRenderablePlayer {
	
	private Texture playerTexture;
	private TextureRegion[] playerBase;
	private TextureRegion[][] playerWalking;
	private int frame;
	
	private float t;
	private Player.State state;
	
	public RenderablePlayer (int color) {
		setTeam(color);
	}
	
	public void setTeam (int color) {
		String colorText = "";
		if (color == TeamDefinition.COLOR_BLUE) {
			colorText = "_Blue";
		}
		if (color == TeamDefinition.COLOR_RED) {
			colorText = "_Red";
		}
		if (color == TeamDefinition.COLOR_GREEN) {
			colorText = "_Green";
		}
		state = Player.State.STANDING;
        playerTexture = new Texture("PlayerV4" + colorText + ".png");
        playerBase = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
        	int[] region = ApolloSettings.getFrame(i, Player.State.STANDING.frame, ApolloSettings.PLAYER_FRAMES);
        	playerBase[i] = new TextureRegion(playerTexture, region[0], region[1], region[2], region[3]);
        }
        playerWalking = new TextureRegion[Player.State.WALKING.numFrames][8];
        for (int i = 0; i < Player.State.WALKING.numFrames; i++) {
        	playerWalking[i] = new TextureRegion[8];
        	for (int j = 0; j < 8; j++) {
            	int[] region = ApolloSettings.getFrame(j, Player.State.WALKING.frame + i, ApolloSettings.PLAYER_FRAMES);
        		playerWalking[i][j] = new TextureRegion(playerTexture, region[0], region[1], region[2], region[3]);
        	}
        }//FIXME walking down right looks weird
	}
	
	public void onStateChange (Player.State state) {
		t = 0;
		this.state = state;
	}
	
	public void update (float dt) {
		t += dt;
	}
	
	public void setFrame (int frame) {
		this.frame = frame;
	}
	
	public int getFrame () {
		return frame;
	}
	
	public TextureRegion getCurrentTexture () {
		switch (state) {
		case COLLECTING:
			return playerBase[0];//FIXME
		case STANDING:
			return playerBase[frame];
		case WALKING:
			float offset = MathUtils.floatMod(t, state.timePerFrame * state.numFrames);
			int frameOffset = (int) (offset / state.timePerFrame);
			return playerWalking[frameOffset][frame]; //FIXME
		}
		return null;
	}

}
