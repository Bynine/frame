package entity;

import com.badlogic.gdx.math.Rectangle;

import main.Timer;

/**
 * Small animal that wanders around world.
 */
public abstract class Critter extends Entity {
	
	protected boolean reacted = false;
	protected final Timer reactTimer = new Timer(0);
	protected final Rectangle boundary = new Rectangle(0, 0, 64, 64);

	public Critter(float x, float y) {
		super(x, y);
		timerList.add(reactTimer);
		matchRectangleToPosition(boundary);
	}
	
	@Override
	public void update(){
		super.update();
		matchRectangleToPosition(getNoticeBox());
		if (!reacted && touchingPlayer(getNoticeBox())){
			react();
			reacted = true;
			reactTimer.reset();
		}
	}
	
	/**
	 * Behavior when player gets close enough.
	 */
	protected abstract void react();
	
	/**
	 * When the player comes into contact with this, the Critter reacts.
	 * @return
	 */
	public abstract Rectangle getNoticeBox();

}
