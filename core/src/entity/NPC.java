package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.TSVReader;
import text.DialogueTree;
import main.FrameEngine;

public class NPC extends InteractableEntity{

	private final ArrayList<Animation<TextureRegion>> anim;
	private final String name, dialoguePath;

	public NPC(float x, float y, 
			int interactXDisp, int interactYDisp,
			int width, int height, 
			String id, String imagePath, String dialoguePath) {
		super(x, y, "");
		String[] data = new TSVReader().loadDataByID(id, TSVReader.NPC_URL);
		name = data[1];
		voiceUrl = data[2];
		boolean hasShadow = Boolean.parseBoolean(data[3]);
		if (!hasShadow) shadow = null;
		int animNumber = Integer.parseInt(data[4]);
		int animSpeed = Integer.parseInt(data[5]);
		anim = Animator.createAnimation(animSpeed, "sprites/npcs/" + imagePath + ".png", 2, animNumber);
		if (anim.size() == 1) canFlip = false;
		hitbox.setSize(width, height);
		interactHitbox.setSize(width, height);
		this.interactXDisp = interactXDisp;
		this.interactYDisp = interactYDisp;
		this.dialoguePath = dialoguePath;
	}
	
	/**
	 * An NPC for textboxes. Not actually created on map.
	 */
	public NPC(String id, String dialoguePath){
		super(0, 0, "");
		String[] data = new TSVReader().loadDataByID(id, TSVReader.NPC_URL);
		name = data[1];
		voiceUrl = data[2];
		this.dialoguePath = dialoguePath;
		anim = null;
	}
	
	@Override
	public void interact(){
		int playerDir = FrameEngine.getPlayer().dir;
		boolean playerFlipped = FrameEngine.getPlayer().flipped;
		switch(playerDir){
		case DOWN: {
			setUp();
		} break;
		case UP: {
			setDown();
		} break;
		case SIDE: {
			if (playerFlipped) setRight();
			else setLeft();
		} break;
		}
		updateImage();
		FrameEngine.startDialogueTree(new DialogueTree(this, dialoguePath));
	}

	@Override
	public void updateImage(){
		if (null != anim){
			image = anim.get(dir >= anim.size() ? anim.size()-1 : dir).getKeyFrame(FrameEngine.getTime());
		}
	}

	@Override
	public void dispose(){
		if (null != anim) {
			Animator.freeAnimation(anim);
		}
		if (null != image) {
			image.getTexture().dispose();
		}
	}
	
	public String getName(){
		return name;
	}

	/**
	 * DialogueTree can send the speaker a message.
	 */
	public void receiveMessage(String message) {
		//
	}

}
