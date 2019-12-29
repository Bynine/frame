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
import main.Animator;
import main.AudioHandler;
import main.EntityHandler;
import main.FrameEngine;
import timer.Timer;

public class Player extends Entity{

	private static final int stepTime = 14, boostTime = 30, cooldownTime = 60;
	private final Timer 
	invincibility = new Timer(60),
	stepTimer = new Timer(stepTime * 2),
	walkRightTimer = new Timer(30),
	coffeeTimer = new Timer(boostTime + cooldownTime);
	private static ImageState imageState = ImageState.NORMAL;

	private static final ArrayList<Animation<TextureRegion>> walk = 
			Animator.createAnimation(stepTime, "sprites/player/walk.png", 4, 3);
	private static final ArrayList<Animation<TextureRegion>> run = 
			Animator.createAnimation(stepTime/2, "sprites/player/run.png", 4, 3);
	private static final ArrayList<Animation<TextureRegion>> idle = 
			Animator.createAnimation(30, "sprites/player/idle.png", 2, 3);
	private static final ArrayList<Animation<TextureRegion>> slope = 
			Animator.createAnimation(stepTime, "sprites/player/slope.png", 4, 3);
	private static final ArrayList<Animation<TextureRegion>> idle_slope = 
			Animator.createAnimation(30, "sprites/player/idle_slope.png", 2, 3);
	private static final ArrayList<Animation<TextureRegion>> explain = 
			Animator.createAnimation(30, "sprites/player/talking.png", 2, 3);
	private static final ArrayList<Animation<TextureRegion>> dig = 
			Animator.createAnimation(30, "sprites/player/dig.png", 2, 3);
	private static final ArrayList<Animation<TextureRegion>> water = 
			Animator.createAnimation(30, "sprites/player/water.png", 2, 3);
	private static final ArrayList<Animation<TextureRegion>> pet = 
			Animator.createAnimation(30, "sprites/player/pet.png", 2, 3);
	private static final ArrayList<Animation<TextureRegion>> read = 
			Animator.createAnimation(30, "sprites/player/read.png", 2, 3);
	private static final ArrayList<Animation<TextureRegion>> cooldown = 
			Animator.createAnimation(30, "sprites/player/cooldown.png", 1, 3);
	private static final ArrayList<Animation<TextureRegion>> deep_water = 
			Animator.createAnimation(45, "sprites/player/deep_water.png", 2, 3);
	private static final TextureRegion get = 
			new TextureRegion(new Texture(Gdx.files.internal("sprites/player/get.png")));
	private static final TextureRegion sleep = 
			new TextureRegion(new Texture(Gdx.files.internal("sprites/player/sleep.png")));
	public static final TextureRegion ripple = 
			new TextureRegion(new Texture(Gdx.files.internal("sprites/graphics/ripple.png")));
	private static Emitter smokeEmitter = new Emitter(0, 0, stepTime, 30, "smoke");

	private static final Rectangle interaction_box = 
			new Rectangle(0, 0, 16, 16);
	private final Vector2 input = new Vector2();

	private static final Sound 
	stepGrass = Gdx.audio.newSound(Gdx.files.internal("sfx/step_grass.wav")),
	stepWood = Gdx.audio.newSound(Gdx.files.internal("sfx/step_wood.wav")),
	stepStone = Gdx.audio.newSound(Gdx.files.internal("sfx/step_stone.wav")),
	stepWater = Gdx.audio.newSound(Gdx.files.internal("sfx/step_water.wav")),
	stepSnow = Gdx.audio.newSound(Gdx.files.internal("sfx/step_stone.wav")),
	stepIce = Gdx.audio.newSound(Gdx.files.internal("sfx/step_stone.wav"));

	public Player(float x, float y) {
		super(x, y);
		timerList.addAll(Arrays.asList(invincibility, stepTimer, walkRightTimer, coffeeTimer));
		shadow = new TextureRegion(new Texture("sprites/player/shadow.png"));
		EntityHandler.addEntity(smokeEmitter);
		setRight();
	}

	@Override
	public void update(){
		updateInputs();
		super.update();
		smokeEmitter.getPosition().set(new Vector2(
					this.getCenter().x,
					this.getCenter().y
				));
		smokeEmitter.setEnabled(FrameEngine.isCocoaTime());
		on_ice = FrameEngine.getArea().getTerrain(this).equals(Terrain.ICE);
		in_water = FrameEngine.getArea().getTerrain(this).equals(Terrain.DEEP_WATER);
		zPosition = in_water ? -4 : 0;
		if (isBoosting()) {
			stepTimer.countUp();
		}
	}

	private void updateInputs(){
		if (null != FrameEngine.playerInput()){
			input.set(FrameEngine.playerInput());
		}
		else if (!walkRightTimer.timeUp()){
			input.set(1, 0);
		}
		else if (FrameEngine.canControlPlayer()){
			input.set(FrameEngine.getInputHandler().getXInput(), FrameEngine.getInputHandler().getYInput());
		}
		else{
			input.setZero();
		}
	}

