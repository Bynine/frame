package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.FrameEngine;

public class Secret extends Currency {

	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(5, "sprites/items/secret.png", 2, 1);

	public Secret(float x, float y,  int amount, String flag) {
		super(x, y, amount, flag);
		image = null;
		hitbox.setSize(8);
		interactHitbox.setSize(16);
	}
	
	@Override
	protected String getString(String currency){
		return "[CURRENCY_" + amount + "]Neat! You found " + amount + " " + currency + "!";
	}

	@Override
	public void updateImage(){
		if (
				FrameEngine.getTime() % 60 >= 15 ||
				position.dst(FrameEngine.getPlayer().getPosition()) > FrameEngine.TILE * 4
				) image = null;
		else image = anim.get(0).getKeyFrame(FrameEngine.getTime());
	}

	@Override
	public void dispose(){
		Animator.freeAnimation(anim);
	}

}