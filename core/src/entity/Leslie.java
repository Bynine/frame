package entity;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;

import main.FrameEngine;
import text.DialogueTree;

public class Leslie extends NPC {

	public Leslie(float x, float y, int interactXDisp, int interactYDisp, int width, int height, String id,
			String imagePath, String dialoguePath, Layer layer) {
		super(x, y, interactXDisp, interactYDisp, width, height, id, imagePath, dialoguePath, layer);
	}
	
	public void getMessage(String message) {
		super.getMessage(message);
		if (message.equals("RELAX")) {
			defaultAnim = 5;
		}
	}
	
	@SuppressWarnings("serial")
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(
				new DialogueTree(this, dialoguePath, new HashMap<String, String>(){{
					put("REQUEST", Gdx.files.internal("dialogue/leslie_request.txt").readString());
				}})
			);
	}
}
