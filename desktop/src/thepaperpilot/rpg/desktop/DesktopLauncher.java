package thepaperpilot.rpg.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import thepaperpilot.rpg.Main;

class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "thepaperpilot's epic story game";
		new LwjglApplication(new Main(), config);
	}
}
