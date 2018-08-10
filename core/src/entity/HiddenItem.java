package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.Textbox;

public class HiddenItem extends Item {

	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(5, "sprites/secret.png", 2, 1);

	public HiddenItem(float x, float y, String id) {
		super(x, y, id);
		image = null;
		hitbox.setSize(8, 8);
		interactHitbox.setSize(16, 16);
	}

	@Override
	public void interact() {
		setDelete();
		FrameEngine.putTextbox(
				new Textbox("Ooh, you found " + getIndefinite(name) + " " + name + " hidden in the tree!")
				);
		FrameEngine.getSaveFile().addItem(id);
	}

	@Override
	public void updateImage(){
		if (
				FrameEngine.getTime() % 90 >= 15 ||
				position.dst(FrameEngine.getPlayer().getPosition()) > FrameEngine.TILE * 5
				) image = null;
		else image = anim.get(0).getKeyFrame(FrameEngine.getTime());
	}

	@Override
	public void dispose(){
		Animator.freeAnimation(anim);
	}

}