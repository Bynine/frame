package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class DialogueTrigger extends ImmobileEntity {
	
	protected DialogueTree dialogueTree;
	private final boolean entrance;

	public DialogueTrigger(float x, float y, float width, float height, String dialoguePath) {
		super(x, y);
		hitbox.setSize(width, height);
		if (dialoguePath.equals("entered_shrine") || dialoguePath.equals("kami_post_flame")){
			dialogueTree = new DialogueTree(new NPC("KAMI", dialoguePath), dialoguePath);
		}
		else{
			dialogueTree = new DialogueTree(null, dialoguePath);
		}
		entrance = dialoguePath.startsWith("entered") || dialoguePath.startsWith("left_with");
	}
	
	@Override
	public void update(){
		super.update();
		if (shouldActivate()){
			FrameEngine.startDialogueTree(dialogueTree);
			setRemove();
		}
	}
	
	protected boolean shouldActivate() {
		return touchingPlayer(hitbox) && (!entrance || FrameEngine.getPlayer().dir == UP);
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
