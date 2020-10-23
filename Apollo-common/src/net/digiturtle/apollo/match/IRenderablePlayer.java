package net.digiturtle.apollo.match;

public interface IRenderablePlayer {
	
	public void setFrame (int frame);
	
	public void onStateChange (Player.State state);
	
	public void setTeam (int team);
	
	public void update (float dt);

}
