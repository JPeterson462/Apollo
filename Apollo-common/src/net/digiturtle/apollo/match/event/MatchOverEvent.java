package net.digiturtle.apollo.match.event;

public class MatchOverEvent extends MatchEvent {
	
	private int[] scores;
	
	public MatchOverEvent () {
		
	}
	
	public MatchOverEvent (int[] scores) {
		this.scores = scores;
	}
	
	public int[] getScores () {
		return scores;
	}

}
