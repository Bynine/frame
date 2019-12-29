package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class SummonObject extends ImmobileEntity {
	
	private final TextureRegion texture;
	private final String flag;

	public SummonObject(float x, float y, String imagePath, String flag, String layer) {
		super(x, y);
		texture = new TextureRegion(new Texture("sprites/objects/" + imagePath + ".png"));
		this.flag = flag;
		this.layer = layer == "" ? Layer.FRONT : Layer.valueOf(layer);
	}
	
	@Override
	public void updateImage(){
		image = FrameEngine.getSaveFile().getFlag(flag) ? texture : null;
	}

	@Override
	public void dispose() {
		texture.getTexture().dispose();
	}

}
