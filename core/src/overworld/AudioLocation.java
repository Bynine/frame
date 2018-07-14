package overworld;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import main.FrameEngine;

/**
 * Plays audio. The closer the player is, the louder the audio.
 */
public class AudioLocation extends ImmobileEntity {
	
	public final String audioFileName;
	public final String id;

	public AudioLocation(float x, float y, String audio, String id) {
		super(x, y);
		this.audioFileName = audio;
		this.id = id;
	}
	
	@Override
	public TextureRegion getImage(){
		return null;
	}
	
	/**
	 * Calculates volume as distance between this position and player.
	 */
	public float getVolume(){
		float volume = 1.1f - (position.dst(FrameEngine.getPlayer().getPosition())/(FrameEngine.TILE*15.2f));
		return MathUtils.clamp(volume, 0, 1.0f);
	}

	@Override
	public void dispose() {
		/**/
	}

}
