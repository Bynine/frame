package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.Textbox;

public class Secret extends Item {

	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(5, "sprites/items/secret.png", 2, 1);

	public Secret(float x, float y, String id, String flag) {
		super(x, y, id, flag);
		image = null;
		hitbox.setSize(8);
		interactHitbox.setSize(16); // TODO: Reset
	}

	@Override
	public void interact() {
		setDelete();
		FrameEngine.putTextbox(
				new Textbox("Ooh, you found " + getIndefinite(name) + " " + name + " hidden in the tree!")
				);
		FrameEngine.getInventory().addItem(id);
		FrameEngine.getSaveFile().setFlag(flag, true);
	}

	@Override
	public void updateImage(){
		if (
				FrameEngine.getTime() % 60 >= 15 ||
				position.dst(FrameEngine.getPlayer().getPosition()) > FrameEngine.TILE * 5
				) image = null;
		else image = anim.get(0).getKeyFrame(FrameEngine.getTime());
	}

	@Override
	public void dispose(){
		Animator.freeAnimation(anim);
	}

}