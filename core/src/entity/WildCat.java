package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import main.Animator;
import main.FrameEngine;

public class WildCat extends Critter {
	
	private final ArrayList<Animation<TextureRegion>> watch = 
			Animator.createAnimation(45, "sprites/critters/cat.png", 2, 1);
	private final ArrayList<Animation<TextureRegion>> hide = 
			Animator.createAnimation(30, "sprites/critters/cat_hide.png", 2, 1, PlayMode.NORMAL);
	private final Rectangle noticeBox = new Rectangle(
			0,
			0,
			FrameEngine.TILE * 5,
			FrameEngine.TILE * 5
		);

	public WildCat(float x, float y) {
		super(x, y);
		reactTimer.setEndTime(30);
		collides = false;
		shadow = null;
	}
	
	@Override
	public void update(){
		if (!touchingPlayer(getNoticeBox()) && reactTimer.timeUp()){
			reacted = false;
		}
		super.update();
	}
	
	@Override
	public void updateImage(){
		if (reacted) {
			image = hide.get(0).getKeyFrame(reactTimer.getCounter());
		}
		else{
			image = watch.get(0).getKeyFrame(FrameEngine.getTime());
		}
	}

	@Override
	protected void react() {
		if (!FrameEngine.getSaveFile().getFlag("FOUND_GOAL") && reactTimer.timeUp()){
			reactTimer.reset();
			reacted = true;
		}

	}

	@Override
	public Rectangle getNoticeBox() {
		return noticeBox;
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(watch);
		Animator.freeAnimation(hide);
	}

}
