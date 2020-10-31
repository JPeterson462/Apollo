package net.digiturtle.apollo.match;

import java.util.HashMap;

public class Arsenal {
	
	public enum Powerup {
		RESILIENCE (4, 1, true, new float[] { 1.2f, 1.5f, 1.8f, 2.1f }),
		SPEED (4, 1, true, new float[] { 1.2f, 1.5f, 1.8f, 2.1f }),
		DAMAGE (4, 1, true, new float[] { 1.2f, 1.5f, 1.8f, 2.1f }),
		EXPLOSIVES (3, 3, false, null),
		;
		
		public final int levels, uses;
		public final boolean regenerates;
		public final float[] boosts;
		Powerup (int levels, int uses, boolean regenerates, float[] boosts) {
			this.levels = levels;
			this.uses = uses;
			this.regenerates = regenerates;
			this.boosts = boosts;
		}
	}
	
	public static class PowerupStatus {
		
		private int level, remaining;
		
		public PowerupStatus () {
			
		}
		
		public PowerupStatus (int level, Powerup powerup) {
			this.level = level;
			remaining = level;
		}

		public int getLevel () {
			return level;
		}

		public void setLevel (int level) {
			this.level = level;
		}

		public int getRemaining () {
			return remaining;
		}

		public void setRemaining (int remaining) {
			this.remaining = remaining;
		}
		
	}
	
	private HashMap<Powerup, PowerupStatus> statuses;
	
	public Arsenal () {
		statuses = new HashMap<>();
	}
	
	public HashMap<Powerup, PowerupStatus> getStatuses () {
		return statuses;
	}

}
