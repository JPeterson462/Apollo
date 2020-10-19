package net.digiturtle.apollo;

import com.badlogic.gdx.math.Rectangle;

public interface ApolloSettings {
	
	public static final String VERSION = "v0.3.0a";
	
	public static final String FONT_FACE = "PixelParty";
	//public static final String FONT_FACE = "arial-15";
	
	public static final int MAX_BULLET_DISTANCE = 1024;
	
	public static final int TILE_SIZE = 256;
	
	public static final int CHARACTER_SIZE = 32;
	
	public static final int PLAYER_HEALTH = 100;
	public static final int BULLET_DAMAGE = 20;
	public static final int PLAYER_SPEED = 256;
	
	public static final int DROPPED_BACKPACK_SIZE = 16;
	
	public static final Rectangle HEALTH_BAR_BOUNDS = new Rectangle(30, 17, 69, 4);
	
	public static final int PLAYER_FRAME_DOWN = 0, PLAYER_FRAME_DOWN_RIGHT = 1, PLAYER_FRAME_RIGHT = 2,
							PLAYER_FRAME_UP_RIGHT = 3, PLAYER_FRAME_UP = 4, PLAYER_FRAME_UP_LEFT = 5,
							PLAYER_FRAME_LEFT = 6, PLAYER_FRAME_DOWN_LEFT = 7;
	public static final int PLAYER_STANDING_FRAME = 0;
	public static final int PLAYER_WALKING_FRAME = 1, PLAYER_WALKING_FRAME_COUNT = 4;
	public static final int[][] PLAYER_FRAMES = new int[][] {
		// Standing
		{ 0,0,32,32, 32,0,32,32, 64,0,32,32, 96,0,32,32, 128,0,32,32, 160,0,32,32, 192,0,32,32, 224,0,32,32 },
		// Walking
		{ 0,32,32,32, 32,32,32,32, 64,32,32,32, 96,32,32,32, 128,32,32,32, 160,32,32,32, 192,32,32,32, 224,32,32,32 },
		{ 0,64,32,32, 32,64,32,32, 64,64,32,32, 96,64,32,32, 128,64,32,32, 160,64,32,32, 192,64,32,32, 224,64,32,32 },
		{ 0,96,32,32, 32,96,32,32, 64,96,32,32, 96,96,32,32, 128,96,32,32, 160,96,32,32, 192,96,32,32, 224,96,32,32 },
		{ 0,128,32,32, 32,128,32,32, 64,128,32,32, 96,128,32,32, 128,128,32,32, 160,128,32,32, 192,128,32,32, 224,128,32,32 },
		
	};
	
	public static int[] getFrame (int direction, int frameNumber, int[][] frames) {
		return new int[] {
			frames[frameNumber][direction * 4 + 0], frames[frameNumber][direction * 4 + 1], 
			frames[frameNumber][direction * 4 + 2], frames[frameNumber][direction * 4 + 3]
		};
	}

}
