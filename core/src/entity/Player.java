package entity;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import area.Area.Terrain;
import main.AudioHandler;
import main.FrameEngine;
import main.Timer;

public class Player extends Entity{

	private static final int stepTime = 14;
	private final Timer 
	invincibility = new Timer(60),
	stepTimer = new Timer(stepTime * 2);
	
	private static final ArrayList<Animation<TextureRegion>> walk = 
			Animator.createAnimation(stepTime, "sprites/player/walk.png", 4, 3);
	private static final ArrayList<Animation<TextureRegion>> idle = 
			Animator.createAnimation(35, "sprites/player/idle.png", 2, 3);
	public static final TextureRegion ripple = 
			new TextureRegion(new Texture(Gdx.files.internal("sprites/graphics/ripple.png")));
	
	private static final Rectangle interaction_box = 
			new Rectangle(0, 0, 16, 16);
	private final Vector2 input = new Vector2();
	
	private static final Sound 
	stepGrass = Gdx.audio.newSound(Gdx.files.internal("sfx/step_grass.wav")),
	stepWood = Gdx.audio.newSound(Gdx.files.internal("sfx/step_wood.wav")),
	stepWater = Gdx.audio.newSound(Gdx.files.internal("sfx/step_water.wav"));

	public Player(float x, float y) {
		super(x, y);
		timerList.addAll(Arrays.asList(invincibility, stepTimer));
		shadow = new TextureRegion(new Texture("sprites/player/shadow.png"));
		setRight();
	}
	
	@Override
	public void update(){
		input.set(FrameEngine.getInputHandler().getXInput(), FrameEngine.getInputHandler().getYInput());
		super.update();
	}

	@Override
	protected void updateVelocity(){
		super.updateVelocity();
		velocity.add(
				acceleration * input.x,
				acceleration * input.y
				);
	}

	/**
	 * Returns a rectangle in front of where the player is facing.
	 */
	public Rectangle getInteractionBox(){
		Rectangle temp = new Rectangle(interaction_box);
		if (dir == SIDE && flipped) {
			temp.setPosition(position.x - temp.width, position.y);
		}
		else if (dir == SIDE && !flipped) {
			temp.setPosition(position.x + temp.width*2, position.y);
		}
		else if (dir == UP){
			temp.setPosition(position.x + temp.width/2, position.y + temp.height*1.5f);
		}
		else if (dir == DOWN){
			temp.setPosition(position.x + temp.width/2, position.y - temp.height);
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
		if (input.y > min_control) setUp();
		else if (input.y < -min_control) setDown();
		else if (input.x < -min_control) setLeft();
		else if (input.x > min_control) setRight(); 
	}
	
	@Override
	public void updateImage(){
		if (!FrameEngine.canUpdateEntities() ||
				(input.x == 0 && input.y == 0)){
			image = idle.get(dir).getKeyFrame(FrameEngine.getTime());
		}
		else{
			if (stepTimer.timeUp()) stepSound();
			image = walk.get(dir).getKeyFrame(FrameEngine.getTime());
		}
	}

	/**
	 * Makes sound depending on what Terrain player is on.
	 */
	private void stepSound(){
		Terrain terrain = FrameEngine.getArea().getTerrain(this);
		Sound step;
		switch(terrain){
		case WOOD: {
			step = stepWood;
		} break;
		case WATER: {
			step = stepWater;
		} break;
		case NORMAL:
		default: step = stepGrass;
		}
		AudioHandler.playSound(step);
		stepTimer.reset();
	}

	@Override
	public void dispose() {
		// Don't dispose player assets.
	}

}
