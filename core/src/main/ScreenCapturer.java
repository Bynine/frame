package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Used to capture the screen.
 */
public class ScreenCapturer {

	/**
	 * Copies how the screen looks at this moment.
	 */
	public Texture takeSnapshot(){
		byte[] pixels = 
				ScreenUtils.getFrameBufferPixels(
						0, 0, 
						Gdx.graphics.getBackBufferWidth(), 
						Gdx.graphics.getBackBufferHeight(), 
						true);
		Pixmap pixmap = new Pixmap(
				Gdx.graphics.getBackBufferWidth(), 
				Gdx.graphics.getBackBufferHeight(), 
				Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
		Texture snapshot = new Texture(pixmap);
		return snapshot;
	}
}
