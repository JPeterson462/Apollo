package net.digiturtle.apollo;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MathUtils {
	
	public static String testRectanglePoint(Rectangle r, Vector2 p) {
		return Boolean.toString(r.x <= p.x) + ":" + 
				Boolean.toString(r.x + r.width >= p.x) + ":" +
				Boolean.toString(r.y <= p.y) + ":" +
				Boolean.toString(r.y + r.height >= p.y);
	}
	
	public static String rectangleToString(Rectangle r) {
		return "Rectangle { x: " + r.x + ", y: " + r.y + ", width: " + r.width + ", height: " + r.height + "}";
	}
	
	public static float distanceSquared(Vector2 a, Vector2 b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		return dx * dx + dy * dy;
	}
	
	// http://clintbellanger.net/articles/isometric_math/
	
	public static Vector2 screenToMap(Vector2 screen, float tileSize) {
		Vector2 map = new Vector2();
		float tileWidth = tileSize, tileHeight = tileSize * 0.5f;
		map.x = tileSize * (screen.x / tileWidth + screen.y / tileHeight) /2;
		map.y = tileSize * (screen.y / tileHeight - (screen.x / tileWidth)) /2;
		return map.scl(1f / 3f);//FIXME save 1/3 as a constant
	}
	
	public static Vector2 mapToScreen(Vector2 map, float tileSize) {
		Vector2 screen = new Vector2();
		float tileWidth = tileSize, tileHeight = tileSize * 0.5f;
		screen.x = (map.x - map.y) * tileWidth / tileSize;
		screen.y = (map.x + map.y) * tileHeight / tileSize;
		return screen;
	}

}
