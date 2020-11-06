package net.digiturtle.apollo.screens;

import net.digiturtle.apollo.Apollo;
import net.digiturtle.apollo.User;
import net.digiturtle.apollo.match.event.UserConnectedEvent;
import net.digiturtle.apollo.match.event.UserJoinEvent;

public class LoginScreen extends Screen {

	@Override
	public void create () {
		
	}

	@Override
	public void render () {
		//Screen.set(ScreenId.LOBBY);
		
		//System.out.println("LoginScreen");
		
		User user = new User();
		user.setProductKey("ABCD");
		Apollo.sendToMain(new UserJoinEvent(user));
	}
	
	public void onManagerPacket (Object object) {
		if (object instanceof UserConnectedEvent) {
			Apollo.user = ((UserConnectedEvent) object).getUser();
			Screen.set(ScreenId.LOBBY);
		}
	}

	@Override
	public void onPacket (Object object) {
		
	}

}
