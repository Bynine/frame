package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import timer.DurationTimer;

public class Note extends Entity {
	
	private final TextureRegion note = new TextureRegion(new Texture("sprites/graphics/note.png"));
	private final DurationTimer life = new DurationTimer(20);

	public Note(float x, float y, int dur) {
		super(x, y);
		collides = false;
		layer = Layer.OVERHEAD;
		image = note;
		life.setEndTime(dur);
		timerList.add(life);
	}
	
	@Override
	public void update(){
		updateTimers();
		updateImage();
		velocity.x = (float) (FrameEngine.elapsedTime * Math.sin(FrameEngine.getTime()/10.0f));
		velocity.y = FrameEngine.elapsedTime * 0.75f;
		updatePosition();
		if (life.timeUp()){
			setRemove();
		}
	}

	@Override
	public void dispose() {
		note.getTexture().dispose();
	}

}
