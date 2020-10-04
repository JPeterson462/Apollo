package net.digiturtle.apollo;

import com.badlogic.gdx.math.Vector2;

public class MathUtils {
	
	// http://clintbellanger.net/articles/isometric_math/
	
	public static Vector2 screenToMap(Vector2 screen, float tileSize) {
		Vector2 map = new Vector2();
		float tileWidth = tileSize, tileHeight = tileSize * 0.5f;
		map.x = tileSize * (screen.x / tileWidth + screen.y / tileHeight) /2;
		map.y = tileSize * (screen.y / tileHeight - (screen.x / tileWidth)) /2;
		return map;
	}
	
	public static Vector2 mapToScreen(Vector2 map, float tileSize) {
		Vector2 screen = new Vector2();
		float tileWidth = tileSize, tileHeight = tileSize * 0.5f;
		screen.x = (map.x - map.y) * tileWidth / tileSize;
		screen.y = (map.x + map.y) * tileHeight / tileSize;
		return screen;
	}

}
