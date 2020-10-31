package net.digiturtle.apollo.match;

import java.util.HashMap;
import java.util.Map;

public class Arsenal {
	
	public enum Powerup {
		RESILIENCE (4, 1, true, new float[] { 1.2f, 1.5f, 1.8f, 2.1f }, 30),
		SPEED (4, 1, true, new float[] { 1.2f, 1.5f, 1.8f, 2.1f }, 30),
		DAMAGE (4, 1, true, new float[] { 1.2f, 1.5f, 1.8f, 2.1f }, 30),
		EXPLOSIVES (3, 3, false, null, 0),
		;
		
		public final int levels, uses;
		public final boolean regenerates;
		public final float[] boosts;
		public final float time;
		Powerup (int levels, int uses, boolean regenerates, float[] boosts, float time) {
			this.levels = levels;
			this.uses = uses;
			this.regenerates = regenerates;
			this.boosts = boosts;
			this.time = time;
		}
	}
	
	public static class PowerupStatus {
		
		private int level, remaining;
		private boolean regenerates;
		private Powerup powerup;
		
		public PowerupStatus () {
			
		}
		
		public PowerupStatus (int level, Powerup powerup) {
			this.level = level;
			remaining = powerup.uses;
			regenerates = powerup.regenerates;
			this.powerup = powerup;
		}
		
		public PowerupStatus cloneAndUse () {
			PowerupStatus clone = new PowerupStatus(level, powerup);
			clone.remaining--;
			return clone;
		}

		public void tryReset () {
			if (regenerates) {
				remaining = level;
			}
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
	
	public void tryReset () {
		for (Map.Entry<Powerup, PowerupStatus> entry : statuses.entrySet()) {
			entry.getValue().tryReset();
		}
	}
	
	public HashMap<Powerup, PowerupStatus> getStatuses () {
		return statuses;
	}

}
