package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.FrameEngine;

public class Fire extends ImmobileEntity {
	
	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(30, "sprites/objects/fire.png", 2, 1);
	private final String id;

	public Fire(float x, float y, String id) {
		super(x, y);
		this.id = id;
		layer = Layer.LIGHT;
	}
	
	@Override
	public void updateImage(){
		image = FrameEngine.getSaveFile().getMapping(id).isEmpty() ? 
						null : anim.get(0).getKeyFrame(FrameEngine.getTime());
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(anim);
	}

}
