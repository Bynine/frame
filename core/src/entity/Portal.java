package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;

public class Portal extends ImmobileEntity {

	final String destArea;
	final Vector2 destLocation = new Vector2();
	final Direction direction;

	public Portal(float x, float y, float width, float height, 
			String destination, double destX, double destY, Direction direction) {
		super(x, y);
		this.destArea = destination;
		this.direction = direction;
		destLocation.set(
				(int) (destX * FrameEngine.TILE), 
				(int) ( (destY + 1) * FrameEngine.TILE)
				);
		hitbox.setSize(width, height);
	}

	@Override
	public void update(){
		super.update();
		if (touchingPlayer(hitbox)){
			FrameEngine.initiateAreaChange(destArea, destLocation, direction);
		}
	}

	@Override
	public TextureRegion getImage(){
		return null;
	}

	@Override
	public void dispose() {
		/**/
	}
	
	public enum Direction{
		ANY, UP, RIGHT, DOWN, LEFT
	}

}
