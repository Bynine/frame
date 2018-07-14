package overworld;

import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;
import main.Textbox;

public abstract class InteractableEntity extends ImmobileEntity {
	
	protected final String text;
	protected final Rectangle interactHitbox = new Rectangle(0, 0, 32, 32);
	protected String voiceUrl;
	protected int interactYDisp;

	public InteractableEntity(float x, float y, String text) {
		super(x, y);
		this.text = text;
	}

	public void interact() {
		FrameEngine.setTextbox(new Textbox(this));
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
	protected void update_position(){
		super.update_position();
		interactHitbox.setPosition(
				getPosition().x,
				getPosition().y + interactYDisp
				);
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
