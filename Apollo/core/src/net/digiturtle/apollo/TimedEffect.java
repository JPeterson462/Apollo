package net.digiturtle.apollo;

public abstract class TimedEffect implements IEffect {
	
	private float t, length;
	
	public TimedEffect () {
		t = 0;
	}
	
	public void setLength(float t) {
		length = t;
	}
	
	public void update (float dt) {
		t += dt;
	}
	
	public boolean isActive () {
		return t < length;
	}

}
