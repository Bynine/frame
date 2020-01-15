package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import main.AudioHandler;
import main.EntityHandler;
import main.FrameEngine;

public class ArtistSnow extends NPC {
	
	private final Sound desnowSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/desnow.wav"));

	public ArtistSnow(float x, float y, int interactXDisp, int interactYDisp, int width, int height, String id,
			String imagePath, String dialoguePath, Layer layer) {
		super(x, y, interactXDisp, interactYDisp, width, height, id, imagePath, dialoguePath, layer);
	}
	
	@Override
	public void getMessage(String message){
		super.getMessage(message);
		if (message.equals("RELAX")){
			for (int ii = 0; ii < 4; ++ii) {
				EntityHandler.addEntity(new SnowParticle(this.getPosition().x + 12, this.getPosition().y, ii));
			}
			AudioHandler.playSoundVariedPitch(desnowSFX);
			defaultAnim = 4;
			FrameEngine.endCocoaTime();
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		desnowSFX.dispose();
	}
	
}
