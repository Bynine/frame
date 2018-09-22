package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import main.FrameEngine;

public class TrapDoor extends PortalHole {
	
	private final Sound open = Gdx.audio.newSound(Gdx.files.internal("sfx/door_open.wav"));

	public TrapDoor(float x, float y, String destination, double destX, double destY) {
		super(x, y, "", destination, destX, destY);
		marker = null;
		hole = new TextureRegion(new Texture("sprites/objects/trapdoor.png"));
		checkOpened();
	}
	
	@Override
	public void update(){
		super.update();
		checkOpened();
	}
	
	private void checkOpened(){
		boolean toOpen = 
				!FrameEngine.getSaveFile().getMapping("STAND1").isEmpty() &&
				!FrameEngine.getSaveFile().getMapping("STAND2").isEmpty() &&
				!FrameEngine.getSaveFile().getMapping("STAND3").isEmpty() &&
				!FrameEngine.getSaveFile().getMapping("STAND4").isEmpty()
				;
		if (!opened && toOpen){
			AudioHandler.playSound(open);
		}
		opened = toOpen;
	}

	@Override
	public void dispose() {
		hole.getTexture().dispose();
		open.dispose();
	}

}
