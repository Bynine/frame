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
	
	@SuppressWarnings("serial")
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "customer3", new HashMap<String, String>(){{
					put("CUST_3_STORY", Gdx.files.internal("dialogue/customer3_story.txt").readString());
					put("CUST_3_ORDER", Gdx.files.internal("dialogue/customer3_order.txt").readString());
				}})
			);
	}

}
