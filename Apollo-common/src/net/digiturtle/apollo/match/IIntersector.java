package net.digiturtle.apollo.match;

import net.digiturtle.apollo.Vector2;

public interface IIntersector {
	
	public boolean intersectSegmentCircle (Vector2 start, Vector2 end, Vector2 center, float radius);

}
