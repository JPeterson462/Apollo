package net.digiturtle.apollo.screens;

public class LoginScreen extends Screen {

	@Override
	public void create () {
		
	}

	@Override
	public void render () {
		Screen.set(ScreenId.LOBBY);
		
		System.out.println("LoginScreen");
	}

	@Override
	public void onPacket (Object object) {
		
	}

}
