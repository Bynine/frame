package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class Sky extends Entity {

	public Sky(float x, float y) {
		super(x, y);
		image = new TextureRegion(new Texture("sprites/graphics/sky.png"));
		layer = Layer.WAYBACK;
	}
	
	public void updatePosition() {
		position.y = FrameEngine.getPlayer().getPosition().y / 4.0f + (FrameEngine.TILE * 70);
	}

	@Override
	public void dispose() {
		image.getTexture().dispose();
	}

}
