package entity;

import main.FrameEngine;

public class ArtistSnow extends NPC {

	public ArtistSnow(float x, float y, int interactXDisp, int interactYDisp, int width, int height, String id,
			String imagePath, String dialoguePath, Layer layer) {
		super(x, y, interactXDisp, interactYDisp, width, height, id, imagePath, dialoguePath, layer);
	}
	
	@Override
	public void getMessage(String message){
		super.getMessage(message);
		if (message.equals("RELAX")){
			defaultAnim = 4;
			FrameEngine.endCocoaTime();
		}
	}
	
}
