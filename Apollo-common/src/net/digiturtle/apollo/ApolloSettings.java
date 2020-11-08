package net.digiturtle.apollo;

import java.util.HashMap;
import java.util.Map;

import net.digiturtle.apollo.match.Resource;

public interface ApolloSettings {
	
	public static final int MAX_UDP_PACKET = 16 * 1024;
	
	public static final String VERSION = "v0.6.0a";
	
	public static final String FONT_FACE = "PixelParty";
	//public static final String FONT_FACE = "arial-15";
	
	public static final int MAX_BULLET_DISTANCE = 1024;
	public static final int EXPLOSION_DISTANCE = 32;
	public static final float EXPLOSION_TIME = 0.4f;
	public static final int EXPLOSION_POWER = 60;
	public static final float EXPLOSION_TIME_TO_HIT = 1f;
	public static final int EXPLOSION_PATH_INTERVALS = 6;
	
	public static final int TILE_SIZE = 256;
	
	public static final int CHARACTER_SIZE = 32;
	
	public static final int PLAYER_HEALTH = 100;
	public static final int BULLET_DAMAGE = 20;
	public static final int MAX_PLAYER_SPEED = 110;
	public static final int PLAYER_SPEED = 60;
	
	public static final int DROPPED_BACKPACK_SIZE = 16;
	
	public static final float EXPLOSIVE_THROW_DELAY = 0.3f;
	
	public static final Rectangle HEALTH_BAR_BOUNDS = new Rectangle(30, 17, 69, 4);
	
	public static final int PLAYER_FRAME_DOWN = 0, PLAYER_FRAME_DOWN_RIGHT = 1, PLAYER_FRAME_RIGHT = 2,
							PLAYER_FRAME_UP_RIGHT = 3, PLAYER_FRAME_UP = 4, PLAYER_FRAME_UP_LEFT = 5,
							PLAYER_FRAME_LEFT = 6, PLAYER_FRAME_DOWN_LEFT = 7;
	public static final int PLAYER_STANDING_FRAME = 0;
	public static final int PLAYER_WALKING_FRAME = 1, PLAYER_WALKING_FRAME_COUNT = 4;
	public static final int PLAYER_THROWING_FRAME = 0, PLAYER_THROWING_FRAME_COUNT = 8;
	public static final int[][] PLAYER_FRAMES = new int[][] {
		// Standing
		{ 0,0,32,32, 32,0,32,32, 64,0,32,32, 96,0,32,32, 128,0,32,32, 160,0,32,32, 192,0,32,32, 224,0,32,32 },
		// Walking
		{ 0,32,32,32, 32,32,32,32, 64,32,32,32, 96,32,32,32, 128,32,32,32, 160,32,32,32, 192,32,32,32, 224,32,32,32 },
		{ 0,64,32,32, 32,64,32,32, 64,64,32,32, 96,64,32,32, 128,64,32,32, 160,64,32,32, 192,64,32,32, 224,64,32,32 },
		{ 0,96,32,32, 32,96,32,32, 64,96,32,32, 96,96,32,32, 128,96,32,32, 160,96,32,32, 192,96,32,32, 224,96,32,32 },
		{ 0,128,32,32, 32,128,32,32, 64,128,32,32, 96,128,32,32, 128,128,32,32, 160,128,32,32, 192,128,32,32, 224,128,32,32 },
		
	};
	public static final int[][] PLAYER_THROWING_FRAMES = new int[][] {
		{ 0,0,32,32, 32,0,32,32, 64,0,32,32, 96,0,32,32, 128,0,32,32, 160,0,32,32, 192,0,32,32, 224,0,32,32 },
		{ 0,32,32,32, 32,32,32,32, 64,32,32,32, 96,32,32,32, 128,32,32,32, 160,32,32,32, 192,32,32,32, 224,32,32,32 },
		{ 0,64,32,32, 32,64,32,32, 64,64,32,32, 96,64,32,32, 128,64,32,32, 160,64,32,32, 192,64,32,32, 224,64,32,32 },
		{ 0,96,32,32, 32,96,32,32, 64,96,32,32, 96,96,32,32, 128,96,32,32, 160,96,32,32, 192,96,32,32, 224,96,32,32 },
		{ 0,128,32,32, 32,128,32,32, 64,128,32,32, 96,128,32,32, 128,128,32,32, 160,128,32,32, 192,128,32,32, 224,128,32,32 },
		{ 0,160,32,32, 32,160,32,32, 64,160,32,32, 96,160,32,32, 128,160,32,32, 160,160,32,32, 192,160,32,32, 224,160,32,32 },
		{ 0,192,32,32, 32,192,32,32, 64,192,32,32, 96,192,32,32, 128,192,32,32, 160,192,32,32, 192,192,32,32, 224,192,32,32 },
		{ 0,224,32,32, 32,224,32,32, 64,224,32,32, 96,224,32,32, 128,224,32,32, 160,224,32,32, 192,224,32,32, 224,224,32,32 },
	};
	
