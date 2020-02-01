package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.FrameEngine;
import text.DialogueTree;
import timer.Timer;

public class Egg extends InteractableEntity {
	
	private final TextureRegion stillTex = new TextureRegion(new Texture("sprites/objects/egg3.png"));
	private final ArrayList<Animation<TextureRegion>> 
		shakeAnim = Animator.createAnimation(15, "sprites/objects/egg3_shake.png", 2, 1);
	private Timer shakeTimer = new Timer(60);

	public Egg(float x, float y) {
		super(x, y, "");
		image = stillTex;
		timerList.add(shakeTimer);
		this.interactYDisp = -32;
		layer = Layer.OVERHEAD;
	}
	
	@Override
	public void interact() {
		shakeTimer.reset();
		FrameEngine.startDialogueTree(new DialogueTree(this, "egg"));
	}
	
	@Override
	public void updateImage() {
		if (!shakeTimer.timeUp()) {
			image = shakeAnim.get(0).getKeyFrame(FrameEngine.getTime());
		} else {
			image = stillTex;
		}
	}

	@Override
	public void dispose() {
		stillTex.getTexture().dispose();
		Animator.freeAnimation(shakeAnim);
	}

}
