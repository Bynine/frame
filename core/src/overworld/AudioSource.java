package overworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

/**
 * Plays audio. The closer the player is, the louder the audio.
 */
public class AudioSource extends ImmobileEntity {
	
	final Music audio;

	public AudioSource(float x, float y, String audioFileName) {
		super(x, y);
		audio = Gdx.audio.newMusic(Gdx.files.internal("music/" + audioFileName + ".mp3"));
	}
	
	@Override
	public TextureRegion getImage(){
		return null;
	}
	
	/**
	 * Calculates volume as distance between this position and player.
	 */
	public float getVolume(){
		return 1.0f - (position.dst(FrameEngine.getPlayer().getPosition())/(FrameEngine.TILE*9.2f));
	}
	
	public Music getAudio(){
		return audio;
	}

	@Override
	public void dispose() {
		audio.dispose();
	}

}
