package overworld;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import main.AudioHandler;
import main.FrameEngine;
import main.Timer;

public class Player extends Entity{

	private final Timer 
		invincibility = new Timer(60),
		stepTimer = new Timer(30);
	private static final ArrayList<Animation<TextureRegion>> walk = 
			Animator.create_animation(15, "sprites/overworld/player/walk.png", 4, 3);
	private static final ArrayList<Animation<TextureRegion>> idle = 
			Animator.create_animation(30, "sprites/overworld/player/idle.png", 2, 3);
	private static final Rectangle interaction_box = 
			new Rectangle(0, 0, 16, 16);
	private static final Sound step = 
			Gdx.audio.newSound(Gdx.files.internal("sfx/step.wav"));

	public Player(float x, float y) {
		super(x, y);
		timer_list.addAll(Arrays.asList(invincibility, stepTimer));
	}

	@Override
	protected void update_velocity(){
		super.update_velocity();
		velocity.add(
				acceleration * FrameEngine.getInputHandler().getXInput(),
				acceleration * FrameEngine.getInputHandler().getYInput()
				);
	}
	
	/**
	 * Returns a rectangle in front of where the player is facing.
	 */
	public Rectangle getInteractionBox(){
		Rectangle temp = new Rectangle(interaction_box);
		if (direction == SIDE && flipped) {
			temp.setPosition(position.x - temp.width, position.y);
		}
		else if (direction == SIDE && !flipped) {
			temp.setPosition(position.x + temp.width, position.y);
		}
		else if (direction == UP){
			temp.setPosition(position.x, position.y + temp.height);
		}
		else if (direction == DOWN){
			temp.setPosition(position.x, position.y - temp.height);
		}
		return temp;
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

	@Override
	public Color getColor() {
		if (isInvincible()) return new Color(1, 1, 1, 0.5f);
		else return super.getColor();
	}
	
	/**
	 * If the player is going past a certain speed in a direction,
	 * or just pressed an input in that direction, faces that direction.
	 */
	@Override
	protected void setDirection(){
		float min_control = 0.5f;
		if (FrameEngine.getInputHandler().getYInput() > min_control) setUp();
		else if (FrameEngine.getInputHandler().getYInput() < -min_control) setDown();
		else if (FrameEngine.getInputHandler().getXInput() < -min_control) setLeft();
		else if (FrameEngine.getInputHandler().getXInput() > min_control) setRight(); 
	}

	@Override
	public TextureRegion getImage(){
		if (FrameEngine.getInputHandler().getXInput() == 0 && 
				FrameEngine.getInputHandler().getYInput() == 0){
			return idle.get(direction).getKeyFrame(FrameEngine.getTime());
		}
		else{
			if (stepTimer.timeUp()){
				AudioHandler.play_sfx(step);
				stepTimer.reset();
			}
			return walk.get(direction).getKeyFrame(FrameEngine.getTime());
		}
	}

	@Override
	public void dispose() {
		// Don't dispose player assets.
	}

}
