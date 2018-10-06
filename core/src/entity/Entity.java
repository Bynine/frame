package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;
import main.GraphicsHandler;
import timer.Timer;

public abstract class Entity {

	protected final Vector2 position = new Vector2();
	protected final Vector2 velocity = new Vector2();
	protected final TextureRegion sample = new TextureRegion(new Texture("sprites/npcs/dummy.png"));
	protected final Rectangle hitbox = new Rectangle(0, 0, 28, 18);
	protected final ArrayList<Timer> timerList = new ArrayList<Timer>();
	protected float acceleration = 1.0f;
	protected float corner_acceleration = 0.35f;
	protected float friction = 0.69f;
	protected float contact_friction = 0.92f;

	/**
	 * Directions.
	 */
	protected static final int
	DOWN = 0,
	UP = 1,
	SIDE = 2;

	protected boolean flipped = false;
	protected boolean canFlip = true;
	protected boolean delete = false;
	protected boolean onSlope = false;
	protected boolean collides = true;
	protected int dir = DOWN;
	protected Layer layer = Layer.NORMAL;
	protected float zPosition = 0;
	protected TextureRegion image = sample;
	protected TextureRegion shadow = new TextureRegion(new Texture("sprites/npcs/shadow.png"));

	public Entity(float x, float y){
		position.set(x, y);
	}

	public void update(){
		updateTimers();
		updateImage();
		checkSlopes();
		updateVelocity();
		setDirection();
		limitVelocity();
		if (collides) handleCollision();
		updatePosition();
	}

	/**
	 * Counts up each timer in the entity's timer list.
	 */
	protected void updateTimers(){
		for (Timer timer: timerList) timer.countUp();
	}
	
	/**
	 * Used to change the TextureRegion returned by the Entity.
	 */
	public void updateImage(){
		
	}

	/**
	 * Checks whether or not this entity is on a slope.
	 */
	protected void checkSlopes(){
		onSlope = false;
		for(Rectangle slope: FrameEngine.getArea().getSlopes()){
			if (slope.overlaps(hitbox)){
				onSlope = true;
			}
		}
	}

	/**
	 * Any changes to the entity's velocity.
	 */
	protected void updateVelocity(){
		final float minVelocity = 0.01f;
		if (onSlope && (velocity.y > 0 || velocity.y < -1.8f)){
			velocity.y -= getFrameFriction(0.28f);
		}
		if (Math.abs(velocity.x) < minVelocity) velocity.x = 0;
		if (Math.abs(velocity.y) < minVelocity) velocity.y = 0;
	}

	/**
	 * Effects that limit or affect velocity, such as friction.
	 */
	protected void limitVelocity(){
		final float limit = 0.5f;
		velocity.scl(getFrameFriction(friction));
		if (Math.abs(velocity.x) > limit && Math.abs(velocity.y) > limit) {
			final float diagonalFriction = 0.91f;
			velocity.scl(getFrameFriction(diagonalFriction));
		}
	}
	
	/**
	 * Gets friction relative to player's FPS.
	 */
	private float getFrameFriction(float friction){
		float frameLimit = 8.0f;
		return 
				((frameLimit - 1) * friction + 
				(float) Math.pow(
						friction, 
						FrameEngine.elapsedTime))
				/frameLimit
				;
	}

	/**
	 * Checks for collision in horizontal, vertical, then diagonal directions.
	 */
	protected void handleCollision(){
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
		if (check_x) temp.x += (FrameEngine.elapsedTime) * velocity.x;
		if (check_y) temp.y += (FrameEngine.elapsedTime) * velocity.y;
		for(Rectangle collider: FrameEngine.getArea().getCollision()){
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
			if (is_x) velocity.x += speed/FrameEngine.getGameSpeed();
			else velocity.y += speed/FrameEngine.getGameSpeed();
		}
	}
	
	protected void matchRectangleToPosition(Rectangle rect){
		matchRectangleToPosition(rect, 0, 0);
	}
	
	/**
	 * Centers a rectangle on the entity's position.
	 */
	protected void matchRectangleToPosition(Rectangle rect, int xDisp, int yDisp){
		rect.setPosition(
				getPosition().x + ((hitbox.width - rect.width) / 2) + xDisp,
				getPosition().y + ((hitbox.height - rect.height) / 2) + yDisp
				);
	}

	/**
	 * Updates the actual position of this entity.
	 */
	protected void updatePosition(){
		position.add(
				(FrameEngine.elapsedTime) * velocity.x, 
				(FrameEngine.elapsedTime) * velocity.y
				);
		hitbox.setPosition(position);
	}

	/**
	 * Whether the given rectangle overlaps the player's hitbox.
	 */
	protected boolean touchingPlayer(Rectangle rect){
		return rect.overlaps(FrameEngine.getPlayer().hitbox);
	}

	/**
	 * Gets the current texture region.
	 */
	public TextureRegion getImage(){
		return image;
	}

	/**
	 * Marks this entity for deletion. It will no longer be updated and be removed from the Area.
	 */
	public void setRemove(){
		delete = true;
	}

	/**
	 * Whether or not this entity is set for deletion.
	 */
	public boolean shouldDelete(){
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
		dir = SIDE;
	}

	protected final void setRight(){
		flipped = false;
		dir = SIDE;
	}

	protected final void setUp(){
		flipped = false;
		dir = UP;
	}

	protected final void setDown(){
		flipped = false;
		dir = DOWN;
	}

	public boolean isFlipped(){
		return flipped && canFlip;
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

	public Layer getLayer() {
		return layer;
	}
	
	public float getZPosition(){
		return zPosition;
	}
	
	public static enum Layer{
		BACK, NORMAL, FRONT
	}

	public TextureRegion getShadow() {
		return shadow;
	}

	public boolean collides() {
		return collides;
	}

}
