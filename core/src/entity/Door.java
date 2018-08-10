package entity;

import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;

public class Door extends InteractableEntity {
	
	private final String destArea;
	private final Vector2 destLocation = new Vector2();

	public Door(float x, float y, String destArea, double destX, double destY) {
		super(x, y, "");
		image = null;
		destLocation.set(
				(int) (destX * FrameEngine.TILE), 
				(int) ( (destY + 1) * FrameEngine.TILE)
				);
		this.destArea = destArea;
		hitbox.setSize(0);
	}
	
	public void interact() {
		FrameEngine.initiateAreaChange(destArea, destLocation);
	}

	@Override
	public void dispose() {
		
	}

}
