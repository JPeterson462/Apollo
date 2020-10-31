package net.digiturtle.apollo;

public class SharedSettings {
	
	public static int getValue(String resourceName) {
		if (resourceName.equalsIgnoreCase("COAL")) {
			return 1;
		}
		if (resourceName.equalsIgnoreCase("SHEET_METAL")) {
			return 3;
		}
		if (resourceName.equalsIgnoreCase("ELECTRONICS")) {
			return 10;
		}
		return 0;
	}

}
