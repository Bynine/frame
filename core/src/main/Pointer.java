package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents the pointer location and activation status on this frame.
 */
public class Pointer {
	
	public final boolean this_frame;
	public final int screenX, screenY, pointer, button;

	public Pointer(boolean this_frame, int screenX, int screenY, int pointer, int button){
		this.this_frame = this_frame;
		this.screenX = screenX;
		this.screenY = screenY;
		this.pointer = pointer;
		this.button = button;
	}
	
	/**
	 * Checks to see if the pointer is in this specific zone.
	 */
	public boolean in_zone(Rectangle zone){
		return zone.contains(screenX, Gdx.graphics.getHeight() - screenY);
	}
}
