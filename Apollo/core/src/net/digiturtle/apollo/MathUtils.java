package net.digiturtle.apollo;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MathUtils {
	
	private static Matrix4 matrix = new Matrix4();
	
	public static float distanceSquared(Vector2 a, Vector2 b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		return dx * dx + dy * dy;
	}
	
	public static Vector2 mouseToMap(Vector2 mouse, float tileSize) {
		matrix.setToLookAt(new Vector3(1, 0, 1).nor(), new Vector3(0, 0, 1));
		Vector3 vector = new Vector3(mouse.x, mouse.y, 0);
		vector = vector.mul(matrix);
		return new Vector2(vector.x, vector.y);
	}
	
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
