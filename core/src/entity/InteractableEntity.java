package entity;

import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;
import text.DialogueTree;
import text.Textbox;

public abstract class InteractableEntity extends ImmobileEntity {
	
	private static final String vowels = "aeiou";
	
	protected final String text;
	protected final Rectangle interactHitbox = new Rectangle(0, 0, 32, 32);
	protected String voiceUrl;
	protected int interactXDisp, interactYDisp;

	public InteractableEntity(float x, float y, String text) {
		super(x, y);
		this.text = text;
	}

	public void interact() {
		if (getText().contains("\n")){
			FrameEngine.startDialogueTree(new DialogueTree(getText()));
		}
		else{
			FrameEngine.putTextbox(new Textbox(getText()));
		}
	}
	
	@Override
	public void update(){
		super.update();
		if (canInteractWithPlayer()){
			FrameEngine.setInteractableEntity(this);
		}
	}
	
	/**
	 * Checks to see if the player's interaction hitbox overlaps this interaction hitbox.
	 */
	private boolean canInteractWithPlayer(){
		Player player = FrameEngine.getPlayer();
		return interactHitbox.overlaps(player.getInteractionBox());
	}
	
	@Override
	protected void updatePosition(){
		super.updatePosition();
		matchRectangleToPosition(interactHitbox, interactXDisp, interactYDisp);
	}
	
	protected String getIndefinite(String noun){
		if (vowels.indexOf(Character.toLowerCase(noun.charAt(0))) != -1){
			return "an";
		}
		return "a";
	}
	
	public String getText(){
		return text;
	}
	
	public String getVoiceUrl(){
		return voiceUrl;
	}
	
	public Rectangle getInteractHitbox(){
		return interactHitbox;
	}

}
