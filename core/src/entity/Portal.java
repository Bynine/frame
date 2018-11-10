package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import main.AudioHandler;
import main.FrameEngine;

public class Portal extends ImmobileEntity {

	final String destArea;
	final Vector2 destLocation = new Vector2();
	final Direction direction;
	private final Sound close = Gdx.audio.newSound(Gdx.files.internal("sfx/door_open.wav"));
	private final boolean doorSound;

	public Portal(float x, float y, float width, float height, 
			String destination, double destX, double destY, Direction direction,
			boolean doorSound) {
		super(x, y);
		this.destArea = destination;
		this.direction = direction;
		destLocation.set(
				(int) (destX * FrameEngine.TILE), 
				(int) ( (destY + 1) * FrameEngine.TILE)
				);
		hitbox.setSize(width, height);
		this.doorSound = doorSound;
	}

	@Override
	public void update(){
		super.update();
		if (touchingPlayer(hitbox)){
			FrameEngine.initiateAreaChange(destArea, destLocation, direction);
			if (doorSound) AudioHandler.playSound(close);
		}
	}

	@Override
	public TextureRegion getImage(){
		return null;
	}

	@Override
	public void dispose() {
		close.dispose();
	}
	
	public enum Direction{
		ANY, UP, RIGHT, DOWN, LEFT
	}

}
