package net.digiturtle.apollo;

public interface ApolloSettings {
	
	public static final String VERSION = "v0.2.0a";
	
	public static final int MAX_BULLET_DISTANCE = 1024;
	
	public static final int TILE_SIZE = 256;
	
	public static final int CHARACTER_SIZE = 32;
	
	public static final int PLAYER_HEALTH = 100;
	
	public static final int DROPPED_BACKPACK_SIZE = 16;
	
	public static final int PLAYER_FRAME_DOWN = 0, PLAYER_FRAME_DOWN_RIGHT = 1, PLAYER_FRAME_RIGHT = 2,
							PLAYER_FRAME_UP_RIGHT = 3, PLAYER_FRAME_UP = 4, PLAYER_FRAME_UP_LEFT = 5,
							PLAYER_FRAME_LEFT = 6, PLAYER_FRAME_DOWN_LEFT = 7;
	public static final int[][] PLAYER_FRAMES = new int[][] {
		{ 0,0,32,32, 32,0,32,32, 64,0,32,32, 96,0,32,32, 128,0,32,32, 160,0,32,32, 192,0,32,32, 224,0,32,32 }
	};
	
	public static int[] getFrame (int direction, int frameNumber, int[][] frames) {
		return new int[] {
			frames[frameNumber][direction * 4 + 0], frames[frameNumber][direction * 4 + 1], 
			frames[frameNumber][direction * 4 + 2], frames[frameNumber][direction * 4 + 3]
		};
	}

}
