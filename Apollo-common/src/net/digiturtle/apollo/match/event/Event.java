package net.digiturtle.apollo.match.event;

public abstract class Event {
	
	private boolean remote;

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;
	}

}
