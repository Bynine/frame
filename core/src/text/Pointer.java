package text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import main.GraphicsHandler;

/**
 * Represents the pointer location and activation status on this frame.
 */
public class Pointer {
	
	public final boolean thisFrame;
	private final int screenX, screenY;

	public Pointer(boolean this_frame, int screenX, int screenY){
		this.thisFrame = this_frame;
		this.screenX = screenX;
		this.screenY = screenY;
	}
	
	/**
	 * Checks to see if the pointer is in this specific zone.
	 */
	public boolean inArea(Rectangle zone){
		return zone.contains(
				screenX * GraphicsHandler.ZOOM, 
				(Gdx.graphics.getHeight() - screenY) * GraphicsHandler.ZOOM
				);
	}
}
