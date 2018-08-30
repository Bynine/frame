package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class DialogueTrigger extends ImmobileEntity {
	
	private final DialogueTree dialogueTree;

	public DialogueTrigger(float x, float y, float width, float height, String dialoguePath) {
		super(x, y);
		hitbox.setSize(width, height);
		dialogueTree = new DialogueTree(null, dialoguePath);
	}
	
	@Override
	public void update(){
		super.update();
		if (touchingPlayer(hitbox)){
			FrameEngine.startDialogueTree(dialogueTree);
			setDelete();
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
