package overworld;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;
import main.GraphicsHandler;
import main.Timer;

public abstract class Entity {

	protected final Vector2 position = new Vector2();
	protected final Vector2 velocity = new Vector2();
	protected final TextureRegion sample = new TextureRegion(new Texture("sprites/overworld/dummy.png"));
	protected final Rectangle hitbox = new Rectangle(0, 0, 30, 20);
	protected final ArrayList<Timer> timer_list = new ArrayList<Timer>();
	protected float acceleration = 0.85f;
	protected float corner_acceleration = 0.5f;
	protected float friction = 0.74f;
	protected float contact_friction = 0.88f;

	/**
	 * Directions.
	 */
	protected static final int
	DOWN = 0,
	UP = 1,
	SIDE = 2;

	protected boolean flipped = false;
	protected boolean delete = false;
	protected boolean onSlope = false;
	protected int direction = DOWN;

	public Entity(float x, float y){
		position.set(x, y);
	}

	public void update(){
		update_timer_list();
		check_slopes();
		update_velocity();
		setDirection();
		limit_velocity();
		handle_collision();
		update_position();
	}

	/**
	 * Counts up each timer in the entity's timer list.
	 */
	protected void update_timer_list(){
		for (Timer timer: timer_list) timer.countUp();
	}

	/**
	 * Checks whether or not this entity is on a slope.
	 */
	protected void check_slopes(){
		onSlope = false;
		for(Rectangle slope: FrameEngine.getCurrentArea().getSlopes()){
			if (slope.overlaps(hitbox)){
				onSlope = true;
			}
		}
	}

	/**
	 * Any changes to the entity's velocity.
	 */
	protected void update_velocity(){
		if (onSlope && (velocity.y > 0 || velocity.y < -2.2f)){
			velocity.y -= (0.4f * FrameEngine.elapsed_time);
		}
	}

	/**
	 * Effects that limit or affect velocity, such as friction.
	 */
	protected void limit_velocity(){
		// TODO: incorporate elapsed time. (float) Math.pow(friction, FrameEngine.elapsed_time)
		velocity.scl(friction);
	}

	/**
	 * Checks for collision in horizontal, vertical, then diagonal directions.
	 */
	protected void handle_collision(){
		handle_collision_helper(true, false);	// Horizontal
		handle_collision_helper(false, true);	// Vertical
		handle_collision_helper(true, true);	// Diagonal
	}

	/**
	 * Compares all rectangles to future position to see if it collides.
	 * If it does, stops that velocity.
	 */
	private void handle_collision_helper(boolean check_x, boolean check_y){
		Rectangle temp = new Rectangle(hitbox);
		if (check_x) temp.x += (FrameEngine.elapsed_time) * velocity.x;
		if (check_y) temp.y += (FrameEngine.elapsed_time) * velocity.y;
		for(Rectangle collider: FrameEngine.getCurrentArea().getCollision()){
			if (temp.overlaps(collider)){ // Contact, he shouts
				velocity.x *= check_x ? 0 : contact_friction;
				velocity.y *= check_y ? 0 : contact_friction;
				if (!check_x && check_y){
					if (velocity.y <= 0) corner_checker(1, true, temp, collider);
					if (velocity.y >= 0) corner_checker(-1, true, temp, collider);
				}
				if (check_x && !check_y){
					if (velocity.x <= 0) corner_checker(1, false, temp, collider);
					if (velocity.x >= 0) corner_checker(-1, false, temp, collider);
				}
			}
		}
	}

	/**
	 * Checks a corner. If the player is close enough to it, moves toward it.
	 */
	private void corner_checker(int dir, boolean is_x, Rectangle temp, Rectangle collider){
		final int distance_check = 16;
		final float speed = dir * corner_acceleration; // Doesn't check for elapsed time on purpose.
		Rectangle temp2 = new Rectangle(temp);
		if (is_x) temp2.x += dir * distance_check;
		else temp2.y += dir * distance_check;
		if (!temp2.overlaps(collider)){
			if (is_x) velocity.x += speed;
			else velocity.y += speed;
		}
	}

	/**
	 * Updates the actual position of this entity.
	 */
	protected void update_position(){
		position.add(
				(FrameEngine.elapsed_time) * velocity.x, 
				(FrameEngine.elapsed_time) * velocity.y
				);
		hitbox.setPosition(position);
	}

	/**
	 * Whether the given rectangle overlaps the player's hitbox.
	 */
	protected boolean touching_player(Rectangle rect){
		return rect.overlaps(FrameEngine.getPlayer().hitbox);
	}

	/**
	 * Gets the current texture region.
	 */
	public TextureRegion getImage(){
		return sample;
	}

	/**
	 * Marks this entity for deletion. It will no longer be updated and be removed from the Area.
	 */
	public void setDelete(){
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
	public abstract void dispose();

	/**
	 * Current tint of the sprite.
	 */
	public Color getColor() {
		return GraphicsHandler.DEFAULT_COLOR;
	}

	/**
	 * The center of the entity's sprite.
	 */
	public Vector2 getCenter(){
		return new Vector2(
				position.x + getImage().getRegionWidth()/2,
				position.y + getImage().getRegionHeight()/2
				);
	}

	/**
	 * Gets the direction this entity is facing as an int.
	 * TODO: Refactor to be nicer
	 */
	protected void setDirection(){
		if (Math.abs(velocity.x) > Math.abs(velocity.y)){
			if (velocity.x > 0) setLeft();
			else if (velocity.x < 0) setRight();
		}
		else if (velocity.y > 0) setUp();
		else if (velocity.y < 0) setDown();
	}

	protected final void setLeft(){
		flipped = true;
		direction = SIDE;
	}

	protected final void setRight(){
		flipped = false;
		direction = SIDE;
	}

	protected final void setUp(){
		flipped = false;
		direction = UP;
	}

	protected final void setDown(){
		flipped = false;
		direction = DOWN;
	}

	public boolean isFlipped(){
		return flipped;
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

}
