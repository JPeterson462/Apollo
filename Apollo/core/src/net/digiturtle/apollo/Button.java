package net.digiturtle.apollo;

public class Button {
	
	public static final int STATE_DEFAULT = 0, STATE_HOVER = 1, STATE_DOWN = 2;
	
	public String event;
	
	public Rectangle bounds;
	
	public Button(Rectangle bounds, String event) {
		this.bounds = bounds;
		this.event = event;
	}

}
