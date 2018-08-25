package entity;

import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;
import timer.TimerDuration;

/**
 * Creates designated graphic at regular intervals.
 */
public class Emitter extends ImmobileEntity{
	
	private final TextureRegion graphicImage;
	private final TimerDuration intervalTimer;
	private final int duration;
	private final HashSet<Graphic> graphics = new HashSet<>();

	public Emitter(float x, float y, int interval, int duration, String graphicPath) {
		super(x, y);
		graphicImage = new TextureRegion(new Texture(Gdx.files.internal(
				"sprites/graphics/" + graphicPath + ".png"
				)));
		this.duration = duration;
		intervalTimer = new TimerDuration(interval);
		timerList.add(intervalTimer);
	}
	
	@Override
	public void update(){
		super.update();
		if (intervalTimer.timeUp()){
			intervalTimer.reset(-(int)(Math.random() * 15));
			graphics.add(new Graphic(this, duration));
		}
		Iterator<Graphic> graphicIter = graphics.iterator();
		while (graphicIter.hasNext()){
			Graphic graphic = graphicIter.next();
			graphic.life.countUp();
			graphic.position.x += 
					Math.cos( (FrameEngine.getTime() + graphic.life.getCounter())/20.0f) 
					* FrameEngine.elapsedTime;
			graphic.position.y += 1 * FrameEngine.elapsedTime;
			if (graphic.life.timeUp()){
				graphicIter.remove();
			}
		}
	}
	
	public HashSet<Graphic> getGraphics(){
		return graphics;
	}
	
	public TextureRegion getGraphicImage(){
		return graphicImage;
	}
	
	@Override
	public TextureRegion getImage(){
		return null;
	}

	@Override
	public void dispose() {
		graphicImage.getTexture().dispose();
	}
	
	/**
	 * Represents the position and lifetime of an emitted graphic.
	 */
	public class Graphic{
		private final Vector2 position = new Vector2();
		private final TimerDuration life;
		
		private Graphic(Emitter emitter, int lifetime){
			life = new TimerDuration(lifetime);
			position.set(emitter.getPosition());
		}
		public Vector2 getPosition(){
			return position;
		}
		public int getTimeLeft() {
			return life.getEndTime() - life.getCounter();
		}
	}

}