	public static int[] getFrame (int direction, int frameNumber, int[][] frames) {
		return new int[] {
			frames[frameNumber][direction * 4 + 0], frames[frameNumber][direction * 4 + 1], 
			frames[frameNumber][direction * 4 + 2], frames[frameNumber][direction * 4 + 3]
		};
	}
	
	public static final int[] RED_BAR_BOUNDS = new int[] {
		367, 3, 420-367, 4
	};
	public static final int[] BLUE_BAR_BOUNDS = new int[] {
		367, 10, 420-367, 4
	};
	public static final int[] GREEN_BAR_BOUNDS = new int[] {
		367, 17, 420-367, 4
	};
	
	public static final int[][] ARSENAL_BAR_BOUNDS = new int[][] {
		{ 0, 0, 4, 20 },
		{ 4, 0, 2, 10 }
	};
	public static final int SPEED_POWERUP_ARSENAL_SLOT = 0;
	public static final int DAMAGE_POWERUP_ARSENAL_SLOT = 1;
	public static final int RESILIENCE_POWERUP_ARSENAL_SLOT = 2;
	public static final int EXPLOSIVE_POWERUP_ARSENAL_SLOT = 3;
	public static final int SPEED_POWERUP_SLOT = 4;
	public static final int DAMAGE_POWERUP_SLOT = 5;
	public static final int RESILIENCE_POWERUP_SLOT = 6;
	public static final int COAL_BACKPACK_SLOT = 7;
	public static final int GOLD_BACKPACK_SLOT = 8;
	public static final int ELECTRONICS_BACKPACK_SLOT = 9;
	public static final int SHEET_METAL_BACKPACK_SLOT = 10;
	public static final int[][] ARSENAL_BOUNDS = new int[][] {
		// Powerup slots
		{ 6, 17, 27-6, 39-17 },
		{ 30, 17, 27-6, 39-17 },
		{ 54, 17, 27-6, 39-17 },
		{ 78, 17, 27-6, 39-17 },
		// Powerup status slots
		{ 202, 28 },
		{ 220, 28 },
		{ 238, 28 },
		// Backpack slots
		{ 344, 18 },
		{ 368, 18 },
		{ 392, 18 },
		{ 416, 18 }
	};
	
	// Powerups.png
	public static final int SPEED_POWERUP = 0, SPEED_POWERUP_COUNT = 4;
	public static final int DAMAGE_POWERUP = 1, DAMAGE_POWERUP_COUNT = 4;
	public static final int RESILIENCE_POWERUP = 2, RESILIENCE_POWERUP_COUNT = 4;
	public static final int EXPLOSIVE_POWERUP = 3, EXPLOSIVE_POWERUP_COUNT = 3;
	public static final int[][] POWERUP_BOUNDS = new int[][] {
		{ 0,0,21,22,  21,0,21,22,  42,0,21,22,  63,0,21,22 },
		{ 0,22,21,22,  21,22,21,22,  42,22,21,22,  63,22,21,22 },
		{ 0,44,21,22,  21,44,21,22,  42,44,21,22,  63,44,21,22 },
		{ 0,66,21,22,  21,66,21,22,  42,66,21,22 },
	};	
	public static final int[][] POWERUP_COSTS = new int[][] {
		{ 1200, 3000, 7500 },
		{ 1200, 3000, 7500 },
		{ 1200, 3000, 7500 },
		{ 2500, 6000 }
	};
	
	// Buttons1.png
	public static final int BUTTON_UPGRADE = 0;
	public static final int BUTTON_JOIN = 1;
	public static final int[][] BUTTON_BOUNDS = new int[][] {
		{ 0,0,50,11,  0,11,50,11,  0,22,50,11 },
		{ 50,0,11,11, 50,11,11,11,  50,22,11,11 }
	};
	
	// ShopBG.png
	public static final int PANEL4 = 0, PANEL3 = 1;
	public static final int[][] SHOP_PANEL_BOUNDS = new int[][] {
		{ 0,0,120,54, 35,36,   7,7,  35,7,  63,7,  91,7 },
		{ 120,0,120,54, 155,36,   141,7,  169,7,  197,7 }
	};
	
	public static int getResourceValue(HashMap<Resource, Integer> items) {
		int value = 0;
		for (Map.Entry<Resource, Integer> item : items.entrySet()) {
			value += item.getValue() * SharedSettings.getValue(item.getKey().name());
		}
		return value;
	}
	
}
