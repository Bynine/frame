package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import timer.Timer;

public class Firefly extends Entity {

	private final TextureRegion tex = new TextureRegion(new Texture("sprites/critters/firefly.png"));
	private final float fric = 0.99f;
	private final Timer dartTimer = new Timer(30);
	
	public Firefly(float x, float y) {
		super(x, y);
		image = tex;
		layer = Layer.OVERHEAD;
		zPosition = FrameEngine.TILE * 4;
		collides = false;
		timerList.add(dartTimer);
	}

	@Override
	public void updateVelocity() {
		if (dartTimer.timeUp()) {
			dartTimer.reset();
			if (Math.random() > 0.5f) {
				velocity.x += getBoost();
				velocity.y += getBoost();
			}
		}
	}
	
	@Override
	protected float getFriction() {
		return fric;
	}
	
	private float getBoost() {
		return (float) (0.5f - Math.random())*5.0f;
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
	}

}
