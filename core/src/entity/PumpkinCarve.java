package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class PumpkinCarve extends InteractableEntity {
	
	private final TextureRegion normal = new TextureRegion(new Texture("sprites/objects/pumpkin.png"));
	private final TextureRegion carved = new TextureRegion(new Texture("sprites/objects/pumpkin_carved.png"));

	public PumpkinCarve(float x, float y) {
		super(x, y, "It appears to be a carving of your face."
				+ "You feel flattered, you think.");
		canInteract = FrameEngine.getSaveFile().getFlag("FOUND_GOAL");
	}
	
	@Override
	public void updateImage(){
		image = FrameEngine.getSaveFile().getFlag("FOUND_GOAL") ? carved : normal;
	}

	@Override
	public void dispose() {
		normal.getTexture().dispose();
		carved.getTexture().dispose();
	}

}
