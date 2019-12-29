package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import entity.Portal.Direction;
import main.Animator;
import main.FrameEngine;
import text.DialogueTree;

public class Canoe extends InteractableEntity {
	
	private final ArrayList<Animation<TextureRegion>> 
	anim = Animator.createAnimation(30, "sprites/objects/canoe.png", 2, 1);
	final String destArea;
	final Vector2 destLocation = new Vector2();
	private final Direction direction;

	public Canoe(float x, float y, String destination, double destX, double destY, Direction direction) {
		super(x, y, "");
		this.destArea = destination;
		this.direction = direction;
		destLocation.set(
				(int) (destX * FrameEngine.TILE), 
				(int) ( (destY + 1) * FrameEngine.TILE)
				);
		interactHitbox.setSize(64, 32);
		hitbox.setSize(64, 32);
		zPosition = -8;
	}
	
	public void interact(){
		FrameEngine.startDialogueTree(new DialogueTree(this, "canoe"));
	}
	
	@Override
	public void getMessage(String message){
		if (message.equals("ENTER")){
			FrameEngine.endDialogueTree();
			FrameEngine.initiateAreaChange(destArea, destLocation, direction);
		}
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
