package overworld;

import main.FrameEngine;
import main.Timer;

public class Player extends Entity{
	
	private final float speed = 5.0f;
	private final Timer invincibility = new Timer(60);

	public Player(float x, float y) {
		super(x, y);
		timer_list.add(invincibility);
	}

	@Override
	protected void update_velocity(){
		super.update_velocity();
		velocity.set(
				speed * FrameEngine.getInputHandler().getXInput(),
				speed * FrameEngine.getInputHandler().getYInput());
	}
	
	/**
	 * The player temporarily becomes invincible, and battles won't start.
	 */
	public void reset_invincibility(){
		invincibility.reset();
	}
	
	public boolean isInvincible(){
		return !invincibility.timeUp();
	}
}
