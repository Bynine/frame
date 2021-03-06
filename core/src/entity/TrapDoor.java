package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import entity.Portal.Direction;
import main.AudioHandler;
import main.FrameEngine;

public class TrapDoor extends PortalHole {
	
	private final Sound open = Gdx.audio.newSound(Gdx.files.internal("sfx/door_open.wav"));

	public TrapDoor(float x, float y, String destination, double destX, double destY) {
		super(x, y, "", destination, destX, destY, Direction.DOWN);
		marker = null;
		canInteract = false;
		hole = new TextureRegion(new Texture("sprites/objects/trapdoor.png"));
		checkOpened(false);
	}
	
	@Override
	public void update(){
		super.update();
		checkOpened(true);
	}
	
	@Override
	public void interact(){
		if (opened) super.interact();
	}
	
	private void checkOpened(boolean b){
		boolean toOpen = 
				!FrameEngine.getSaveFile().getMapping("STAND1").isEmpty() &&
				!FrameEngine.getSaveFile().getMapping("STAND2").isEmpty() &&
				!FrameEngine.getSaveFile().getMapping("STAND3").isEmpty() &&
				!FrameEngine.getSaveFile().getMapping("STAND4").isEmpty()
				;
		if (!opened && toOpen){
			if (b) AudioHandler.playSoundVariedPitch(open);
			canInteract = true;
		}
		opened = toOpen;
	}

	@Override
	public void dispose() {
		hole.getTexture().dispose();
		open.dispose();
	}

}
