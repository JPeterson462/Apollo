package net.digiturtle.apollo.match;

import net.digiturtle.apollo.Vector2;

public interface ITiledMapLoader {
	
	public ITiledMap load (String file);
	
	public IWorld createWorld (Vector2 gravity, boolean doSleep);

}
