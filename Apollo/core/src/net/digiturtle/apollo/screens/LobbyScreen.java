package net.digiturtle.apollo.screens;

public class LobbyScreen extends Screen {

	@Override
	public void create () {
		
	}

	@Override
	public void render () {
		Screen.set(ScreenId.MATCH_LOBBY);
		
		System.out.println("LobbyScreen");
	}

	@Override
	public void onPacket (Object object) {
		
	}

}
