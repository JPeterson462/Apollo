package net.digiturtle.apollo.graphics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.digiturtle.apollo.Vector2;

public class DebugRenderer {
	
	private static class Line {
		Vector2 start, end;
	}
	
	private static ArrayList<Line> lines = new ArrayList<>();
	private static ShapeRenderer shapeRenderer;
	
	public static void addLine (Vector2 start, Vector2 end) {
		Line line = new Line();
		line.start = start;
		line.end = end;
		lines.add(line);
	}
	
	public static void create () {
		//lines = new ArrayList<>();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.setAutoShapeType(true);
	}
	
	public static void setColor (Color color) {
		shapeRenderer.setColor(color);
	}
	
	public static void render (Camera camera) {
		shapeRenderer.begin();
		shapeRenderer.setProjectionMatrix(camera.combined);
		for (Line line : lines) {
			shapeRenderer.line(line.start.x, line.start.y, line.end.x, line.end.y);
		}
		shapeRenderer.end();
	}

}
