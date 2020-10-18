package net.digiturtle.apollo;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ResourceRegion {
	
	// Definition
	private Resource resource;
	private Rectangle bounds;
	private Vector2 position, size;
	private int capacity, quantity;
	private float regenerationRate, collectionRate;
	
	// State
	private float t;
	
	public ResourceRegion (Resource resource) {
		this.resource = resource;
		bounds = new Rectangle();
		t = 0;
	}
	
	public Resource getResource () {
		return resource;
	}
	
	public Rectangle getBounds () {
		bounds.set(position.x, position.y - size.y, size.x, size.y);
		return bounds;
	}

	public Vector2 getPosition () {
		return position;
	}

	public void setPosition (Vector2 position) {
		this.position = position;
	}

	public Vector2 getSize () {
		return size;
	}

	public void setSize (Vector2 size) {
		this.size = size;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getRegenerationRate() {
		return regenerationRate;
	}

	public void setRegenerationRate(float regenerationRate) {
		this.regenerationRate = regenerationRate;
	}

	public float getCollectionRate() {
		return collectionRate;
	}

	public void setCollectionRate(float collectionRate) {
		this.collectionRate = collectionRate;
	}

	public void update (float dt) {
		t += dt;
		int growth = (int) Math.floor(t / regenerationRate);
		t -= growth * regenerationRate;
		quantity = Math.min(capacity, quantity + growth);
	}
	
	public int collect (float t) {
		int taken = (int) Math.floor(t / collectionRate);
		taken = Math.min(taken, quantity);
		quantity -= taken;
		return taken;
	}

}
