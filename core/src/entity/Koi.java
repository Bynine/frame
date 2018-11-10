package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.FrameEngine;

public class Koi extends ImmobileEntity {
	
	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(15, "sprites/critters/koi.png", 4, 1);
	private final int disp;

	public Koi(float x, float y) {
		super(x, y);
		layer = Layer.BACK;
		disp = (int) (Math.random() * 30);
	}
	
	@Override
	public void updateImage(){
		image = anim.get(0).getKeyFrame(disp + FrameEngine.getTime());
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(anim);
	}

}
