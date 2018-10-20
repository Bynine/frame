package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import main.AudioHandler;
import main.FrameEngine;
import timer.DurationTimer;

public class Toss extends Item {
	
	private final DurationTimer duration;
	private final Vector2 startPoint, endPoint;
	private final Sound whistle = Gdx.audio.newSound(Gdx.files.internal("sfx/toss.wav"));
	
	public Toss(float x, float y, String id, int dur, Vector2 endPoint){
		super(x, y, id, "");
		endPoint = new Vector2(endPoint.x, endPoint.y - FrameEngine.TILE/2);
		AudioHandler.playPositionalSound(this, whistle);
		startPoint = new Vector2(x, y + image.getRegionHeight()/2);
		this.endPoint = endPoint;
		duration = new DurationTimer(dur);
		timerList.add(duration);
		collides = false;
		layer = Layer.FRONT;
	}
	
	public void update(){
		super.update();
		position.x = ((1-getDist()) * startPoint.x) + ((getDist()) * endPoint.x);
		position.y = ((1-getDist()) * startPoint.y) + ((getDist()) * endPoint.y);
		zPosition = (float) ((Math.sin(1-getDist()) * FrameEngine.TILE) + (Math.sin(getDist()) * FrameEngine.TILE));
		if (duration.timeUp()){
			setRemove();
		}
	}
	
	private float getDist(){
		return (float) duration.getCounter() / (float) duration.getEndTime();
	}

	@Override
	public void dispose() {
		image.getTexture().dispose();
		whistle.dispose();
	}

}
