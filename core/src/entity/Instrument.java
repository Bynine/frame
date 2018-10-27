package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import timer.Timer;

public class Instrument extends InteractableEntity {

	private final Sound sound;
	private final Timer playTimer = new Timer(10);

	public Instrument(float x, float y, String sfxUrl) {
		super(x, y, "");
		sound = Gdx.audio.newSound(Gdx.files.internal("sfx/" + sfxUrl + ".wav"));
		timerList.add(playTimer);
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
		}
	}

	@Override
	public void dispose() {
		sound.dispose();
	}

}
