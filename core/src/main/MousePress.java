package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class MousePress {
	
	public final boolean active;
	public final int screenX, screenY, pointer, button;

	public MousePress(boolean active, int screenX, int screenY, int pointer, int button){
		this.active = active;
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
