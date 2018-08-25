package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

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

	@Override
	public void dispose() {
		/**/
	}

}
