package net.digiturtle.apollo;

import com.badlogic.gdx.math.Vector2;

public class RenderPath {
	
	private Vector2[] points;
	
	public RenderPath (Vector2[] points) {
		this.points = points;
	}
	
	// http://mathonline.wikidot.com/quadratic-lagrange-interpolating-polynomials
	
	public static RenderPath createdProjectedArc(Vector2 start, float peak, Vector2 end, int steps) {
		Vector2 mid = new Vector2((start.x + end.x) * .5f, (start.y + end.y) * .5f);
		Vector2[] rawPoints = new Vector2[steps + 1];
		float[] rawHeights = new float[steps + 1];
		//FIXME
		return null;
	}
	
	public Vector2 getPointAt (float t) {
		// t = .8, points.length = 4
		// index = 3
		// t -> .05/.25
		int segments = points.length - 1;
		int index = (int)(t * segments);
		t = (t - (index * 1f/segments)) * segments;
		Vector2 a = points[index], b = points[index + 1];
		return new Vector2(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y));
	}

}
