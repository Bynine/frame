package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import main.ProgressionHandler;

public class ShrineDoor extends InteractableEntity {
	
	private final boolean opened;
	private final TextureRegion closed = new TextureRegion(new Texture("sprites/objects/shrine_door.png"));

	public ShrineDoor(float x, float y) {
		super(x, y, 
				"This strange door is completely shut. It won't budge!"
				+ "\nSomething's written on it... \"Find the four effigies to open the way.\""
				);
		opened = FrameEngine.getSaveFile().getCounter(ProgressionHandler.foundStatuette) > 3;
		image = closed;
		if (opened) setOpenedState();
	}
	
	@Override
	public void updateImage(){
		if (opened) image = null;
	}
	
	private void setOpenedState(){
		hitbox.setSize(0);
		interactHitbox.setSize(0);
	}

	@Override
	public void dispose() {
		closed.getTexture().dispose();
	}

}
