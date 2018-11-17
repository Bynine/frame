package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import entity.Portal.Direction;
import main.AudioHandler;
import main.FrameEngine;

public class Door extends InteractableEntity {
	
	private final String destArea;
	private final Vector2 destLocation = new Vector2();
	private final Sound open = Gdx.audio.newSound(Gdx.files.internal("sfx/door_open.wav"));

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
		AudioHandler.playSoundVariedPitch(open);
		FrameEngine.initiateAreaChange(destArea, destLocation, Direction.UP);
	}

	@Override
	public void dispose() {
		open.dispose();
	}

}
