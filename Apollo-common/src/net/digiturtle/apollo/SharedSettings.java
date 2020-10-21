package net.digiturtle.apollo;

public class SharedSettings {
	
	public static int getValue(String resourceName) {
		if (resourceName.equalsIgnoreCase("COAL")) {
			return 1;
		}
		return 0;
	}

}
