package entity;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import main.Animator;
import main.FrameEngine;
import timer.Timer;

public class Fish extends Critter {

	private final Rectangle noticeBox = new Rectangle(0, 0, 96, 96);
	private Timer wanderTimer = new Timer(60);
	private final Random random;

	private final ArrayList<Animation<TextureRegion>> anim;
	private final int disp;
	private final float boost;

	public enum Species{
		SMALL, MEDIUM, LARGE, KOI, GIANT
	}

	/**
	 * Yeah, I get lazy at the end of the development cycle, so what?
	 */
	public Fish(float x, float y, Species species) {
		super(x, y);
		layer = Layer.WAYBACK;
		disp = (int) (Math.random() * 30);
		shadow = null;
		random = new Random((long) (Math.random()*100));
		timerList.add(wanderTimer);
		switch (species){
		case SMALL: {
			anim = Animator.createAnimation(30, "sprites/critters/fishsmall.png", 2, 1);
			wanderTimer.setEndTime(30 + random.nextInt(20));
			boost = 3.6f;
			friction = 0.82f;
			break;
		}
		case MEDIUM: {
			anim = Animator.createAnimation(45, "sprites/critters/fish.png", 2, 1);
			wanderTimer.setEndTime(45 + random.nextInt(25));
			boost = 2.4f;
			friction = 0.88f;
			break;
		}
		case LARGE: {
			anim = Animator.createAnimation(60, "sprites/critters/fishbig.png", 2, 1);
			wanderTimer.setEndTime(69 + random.nextInt(30));
			boost = 1.8f;
			friction = 0.94f;
			break;
		}
		case KOI: {
			anim = Animator.createAnimation(60, "sprites/critters/koi2.png", 2, 1);
			wanderTimer.setEndTime(100 + random.nextInt(20));
			boost = 1.3f;
			friction = 0.96f;
			break;
		}
		case GIANT: {
			anim = Animator.createAnimation(90, "sprites/critters/fishgiant.png", 2, 1);
			wanderTimer.setEndTime(180 + random.nextInt(30));
			boost = 1.1f;
			friction = 0.992f;
			break;
		}
		default: {
			FrameEngine.logger.warning("Made a fish that somehow isn't any species");
			anim = Animator.createAnimation(45, "sprites/critters/fish.png", 2, 1);
			boost = -2;
			break;
		}
		}
		reactTimer.setEndTime(60);
	}

	@Override
	public void update(){
		super.update();
		if (wanderTimer.timeUp()){
			wanderTimer.reset();
			double rand = random.nextInt(3);
			if (rand == 1 || position.x < (boundary.x)) velocity.x = boost;
			else if (rand == 2 || position.x > (boundary.x + boundary.width)) velocity.x = -boost;
		}
	}

	@Override
	public void updateImage(){
		image = anim.get(0).getKeyFrame(disp + reactTimer.getCounter());
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(anim);
	}

	@Override
	protected void react() {
		velocity.x = 1.5f * boost * Math.signum(position.x - FrameEngine.getPlayer().position.x);
		wanderTimer.reset();
	}

	@Override
	public Rectangle getNoticeBox() {
		return noticeBox;
	}

}