	@Override
	protected void updateVelocity(){
		super.updateVelocity();
		if (isBoosting()) {
			final float coffeeSpeed = 7.2f;
			float coffeeSlopeSpeed = coffeeSpeed;
			Vector2 add = new Vector2();
			if (dir == DOWN) {
				add.y = -1;
				coffeeSlopeSpeed *= 1.1f;
			}
			if (dir == UP) {
				add.y = 1;
				coffeeSlopeSpeed *= 0.75f;
			}
			if (dir == SIDE && flipped) add.x = -1;
			if (dir == SIDE && !flipped) add.x = 1;
			velocity.set(add.scl(onSlope ? coffeeSlopeSpeed : coffeeSpeed));
		}
		else {
		velocity.add(
				getAcceleration() * input.x,
				getAcceleration() * input.y
				);
		}
	}
	
	private float getAcceleration() {
		if (in_water) return water_acceleration;
		return on_ice ? ice_acceleration : acceleration;
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
			temp.setPosition(position.x + temp.width/2, position.y + temp.height*1.55f);
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
		if (isBoosting()) return;
		float min_control = 0.5f;
		if (input.y > min_control) setUp();
		else if (input.y < -min_control) setDown();
		else if (input.x < -min_control) setLeft();
		else if (input.x > min_control) setRight(); 
	}

	@Override
	public void updateImage(){
		switch(imageState){
		case NORMAL:{
			if (isBoosting()) {
				if (stepTimer.timeUp()) stepSound();
				image = run.get(dir).getKeyFrame(FrameEngine.getTime());
			}
			else if (isCooldown()) {
				image = cooldown.get(dir).getKeyFrame(FrameEngine.getTime());
			}
			else if (in_water) {
				image = deep_water.get(dir).getKeyFrame(FrameEngine.getTime());
			}
			else if (!FrameEngine.canUpdateEntities() ||
					(input.x == 0 && input.y == 0)){
				if (onSlope) image = idle_slope.get(dir).getKeyFrame(FrameEngine.getTime());
				else image = idle.get(dir).getKeyFrame(FrameEngine.getTime());
			}
			else{
				if (stepTimer.timeUp()) stepSound();
				else if (onSlope) image = slope.get(dir).getKeyFrame(FrameEngine.getTime());
				else image = walk.get(dir).getKeyFrame(FrameEngine.getTime());
			}
		} break;
		case GET:{
			image = get;
		} break;
		case EXPLAIN:{
			image = explain.get(dir).getKeyFrame(FrameEngine.getTime());
		} break;
		case DIG:{
			image = dig.get(dir).getKeyFrame(FrameEngine.getTime()); 
		} break;
		case WATER:{
			image = water.get(dir).getKeyFrame(FrameEngine.getTime()); 
		} break;
		case SLEEP:{
			image = sleep;
		} break;
		case PET:{
			image = pet.get(dir).getKeyFrame(FrameEngine.getTime()); 
		} break;
		case READ:{
			image = read.get(dir).getKeyFrame(FrameEngine.getTime());
		} break;
		default: {
			image = get;
		} break;
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
		case STONE: {
			step = stepStone;
		} break;
		case SNOW: {
			step = stepSnow;
		} break;
		case ICE: {
			step = stepIce;
		} break;
		case NORMAL:
		default: step = stepGrass;
		}
		final float stepVolume = isBoosting() ? 1.3f : 0.8f;
		AudioHandler.playSoundVariedPitch(step, stepVolume);
		stepTimer.reset();
	}

	@Override
	public void dispose() {
		// Don't dispose player assets.
	}

	public void walkRight(int i) {
		setRight();
		walkRightTimer.setEndTime(i);
		walkRightTimer.reset();
	}

	public static enum ImageState{
		NORMAL, GET, DIG, WATER, EXPLAIN, SLEEP, PET, READ
	}

	public static ImageState getImageState(){
		return imageState;
	}

	public static void setImageState(ImageState is){
		imageState = is;
	}

	public boolean isColliding() {
		for(Rectangle collider: FrameEngine.getArea().getCollision()){
			if (hitbox.overlaps(collider)){ // Contact, he shouts
				return true;
			}
		}
		return false;
	}
	
	public boolean canControl() {
		return !isBoosting() && !isCooldown();
	}
	
	private boolean isBoosting() {
		return coffeeTimer.getCounter() < boostTime;
	}
	
	private boolean isCooldown() {
		return coffeeTimer.getCounter() < (boostTime + cooldownTime) && coffeeTimer.getCounter() >= boostTime;
	}

	public void coffeeBoost() {
		coffeeTimer.reset();
	}

	public void reset() {
		smokeEmitter = new Emitter(0, 0, stepTime, 30, "smoke");
		EntityHandler.addEntity(smokeEmitter);
	}

}