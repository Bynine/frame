package entity;

import java.util.ArrayList;

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
		ArrayList<String> grownSeeds = new ArrayList<>();
		for (int ii = 1; ii <= 5; ++ii){
			grownSeeds.add(FrameEngine.getSaveFile().getMapping(Flower.flowerPrefix + "FLOWER" + ii));
		}
		if (grownSeeds.contains("SEED2") && grownSeeds.contains("SEED3")
				&& grownSeeds.contains("SEED4") && grownSeeds.contains("SEED5")){
			return true;
		}
		return false;
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
