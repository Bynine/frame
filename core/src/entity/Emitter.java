package entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import main.Animator;
import main.FrameEngine;
import timer.DurationTimer;

/**
 * Creates designated graphic at regular intervals.
 */
public class Emitter extends ImmobileEntity{
	
	private final ArrayList<Animation<TextureRegion>> graphicAnimation;
	private final DurationTimer intervalTimer;
	private final int duration;
	private final HashSet<Graphic> graphics = new HashSet<>();
	private final String graphicName;

	public Emitter(float x, float y, int interval, int duration, String graphicPath) {
		super(x, y);
		graphicAnimation = 
				Animator.createAnimation(30, "sprites/graphics/" + graphicPath + ".png", 2, 1);
		graphicName = graphicPath;
		this.duration = duration;
		intervalTimer = new DurationTimer(interval);
		timerList.add(intervalTimer);
		intervalTimer.reset((int) (Math.random() * intervalTimer.getEndTime()));
	}
	
	@Override
	public void update(){
		super.update();
		if (intervalTimer.timeUp()){
			intervalTimer.reset(-(int)(Math.random() * intervalTimer.getEndTime())/5);
			graphics.add(new Graphic(this, duration));
		}
		Iterator<Graphic> graphicIter = graphics.iterator();
		while (graphicIter.hasNext()){
			Graphic graphic = graphicIter.next();
			graphic.life.countUp();
			if (graphicName.equals("smoke")){
				graphic.position.x += 
						Math.cos( (FrameEngine.getTime() + graphic.life.getCounter())/20.0f) 
						* 2 * Math.random() * FrameEngine.elapsedTime;
				graphic.position.y += 1 * FrameEngine.elapsedTime;
			}
			else if (graphicName.equalsIgnoreCase("leaf")){
				graphic.position.x -= 
						2 + (2 * Math.cos( (FrameEngine.getTime() + graphic.life.getCounter())/20.0f)) 
						* FrameEngine.elapsedTime * Math.random();
				graphic.position.y -= 0.75f * FrameEngine.elapsedTime;
			}
			if (graphic.life.timeUp()){
				graphicIter.remove();
			}
		}
	}
	
	public HashSet<Graphic> getGraphics(){
		return graphics;
	}
	
	public TextureRegion getGraphicImage(){
		return graphicAnimation.get(0).getKeyFrame(FrameEngine.getTime());
	}
	
	@Override
	public TextureRegion getImage(){
		return null;
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(graphicAnimation);
	}
	
	/**
	 * Represents the position and lifetime of an emitted graphic.
	 */
	public class Graphic{
		private final Vector2 position = new Vector2();
		private final DurationTimer life;
		
		private Graphic(Emitter emitter, int lifetime){
			life = new DurationTimer(lifetime);
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
