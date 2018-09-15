package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.TSVReader;
import text.DialogueTree;
import main.Animator;
import main.FrameEngine;

public class NPC extends InteractableEntity{

	protected final ArrayList<ArrayList<Animation<TextureRegion>>> anims;
	private final String name, dialoguePath;
	private int currentAnim = 0;

	public NPC(float x, float y, 
			int interactXDisp, int interactYDisp,
			int width, int height, 
			String id, String imagePath, String dialoguePath,
			Layer layer
			) {
		super(x, y, "");
		String[] data = new TSVReader().loadDataByID(id, TSVReader.NPC_URL);
		name = data[1];
		voiceUrl = data[2];
		boolean hasShadow = Boolean.parseBoolean(data[3]);
		if (!hasShadow) shadow = null;
		anims = Animator.createAnimationList(data, imagePath);
		if (Integer.parseInt(data[4]) == 1) canFlip = false;
		hitbox.setSize(width, height);
		interactHitbox.setSize(width, height);
		this.interactXDisp = interactXDisp;
		this.interactYDisp = interactYDisp;
		this.dialoguePath = dialoguePath;
		this.layer = layer;
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
		anims = new ArrayList<>();
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
		if (anims.size() > currentAnim){
			ArrayList<Animation<TextureRegion>> anim = anims.get(currentAnim);
			image = anim.get(dir >= anim.size() ? anim.size()-1 : dir).getKeyFrame(FrameEngine.getTime());
		}
	}
	
	@Override
	public void getMessage(String message) {
		super.getMessage(message);
		if (message.startsWith("ANIM_")){
			currentAnim = Integer.parseInt(message.split("_")[1]);
		}
	}

	@Override
	public void dispose(){
		for (ArrayList<Animation<TextureRegion>> anim: anims){
				Animator.freeAnimation(anim);
		}
		if (null != image) {
			image.getTexture().dispose();
		}
	}

	public String getName(){
		return name;
	}

}
