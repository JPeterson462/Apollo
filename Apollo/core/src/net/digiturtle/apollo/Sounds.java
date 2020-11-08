package net.digiturtle.apollo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
	
	public static Sound grenade, pistol, end_level;
	
	public static void create () {
		grenade = Gdx.audio.newSound(Gdx.files.internal("audio/grenade.ogg"));
		pistol = Gdx.audio.newSound(Gdx.files.internal("audio/pistol.ogg"));
		end_level = Gdx.audio.newSound(Gdx.files.internal("audio/end_level.wav"));
	}

}
