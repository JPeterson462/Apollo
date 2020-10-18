package net.digiturtle.apollo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum Resource {
	
	COAL("CoalResourceRegionV2.png", 256, 128, 7),
	
	;
	
	private String regionTextureSrc;
	private Texture regionTexture;
	private int width, height, states;
	private TextureRegion[] regions;
	
	Resource (String regionTextureSrc, int width, int height, int states) {
		this.regionTextureSrc = regionTextureSrc;
		this.width = width;
		this.height = height;
		this.states = states;
	}
	
	public int getNumberOfStates () {
		return states;
	}
	
	public TextureRegion getRegionTexture (int state) {
		return regions[state];
	}
	
	public void create () {
		regionTexture = new Texture(regionTextureSrc);
		regions = new TextureRegion[states];
		for (int i = 0; i < states; i++) {
			regions[i] = new TextureRegion(regionTexture, i * width, 0, width, height);
		}
	}

}
