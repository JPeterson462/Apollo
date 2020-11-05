package net.digiturtle.apollo.match.event;

public class UserLobbiedEvent extends UserEvent {
	
	private String ip;
	private int port;
	
	public UserLobbiedEvent () {
		
	}
	
	public UserLobbiedEvent (String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public String getIP () {
		return ip;
	}
	
	public int getPort () {
		return port;
	}

}
