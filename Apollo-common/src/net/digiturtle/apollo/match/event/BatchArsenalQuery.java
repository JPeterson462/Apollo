package net.digiturtle.apollo.match.event;

import java.util.HashMap;
import java.util.UUID;

import net.digiturtle.apollo.match.Arsenal;

public class BatchArsenalQuery {

	public static class Request {
		
		public UUID[] ids;
		
	}
	
	public static class Response {
		
		public HashMap<UUID, Arsenal> arsenals;
		
	}
	
}
