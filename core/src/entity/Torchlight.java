package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.FrameEngine;

public class Torchlight extends ImmobileEntity {
	
	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(15, "sprites/graphics/light_big.png", 2, 1);

	public Torchlight(float x, float y) {
		super(x, y);
		layer = Layer.LIGHT;
		collides = false;
		updateImage();
	}
	
	@Override
	public void updateImage(){
		image = anim.get(0).getKeyFrame(FrameEngine.getTime());
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(anim);
	}

}
