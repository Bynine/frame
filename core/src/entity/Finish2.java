package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class Finish2 extends InteractableEntity {
	
	boolean touched = false;

	public Finish2(float x, float y, int width, int height) {
		super(x, y, "");
		interactHitbox.setSize(0);
		hitbox.setSize(width, height);
		collides = false;
		canInteract = false;
	}

	@Override
	public void update(){
		super.update();
		if (touchingPlayer(hitbox)){
			if (!touched){
				FrameEngine.startDialogueTree(new DialogueTree(this, "finish2"));
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
			FrameEngine.getPlayer().walkUp(15);
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
