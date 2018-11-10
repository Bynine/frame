package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import main.EntityHandler;
import main.FrameEngine;
import text.DialogueTree;
import timer.Timer;

public class Instrument extends InteractableEntity {

	private final Sound sound;
	private final Timer playTimer = new Timer(10);
	private final String sfxUrl;
	private int timesPlayed = 0;

	public Instrument(float x, float y, String sfxUrl) {
		super(x, y, "");
		sound = Gdx.audio.newSound(Gdx.files.internal("sfx/" + sfxUrl + ".wav"));
		timerList.add(playTimer);
		collides = false;
		this.sfxUrl = sfxUrl;
	}
	
	@Override
	public TextureRegion getImage(){
		return null;
	}

	@Override
	public void interact(){
		if (playTimer.timeUp()){
			int keyRange = 11;
			int key = (int) ((keyRange/2) - (Math.random() * (keyRange)));
			float pitch = (float)Math.pow(1.1091f,key);
			AudioHandler.playPitchedSound(sound, pitch, 1.0f);
			playTimer.reset();
			EntityHandler.addEntity(new Note(position.x + 16, position.y + 24, 20));
			timesPlayed++;
			if (sfxUrl.equals("lute") && 
					((timesPlayed == 16 && !FrameEngine.getSaveFile().getFlag("PLAY_LUTE_LOTS")) ||
							timesPlayed == 24)){
				FrameEngine.startDialogueTree(new DialogueTree(null, "play_lute_lots"));
			}
		}
	}

	@Override
	public void dispose() {
		sound.dispose();
	}

}
