package net.digiturtle.apollo.match.event;

import java.util.HashMap;
import java.util.UUID;

public class MatchResultEvent extends Event {
	
	private HashMap<UUID, Integer> points;
	
	public int[] teamCounts;
	
	public int teams;
	
	public MatchResultEvent () {
		
	}
	
	public HashMap<UUID, Integer> getPoints () {
		return points;
	}
	
	public void setPoints (HashMap<UUID, Integer> points) {
		this.points = points;
	}

}
