package entity;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;

import main.FrameEngine;
import text.DialogueTree;

public class Pumpkin extends NPC {

	public Pumpkin(float x, float y, int interactXDisp, int interactYDisp, int width, int height, String id,
			String imagePath, String dialoguePath, Layer layer) {
		super(x, y, interactXDisp, interactYDisp, width, height, id, imagePath, dialoguePath, layer);
		currentAnim = FrameEngine.getSaveFile().getFlag("CAFE_REWARD") ? 2 : 0;
	}
	
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "pumpkin", new HashMap<String, String>(){{
					put("PUMPKINQUERY", Gdx.files.internal("dialogue/pumpkinquery.txt").readString());
				}})
			);
	}
	
	@Override
	public void getMessage(String message){
		System.out.println(message);
		super.getMessage(message);
		if (message.equals("RELAX")){
			defaultAnim = 2;
		}
	}


}
