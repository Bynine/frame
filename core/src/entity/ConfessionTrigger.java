package entity;

import com.badlogic.gdx.math.Vector2;

import entity.Portal.Direction;
import main.FrameEngine;
import main.GraphicsHandler;
import text.DialogueTree;

public class ConfessionTrigger extends InteractableEntity {
	
	private boolean panMode = false;
	private boolean activated = false;

	public ConfessionTrigger(float x, float y, int w, int h) {
		super(x, y, "");
		canInteract = false;
		image = null;
		collides = false;
		hitbox.setSize(w, h);
	}
	
	@Override
	public void update(){
		super.update();
		if (shouldActivate()){
			FrameEngine.startDialogueTree(new DialogueTree(this, "leslie_confession"));
			activated = true;
		}
		if (panMode) {
			GraphicsHandler.setOffset(new Vector2(FrameEngine.TILE * 11, FrameEngine.TILE * - 4));
		}
	}
	
	@Override
	public void getMessage(String msg) {
		super.getMessage(msg);
		if (msg.equals("CAMERAPAN")) {
			panMode = true;
		}
		else if (msg.equals("CAMERAPANBACK")) {
			panMode = false;
		}
		else if (msg.equals("PARTY!")) {
			FrameEngine.initiateAreaChange(
					"CAFE_PARTY", 
					new Vector2(FrameEngine.TILE * 14, FrameEngine.TILE * 11), 
					Direction.UP);
		}
	}
	
	protected boolean shouldActivate() {
		return touchingPlayer(hitbox) && !activated;
	}

	@Override
	public void dispose() {}

}
