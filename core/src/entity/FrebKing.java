package entity;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.AudioHandler;
import main.FrameEngine;
import text.DialogueTree;
import timer.Timer;

public class FrebKing extends NPC {

	int NUM_FREBS_WITHOUT_SHELLS = 3;
	private final Timer croakTimer = new Timer(30);
	private final Timer singTimer = new Timer(0);
	private int singCounter = 0;
	private int singCounterB = 0;
	private final Sound 
	croak = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/croak.wav"));
	private final ArrayList<Animation<TextureRegion>> sing = 
			Animator.createAnimation(30, "sprites/npcs/frebking/2.png", 8, 1);

	public FrebKing(float x, float y, int width, int height) {
		super(x, y, 0, 0, width, height, "FREBKING", "frebking", "", Layer.NORMAL);
		checkFrebShellCounter(1);
		checkFrebShellCounter(2);
		checkFrebShellCounter(3);
		timerList.add(croakTimer);
		timerList.add(singTimer);
	}

	/**
	 * If the counter is greater than 0, the freb has a shell, and so the count is decremented.
	 */
	private void checkFrebShellCounter(int num){
		if (FrameEngine.getSaveFile().getCounter("FREB_SHELL_" + num) > 0){
			NUM_FREBS_WITHOUT_SHELLS--;
		}
		if (NUM_FREBS_WITHOUT_SHELLS <= 0){
			FrameEngine.getSaveFile().setFlag("FREBKING_HAPPY", true);
		}
	}

	@Override
	public void update(){
		super.update();
		if (happy() && croakTimer.timeUp()){
			singTimer.countDown();
			float pitch = 0.5f + (0.2f * 
					((singCounter >= 4) ? (singCounter * 0.66f) : singCounter) - (singCounterB * 0.125f));
			AudioHandler.playPitchedSound(croak, pitch, 1, false);
			croakTimer.reset();
			singCounter++;
			if (singCounter >= 8) {
				singCounter = 0;
				singCounterB++;
			}
			if (singCounterB > 1){
				singCounterB = 0;
			}
		}
	}

	@Override
	public void updateImage(){
		if (happy()) {
			image = sing.get(0).getKeyFrame(singTimer.getCounter());
		}
		else image = anims.get(0).get(0).getKeyFrame(singTimer.getCounter());
	}

	@SuppressWarnings("serial")
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "frebking", new HashMap<String, String>(){{
					put("NUM_FREBS_WITHOUT_SHELLS", 
							numToWordMap.get(NUM_FREBS_WITHOUT_SHELLS).toUpperCase());
					put("FREBKING_TREASURE", (!FrameEngine.getSaveFile().getFlag("FOUND_GOAL") ?
							Gdx.files.internal("dialogue/frebking_treasure.txt").readString() :
							""));
				}})
				);
	}

	private boolean happy(){
		return NUM_FREBS_WITHOUT_SHELLS == 0;
	}

	@Override
	public void dispose(){
		super.dispose();
		croak.dispose();
	}

}
