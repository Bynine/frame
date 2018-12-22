package main.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import main.FrameEngine;

public class DesktopLauncher {
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		final int FPS = 60;
		config.foregroundFPS = FPS;
		config.backgroundFPS = FPS;
		config.width	= (int)FrameEngine.resolution.x;
		config.height	= (int)FrameEngine.resolution.y;
		config.vSyncEnabled = false;
		config.resizable = false;
		config.addIcon("sprites/icon16.png", FileType.Classpath);
		config.addIcon("sprites/icon32.png", FileType.Classpath);
		config.title = "Forest's Secret";
		new LwjglApplication(new FrameEngine(), config);
	}
	
}