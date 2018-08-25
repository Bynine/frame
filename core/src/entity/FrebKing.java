package entity;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import main.FrameEngine;
import text.DialogueTree;
import timer.Timer;

public class FrebKing extends NPC {
	
	int NUM_FREBS_WITHOUT_SHELLS = 3;
	private final Timer croakTimer = new Timer(120);
	private final Sound 
	croak = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/croak.wav"));
	private final ArrayList<Animation<TextureRegion>> 
	normal = Animator.createAnimation(60, "sprites/npcs/frebking.png", 2, 1),
	happy = Animator.createAnimation(60, "sprites/npcs/frebking_happy.png", 2, 1);

	public FrebKing(float x, float y, int width, int height) {
		super(x, y, 0, 0, width, height, "FREBKING", "dummy", "");
		checkFrebShellCounter(1);
		checkFrebShellCounter(2);
		checkFrebShellCounter(3);
		timerList.add(croakTimer);
	}
	
	/**
	 * If the counter is greater than 0, the freb has a shell, and so the count is decremented.
	 */
	private void checkFrebShellCounter(int num){
		if (FrameEngine.getSaveFile().getCounter("FREB_SHELL_" + num) > 0){
			NUM_FREBS_WITHOUT_SHELLS--;
		}
	}
	
	@Override
	public void update(){
		super.update();
		if (happy() && croakTimer.timeUp()){
			AudioHandler.playSound(croak);
			croakTimer.reset();
		}
	}
	
	@Override
	public void updateImage(){
		if (happy()) {
			image = happy.get(0).getKeyFrame(FrameEngine.getTime());
		}
		else image = normal.get(0).getKeyFrame(FrameEngine.getTime());
	}
	
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(
			new DialogueTree(this, "frebking", new HashMap<String, String>(){{
				put("NUM_FREBS_WITHOUT_SHELLS", Integer.toString(NUM_FREBS_WITHOUT_SHELLS));
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
