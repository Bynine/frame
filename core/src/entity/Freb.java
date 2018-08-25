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

public class Freb extends NPC {

	private final ArrayList<Animation<TextureRegion>> 
	sulk = Animator.createAnimation(45, "sprites/critters/frebnaked.png", 2, 1),
	shell1 = Animator.createAnimation(30, "sprites/critters/frebshell1.png", 2, 1),
	shell2 = Animator.createAnimation(30, "sprites/critters/frebshell2.png", 2, 1),
	shell3 = Animator.createAnimation(30, "sprites/critters/frebshell3.png", 2, 1);
	/**
	 * If 0, the Freb has no shell.
	 * 1-3 indicate which shell it has.
	 */
	private int shell = 0;
	private final String flag;
	private final Timer chirpTimer = new Timer(60);
	private final Sound 
	chirp = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/croak.wav"));

	public Freb(float x, float y, int width, int height, String flag) {
		super(x, y, 0, 0, width, height, "FREB", "dummy", "");
		shell = FrameEngine.getSaveFile().getCounter(flag);
		this.flag = flag;
		timerList.add(chirpTimer);
	}
	
	@Override
	public void interact(){
		if (hasShell()){
			FrameEngine.startDialogueTree(new DialogueTree(this, "frebshell"));
		}
		else{
			FrameEngine.startDialogueTree(
					new DialogueTree(this, "freb", new HashMap<String, String>(){{
						put("SHELL_FREB_FLAG", flag);
						}})
					);
		}
	}

	@Override
	public void updateImage(){
		if (hasShell()) {
			if (chirpTimer.timeUp()){
				AudioHandler.playPositionalSound(this, chirp);
				chirpTimer.reset();
			}
			if (shell == 1) image = shell1.get(0).getKeyFrame(FrameEngine.getTime());
			if (shell == 2) image = shell2.get(0).getKeyFrame(FrameEngine.getTime());
			if (shell == 3) image = shell3.get(0).getKeyFrame(FrameEngine.getTime());
		}
		else image = sulk.get(0).getKeyFrame(FrameEngine.getTime());
	}
	
	@Override
	public void receiveMessage(String message){
		switch(message){
		case "SHELL1": setShell(message, 1); break;
		case "SHELL2": setShell(message, 2); break;
		case "SHELL3": setShell(message, 3); break;
		}
	}
	
	private void setShell(String item, int num){
		shell = num;
		FrameEngine.getSaveFile().addToCounter(num, flag);
		FrameEngine.getInventory().removeItem(item);
	}
	
	private boolean hasShell(){
		return shell >= 1;
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(sulk, shell1, shell2, shell3);
		chirp.dispose();
	}

}
