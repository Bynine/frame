package overworld;

import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;

public abstract class InteractableEntity extends ImmobileEntity {
	
	protected final String text;
	protected final Rectangle interact_hitbox = new Rectangle(0, 0, 32, 32);

	public InteractableEntity(float x, float y, String text) {
		super(x, y);
		this.text = text;
	}

	public void interact() {
		FrameEngine.setTextbox(getText());
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
		return interact_hitbox.overlaps(player.getInteractionBox());
	}
	
	@Override
	protected void update_position(){
		super.update_position();
		interact_hitbox.setPosition(
				getPosition().x + (hitbox.width - interact_hitbox.width)/2,
				getPosition().y + (hitbox.height - interact_hitbox.height)/2
				);
	}
	
	public String getText(){
		return text;
	}

}
