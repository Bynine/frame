package main.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import main.FrameEngine;

public class DesktopLauncher {
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;
		config.backgroundFPS = config.foregroundFPS;
		config.width	= (int)FrameEngine.resolution.x;
		config.height	= (int)FrameEngine.resolution.y;
		config.vSyncEnabled = false;
		config.title = "Marble";
		new LwjglApplication(new FrameEngine(), config);
	}
	
}