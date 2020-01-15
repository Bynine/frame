package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import timer.DurationTimer;

public class SnowParticle extends ImmobileEntity {
	
	private final TextureRegion tex = new TextureRegion(new Texture("sprites/graphics/snow_particle.png"));
	private final DurationTimer life = new DurationTimer(75);
	private final float speed, zMod;

	public SnowParticle(float x, float y, int ii) {
		super(x, y);
		image = tex;
		collides = false;
		layer = Layer.OVERHEAD;
		timerList.add(life);
		speed = (float) (-1.5f + (3.25f * ii * Math.random()));
		zMod = (float) (0.8f + (0.2f * Math.random()));
	}
	
	@Override
	public void update() {
		super.update();
		position.x += speed * FrameEngine.elapsedTime;
		float dec = ((float)life.getCounter()/(float)life.getEndTime())*3.0f;
		zPosition += zMod * (2.5f * FrameEngine.elapsedTime) - dec;
		if (life.timeUp()) {
			this.setRemove();
		}
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
	}

}
