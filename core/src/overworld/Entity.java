package overworld;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;
import main.Timer;

public abstract class Entity {

	protected final Vector2 position = new Vector2();
	protected final Vector2 velocity = new Vector2();
	private final TextureRegion sample = new TextureRegion(new Texture("sprites/overworld/dummy.png"));
	protected final Rectangle hitbox = new Rectangle(0, 0, 32, 20);
	protected final float speed = 5.0f;
	protected boolean delete = false;
	protected final ArrayList<Timer> timer_list = new ArrayList<Timer>();

	public Entity(float x, float y){
		position.set(x, y);
	}

	public void update(){
		update_timer_list();
		update_velocity();
		handle_collision();
		update_position();
	}
	
	private void update_timer_list(){
		for (Timer timer: timer_list){
			timer.countUp();
		}
	}

	protected void update_velocity(){
		/**/
	}

	/**
	 * Checks all rectangles to see if it collides. If it does, slows down until it doesn't.
	 */
	protected void handle_collision(){
		// TODO: make more efficient, less sticky
		Rectangle temp = new Rectangle(hitbox);
		temp.x += (FrameEngine.elapsed_time) * velocity.x;
		temp.y += (FrameEngine.elapsed_time) * velocity.y;
		for(Rectangle collider: FrameEngine.getCurrentArea().getCollision()){
			if (temp.overlaps(collider)){ 
				velocity.setZero();
			}
		}
	}
	
	protected void update_position(){
		position.add((FrameEngine.elapsed_time) * velocity.x,
				(FrameEngine.elapsed_time) * velocity.y);
		hitbox.setPosition(position);
	}
	
	protected boolean touching_player(){
		return hitbox.overlaps(FrameEngine.getPlayer().hitbox);
	}

	public Vector2 getPosition(){
		return position;
	}

	public Vector2 getVelocity(){
		return velocity;
	}

	public Rectangle getHitbox(){
		return hitbox;
	}

	/**
	 * Gets the current texture region.
	 */
	public TextureRegion getImage(){
		return sample;
	}

	/**
	 * Marks this entity for deletion. It will no longer be updated and be removed from the Room.
	 */
	public void mark_delete(){
		delete = true;
	}

	/**
	 * Whether or not this entity is set for deletion.
	 */
	public boolean should_delete(){
		return delete;
	}

	/**
	 * Dispose all contained texture regions.
	 */
	public void dispose(){
		sample.getTexture().dispose();
	}
}
