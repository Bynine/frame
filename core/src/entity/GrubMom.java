package entity;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class GrubMom extends NPC {

	int NUM_GRUBS_STILL_LOST = 3;
	private final TextureRegion cocoon = new TextureRegion(new Texture("sprites/npcs/grubmom/cocoon.png"));
	public GrubMom(float x, float y, int interactXDisp, int interactYDisp, int width, int height, String id,
			String imagePath, String dialoguePath, Layer layer) {
		super(x, y, interactXDisp, interactYDisp, width, height, id, imagePath, dialoguePath, layer);
		checkGrubsLost(1);
		checkGrubsLost(2);
		checkGrubsLost(3);
	}
	
	public void updateImage(){
		super.updateImage();
		if (happy()) image = cocoon;
	}
	
	private boolean happy(){
		return !FrameEngine.getSaveFile().getFlag("FOUND_FLAME") && NUM_GRUBS_STILL_LOST == 0;
	}
	
	private void checkGrubsLost(int num){
		if (FrameEngine.getSaveFile().getFlag("RETURNED_GRUB" + num)){
			NUM_GRUBS_STILL_LOST--;
		}
		
	}
	
	@SuppressWarnings("serial")
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(
			new DialogueTree(this, "grubmom", new HashMap<String, String>(){{
				put("NUM_GRUBS_STILL_LOST", numToWordMap.get(NUM_GRUBS_STILL_LOST));
				put("AREIS", NUM_GRUBS_STILL_LOST == 1 ? "is" : "are");
			}})
		);
	}

}
