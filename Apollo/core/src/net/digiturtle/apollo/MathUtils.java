package net.digiturtle.apollo;

import java.util.Arrays;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MathUtils {
	
	public static String[] getClockTime(float t, int padding) {
		int seconds = (int) t;
		int minutes = (seconds - seconds % 60) / 60;
		seconds -= minutes * 60;
		return new String[] {
			pad(minutes, padding), pad(seconds, padding)
		};
	}
	private static String pad(int number, int digits) {
		int spaces = (number == 0 ? digits : digits - (int)Math.floor(Math.log10(number))) - 1;
		char[] zeros = new char[spaces];
		Arrays.fill(zeros, '0');
		return new String(zeros) + Integer.toString(number);
	}
	
	// https://www.geeksforgeeks.org/modulus-two-float-double-numbers/
	
	public static float floatMod(float a, float b) {
		// Handling negative values 
        if (a < 0) 
            a = -a; 
        if (b < 0) 
            b = -b; 
      
        // Finding mod by repeated subtraction 
        float mod = a; 
        while (mod >= b) 
            mod = mod - b; 
      
        // Sign of result typically depends 
        // on sign of a. 
        if (a < 0) 
            return -mod; 
      
        return mod;
	}
	
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
	
	public static boolean overlaps(Rectangle rectangle, Circle circle) {
		return circle.contains(rectangle.x, rectangle.y) || circle.contains(rectangle.x + rectangle.width, rectangle.y) || 
				circle.contains(rectangle.x, rectangle.y + rectangle.height) || circle.contains(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
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
