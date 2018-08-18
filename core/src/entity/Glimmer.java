package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class Glimmer extends Entity {

	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(5, "sprites/secret.png", 2, 1);
	private final String flag;

	public Glimmer(float x, float y, String flag) {
		super(x, y);
		this.flag = flag;
		layer = Layer.FRONT;
	}

	@Override
	public void updateImage(){
		if (
				FrameEngine.getTime() % 60 >= 15 ||
				position.dst(FrameEngine.getPlayer().getPosition()) > FrameEngine.TILE * 5 ||
				FrameEngine.getSaveFile().getFlag(flag)
				) {
			image = null;
		}
		else {
			image = anim.get(0).getKeyFrame(FrameEngine.getTime());
		}
	}

	@Override
	public void dispose(){
		Animator.freeAnimation(anim);
	}

}
