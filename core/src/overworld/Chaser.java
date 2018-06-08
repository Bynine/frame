package overworld;

import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;

/**
 * Enemy that follows the player around.
 */
public class Chaser extends Enemy {
	
	public Chaser(int x, int y) {
		super(x, y);
	}

	protected final float speed = 3.2f;

	@Override
	protected void update_velocity(){
		super.update_velocity();
		Vector2 player_position = FrameEngine.getPlayer().getPosition();
		Vector2 new_velocity = new Vector2(player_position).sub(position);
		if (new_velocity.len() < 8 || new_velocity.len() > 250) { 
			// Player is too close or too far away, so stop moving
			velocity.set(0, 0);
		}
		else{
			float sum = Math.abs(new_velocity.x) +  Math.abs(new_velocity.y);
			new_velocity.scl(speed/sum);
			velocity.set(new_velocity);
		}
	}
	
}
