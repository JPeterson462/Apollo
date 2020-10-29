package net.digiturtle.apollo;

import java.util.function.Function;

public class RenderPath {
	
	private Vector2[] points;
	
	public RenderPath (Vector2[] points) {
		this.points = points;
	}
	
	// http://mathonline.wikidot.com/quadratic-lagrange-interpolating-polynomials
	
	private static void approximateIsometricHeight(Vector2 p, float height, Vector2 start, Vector2 end) {
		//FIXME implement
		p.x += height * (float)1/Math.sqrt(2);
		p.y += height * (float)1/Math.sqrt(2);
	}
	
	public static RenderPath createdProjectedArc(Vector2 start, float peak, Vector2 end, int steps) {
		Function<Float, Float> polynomial = MathUtils.createLagrangePolynomial(start.x, (start.x + end.x)/2, end.x, peak);
		Vector2[] rawPoints = new Vector2[steps + 1];
		for (int i = 0; i <= steps; i++) {
			rawPoints[i] = new Vector2(start.x + (end.x - start.x) * (float)i/steps, start.y + (end.y - start.y) * (float)i/steps);
			approximateIsometricHeight(rawPoints[i], polynomial.apply(rawPoints[i].x), start, end);
		}
		return new RenderPath(rawPoints);
	}
	
	public Vector2[] getPoints () {
		return points;
	}
	
	public Vector2 getPointAt (float t) {
		// t = .8, points.length = 4
		// index = 3
		// t -> .05/.25
		int segments = points.length - 1;
		int index = (int)(t * segments);
		t = (t - (index * 1f/segments)) * segments;
		Vector2 a = points[index < points.length ? index : (points.length - 1)], b = points[(index+1) < points.length ? (index + 1) : (points.length - 1)];
		return new Vector2(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y));
	}

}
