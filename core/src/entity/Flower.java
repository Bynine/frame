package entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.AudioHandler;
import main.FrameEngine;
import text.DialogueTree;

public class Flower extends InteractableEntity {

	private final String id;
	private State state;
	private final TextureRegion 
	dirt = new TextureRegion(new Texture("sprites/objects/dirt.png")),
	seed = new TextureRegion(new Texture("sprites/objects/seed.png"));
	private final ArrayList<Animation<TextureRegion>> 
	flower1 = Animator.createAnimation(30, "sprites/objects/flower1.png", 2, 1),
	flower2 = Animator.createAnimation(30, "sprites/objects/flower2.png", 2, 1),
	flower3 = Animator.createAnimation(30, "sprites/objects/flower3.png", 2, 1),
	flower4 = Animator.createAnimation(30, "sprites/objects/flower4.png", 2, 1),
	flower5 = Animator.createAnimation(30, "sprites/objects/flower5.png", 2, 1);
	public static final String
	flowerPrefix = "FLOWER_",
	seedPrefix = "SEED_",
	grownFlowers = "GROWN_FLOWERS";
	private FlowerType flowerType;
	private final Sound whistle = Gdx.audio.newSound(Gdx.files.internal("sfx/whistle.wav"));


	public Flower(float x, float y, String id) {
		super(x, y, "");
		image = new Sprite(dirt);
		this.id = id;
		hitbox.setSize(24);
		if (!FrameEngine.getSaveFile().getMapping(flowerPrefix + id).isEmpty()){
			setFlower();
			setFlowerType(flowerPrefix);
		}
		else if (!FrameEngine.getSaveFile().getMapping(seedPrefix + id).isEmpty()){
			setSeed();
			setFlowerType(seedPrefix);
		}
		else{
			setDirt();
		}
	}
	
	private void setFlowerType(String prefix){
		System.out.println(FrameEngine.getSaveFile().getMapping(prefix + id));
		int type = Integer.parseInt(
				FrameEngine.getSaveFile().getMapping(prefix + id).substring(4)
						);
		flowerType = FlowerType.values()[type-1];
	}

	private void setFlower(){
		state = State.FLOWER;
		layer = Layer.NORMAL;
	}

	private void setSeed(){
		state = State.SEED;
		layer = Layer.BACK;
	}

	private void setDirt(){
		state = State.DIRT;
		layer = Layer.BACK;
	}

	@Override
	public void interact(){
		switch(state){
		case DIRT: {
			FrameEngine.startDialogueTree(new DialogueTree(this, "dirt"));
		} break;
		case SEED: {
			FrameEngine.startDialogueTree(new DialogueTree(this, "seed"));
		} break;
		case FLOWER: {
			switch (flowerType){
			case ONE:	{
				FrameEngine.startDialogueTree(new DialogueTree(this, "flower1"));
			} break;
			case TWO:	{
				FrameEngine.startDialogueTree(new DialogueTree(this, "flower2"));
			} break;
			case THREE:	{
				FrameEngine.startDialogueTree(new DialogueTree(this, "flower3"));
			} break;
			case FOUR:	{
				FrameEngine.startDialogueTree(new DialogueTree(this, "flower4"));
			} break;
			case FIVE:	{
				FrameEngine.startDialogueTree(new DialogueTree(this, "flower5"));
			} break;
			}
		} break;
		}
	}

	@Override
	public void getMessage(String message){
		super.getMessage(message);
		if (message.startsWith("SEED")){
			FrameEngine.getSaveFile().setMapping(seedPrefix + id, message);
			setFlowerType(seedPrefix);
			setSeed();
		}
		if (message.equals("WATER")){
			FrameEngine.getSaveFile().setMapping(flowerPrefix + id, 
					FrameEngine.getSaveFile().getMapping(seedPrefix + id));
			FrameEngine.getSaveFile().addToCounter(1, grownFlowers);
			setFlower();
			if (flowerType.equals(FlowerType.ONE)) {
				AudioHandler.playSoundVariedPitch(whistle);
			}
		}
	}

	@Override
	public void updateImage(){
		switch(state){
		case DIRT: image = dirt; break;
		case SEED: image = seed; break;
		case FLOWER: {
			switch (flowerType){
			case ONE:	{
				image = flower1.get(0).getKeyFrame(FrameEngine.getTime()); break;
			}
			case TWO:	{
				image = flower2.get(0).getKeyFrame(FrameEngine.getTime()); break;
			}
			case THREE:	{
				image = flower3.get(0).getKeyFrame(FrameEngine.getTime()); break;
			}
			case FOUR:	{
				image = flower4.get(0).getKeyFrame(FrameEngine.getTime()); break;
			}
			case FIVE:	{
				image = flower5.get(0).getKeyFrame(FrameEngine.getTime()); break;
			}
			}
			break;
		}
		
		}
	}

	private enum State{
		DIRT, SEED, FLOWER
	}

	@Override
	public void dispose() {
		dirt.getTexture().dispose();
		seed.getTexture().dispose();
		Animator.freeAnimation(flower1);
		Animator.freeAnimation(flower2);
		Animator.freeAnimation(flower3);
		Animator.freeAnimation(flower4);
		Animator.freeAnimation(flower5);
		whistle.dispose();
	}
	
	private enum FlowerType{
		ONE, TWO, THREE, FOUR, FIVE
	}

}
