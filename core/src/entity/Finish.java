package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class Finish extends InteractableEntity {
	
	boolean touched = false;

	public Finish(float x, float y, int width, int height) {
		super(x, y, "");
		interactHitbox.setSize(0);
		hitbox.setSize(width, height);
		collides = false;
	}

	@Override
	public void update(){
		super.update();
		if (touchingPlayer(hitbox)){
			if (!touched){
				FrameEngine.startDialogueTree(new DialogueTree(this, "finish"));
			}
			touched = true;
		}
		else{
			touched = false;
		}
	}
	
	@Override
	public void getMessage(String message){
		super.getMessage(message);
		if (message.equals("FINISHGAME")){
			FrameEngine.startCredits();
		}
		if (message.equals("CONTINUE")){
			FrameEngine.getPlayer().walkRight(30);
		}
	}

	@Override
	public TextureRegion getImage(){
		return null;
	}

	@Override
	public void dispose() {
		/**/
	}

}
