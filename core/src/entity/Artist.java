package entity;

import java.util.HashMap;

import main.FrameEngine;
import text.DialogueTree;

public class Artist extends NPC {

	public Artist(float x, float y, int interactXDisp, int interactYDisp, int width, int height) {
		super(x, y, interactXDisp, interactYDisp, width, height, "ARTIST", "artist", "artist", Layer.NORMAL);
		if (FrameEngine.getSaveFile().getCounter(Flower.grownFlowers) > 0){
			FrameEngine.getSaveFile().setFlag("GROWN_ENOUGH_FLOWERS", true);
		}
		boolean happy = FrameEngine.getSaveFile().getFlag("GROWN_ENOUGH_FLOWERS");
		currentAnim = happy ? 1 : 0;
		defaultAnim = currentAnim;
		if (!happy){
			voiceUrl = "dip";
		}
	}
	
	public void interact(){
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "artist", new HashMap<String, String>(){{
					put("THANKS_LINE", 
							FrameEngine.getSaveFile().getFlag("TALK_ARTIST") ?
							"I am so sorry for my outburst from before" :
								"Thank you for what you've done");
				}})
			);
	}

}
