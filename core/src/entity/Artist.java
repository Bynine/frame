package entity;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;

import main.EntityHandler;
import main.FrameEngine;
import text.DialogueTree;
import timer.Timer;

public class Artist extends NPC {
	
	private final Timer whistleTimer = new Timer(45);
	private boolean happy;

	public Artist(float x, float y, int interactXDisp, int interactYDisp, int width, int height) {
		super(x, y, interactXDisp, interactYDisp, width, height, "ARTIST", "artist", "artist", Layer.NORMAL);
		if (FrameEngine.getSaveFile().getCounter(Flower.grownFlowers) > 0){
			FrameEngine.getSaveFile().setFlag("GROWN_ENOUGH_FLOWERS", true);
		}
		happy = FrameEngine.getSaveFile().getFlag("GROWN_ENOUGH_FLOWERS");
		currentAnim = happy ? 1 : 0;
		defaultAnim = currentAnim;
		if (!happy){
			voiceUrl = "dip";
		}
		timerList.add(whistleTimer);
	}
	
	@Override
	public void update(){
		super.update();
		if (whistleTimer.timeUp() && happy && currentAnim == 1){
			whistleTimer.reset();
			EntityHandler.addEntity(new Note(position.x + 16, position.y + 40, 30));
		}
	}
	
	public void interact(){
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "artist", new HashMap<String, String>(){{
					put("THANKS_LINE", 
							FrameEngine.getSaveFile().getFlag("TALK_ARTIST") ?
							"I am so sorry for my outburst from before" :
								"Thank you for what you've done");
					put("ARTIST_TREASURE", (!FrameEngine.getSaveFile().getFlag("FOUND_GOAL") ?
							Gdx.files.internal("dialogue/artist_treasure.txt").readString() :
							""));
				}})
			);
	}

}
