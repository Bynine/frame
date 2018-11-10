package entity;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import main.Animator;
import main.AudioHandler;
import main.FrameEngine;
import timer.Timer;

public class Bird extends Critter {
	
	private final TextureRegion ground = new TextureRegion(new Texture("sprites/critters/bird.png"));
	private final TextureRegion hop = new TextureRegion(new Texture("sprites/critters/birdhop.png"));
	private final ArrayList<Animation<TextureRegion>> fly = 
			Animator.createAnimation(5, "sprites/critters/birdfly.png", 2, 1);
	private int flightDir = 0;
	private final Random random;
	private Timer wanderTimer = new Timer(45);
	private final Sound startled = Gdx.audio.newSound(Gdx.files.internal("sfx/bird_startled.wav"));
	
	private final Rectangle noticeBox = new Rectangle(
				0,
				0,
				FrameEngine.TILE * 6,
				FrameEngine.TILE * 6
			);
	private boolean flying = false;

	public Bird(float x, float y) {
		super(x, y);
		timerList.add(wanderTimer);
		friction = 0.6f;
		random = new Random((long) (Math.random()*100));
		wanderTimer.setEndTime(40 + random.nextInt(20));
	}
	
	@Override
	public void update(){
		super.update();
		if (flying){
			velocity.x += (flightDir * acceleration/1.5f) + (flightDir * 0.6) * FrameEngine.elapsedTime;
			zPosition += acceleration * Math.pow(reactTimer.getCounter(), 0.45) * FrameEngine.elapsedTime;
			if (zPosition > 48) layer = Layer.OVERHEAD;
		}
		else{
			if (wanderTimer.timeUp()){
				wanderTimer.reset();
				double rand = random.nextInt(3);
				float boost = 4.8f;
				if (rand == 1 || position.x < (boundary.x)) velocity.x = boost;
				else if (rand == 2 || position.x > (boundary.x + boundary.width)) velocity.x = -boost;
				else velocity.x = 0;
			}
		}
	}
	
	@Override
	public void updateImage(){
		if (flying) image = fly.get(0).getKeyFrame(FrameEngine.getTime());
		else if (velocity.x != 0) image = hop;
		else image = ground;
	}
	
	@Override
	protected void react() {
		flightDir = (int) Math.signum(position.x - FrameEngine.getPlayer().position.x);
		flying = true;
		collides = false;
		AudioHandler.playPositionalSound(this, startled);
	}

	@Override
	public Rectangle getNoticeBox() {
		return noticeBox;
	}
	
	@Override
	public void dispose() {
		ground.getTexture().dispose();
		hop.getTexture().dispose();
		startled.dispose();
		Animator.freeAnimation(fly);
	}

}
