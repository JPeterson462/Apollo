package net.digiturtle.apollo;

import com.badlogic.gdx.graphics.Texture;

public enum Resource {
	
	COAL("CoalResourceRegionV1.png"),
	
	;
	
	private String regionTextureSrc;
	private Texture regionTexture;
	
	Resource (String regionTextureSrc) {
		this.regionTextureSrc = regionTextureSrc;
	}
	
	public Texture getRegionTexture () {
		return regionTexture;
	}
	
	public void create () {
		regionTexture = new Texture(regionTextureSrc);
	}

}
