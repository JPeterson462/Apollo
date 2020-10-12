package net.digiturtle.apollo;

import java.util.HashMap;
import java.util.Map;

public class Backpack {
	
	private HashMap<Resource, Integer> items;
	
	public Backpack () {
		items = new HashMap<>();
		reset();
	}
	
	public void reset () {
		for (Resource resource : Resource.values()) {
			items.put(resource, 0);
		}
	}
	
	/** This is an UNCHECKED change */
	public void changeQuantity (Resource resource, int difference) {
		items.put(resource, items.get(resource) + difference);
	}
	
	public HashMap<Resource, Integer> getContents() {
		return items;
	}
	
	public void deposit (Backpack source) {
		for (Map.Entry<Resource, Integer> item : source.items.entrySet()) {
			changeQuantity(item.getKey(), item.getValue());
		}
	}

}
