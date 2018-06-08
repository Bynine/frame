package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents where the cursor was clicked on screen, and whether it was this frame.
 */
public class MousePress {
	
	public final boolean this_frame;
	public final int screenX, screenY, pointer, button;

	public MousePress(boolean this_frame, int screenX, int screenY, int pointer, int button){
		this.this_frame = this_frame;
		this.screenX = screenX;
		this.screenY = screenY;
		this.pointer = pointer;
		this.button = button;
	}
	
	/**
	 * Checks to see if the mouse was clicked inside this specific zone.
	 */
	public boolean in_zone(Rectangle zone){
		return zone.contains(screenX, Gdx.graphics.getHeight() - screenY);
	}
}
