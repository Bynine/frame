package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.FrameEngine;
import text.DialogueTree;

public class SaleSign extends InteractableEntity {

	private final ArrayList<Animation<TextureRegion>> sign = 
			Animator.createAnimation(10, "sprites/objects/sale_sign.png", 2, 1);

	public SaleSign(float x, float y) {
		super(x, y, "");
		image = sign.get(0).getKeyFrame(0);
		hitbox.setWidth(36);
		hitbox.setHeight(24);
	}

	@Override
	public void updateImage(){
		image = sign.get(0).getKeyFrame(FrameEngine.getTime());
	}

	@Override
	public void interact(){
		if (FrameEngine.getPlayer().getPosition().y > position.y){
			FrameEngine.startDialogueTree(new DialogueTree(this, "sale_sign_back"));
		}
		else{
			FrameEngine.startDialogueTree(new DialogueTree(this, "sale_sign"));
		}
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(sign);
	}

}
