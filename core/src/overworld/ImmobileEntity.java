package overworld;

/**
 * An entity that never moves.
 *
 */
public abstract class ImmobileEntity extends Entity {

	
	public ImmobileEntity(float x, float y) {
		super(x, y);
	}

	/**
	 * This entity never moves, so it doesn't have to handle any movement functions.
	 */
	public void update(){
		update_timer_list();
		update_position();
	}

}
