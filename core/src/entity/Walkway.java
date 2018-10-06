package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class Walkway extends ImmobileEntity {
	
	private boolean risen = false;
	private TextureRegion texture = new TextureRegion(new Texture("sprites/objects/walkway.png"));

	public Walkway(float x, float y, int width, int height) {
		super(x, y);
		hitbox.setSize(width, height);
		risen = checkRisen();
		collides = !risen;
		layer = Layer.BACK;
	}
	
	public static boolean checkRisen(){
		return checkFlowerGrown("SEED2") && checkFlowerGrown("SEED3") &&
				checkFlowerGrown("SEED4") && checkFlowerGrown("SEED5");
	}
	
	private static boolean checkFlowerGrown(String id){
		return !FrameEngine.getSaveFile().getMapping(Flower.flowerPrefix + id).isEmpty();
	}
	
	@Override
	public void updateImage(){
		image = risen ? texture : null;
	}

	@Override
	public void dispose() {
		texture.getTexture().dispose();
	}

}
