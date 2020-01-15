package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import main.FrameEngine;
import text.DialogueTree;

public class PuzzleLock extends InteractableEntity {

	private final TextureRegion tex = new TextureRegion(new Texture("sprites/objects/puzzlelock.png"));
	private boolean open;
	private final Sound openSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/puzzle_open.wav"));
	private final Sound closeSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/puzzle_close.wav"));
	
	public PuzzleLock(float x, float y) {
		super(x, y, "");
		interactHitbox.width = 48;
		open = checkOpen();
		updateOpen(false);
	}
	
	@Override
	public void update() {
		super.update();
		updateOpen(true);
	}
	
	private void updateOpen(boolean update) {
		final float volume = 0.35f;
		boolean nowOpen = checkOpen();
		if (!nowOpen && (!update || (nowOpen != open))) {
			if (update) {
				AudioHandler.playSoundVariedPitch(closeSFX, volume);
				open = false;
			}
			image = tex;
			collides = true;
			canInteract = true;
			FrameEngine.getArea().refreshCollision();
		}
		else if (nowOpen && (!update || (nowOpen != open))) {
			if (update) {
				AudioHandler.playSoundVariedPitch(openSFX, volume);
				open = true;
			}
			image = null;
			collides = false;
			canInteract = false;
			FrameEngine.getArea().refreshCollision();
		}
	}
	
	private boolean checkOpen() {
		return FrameEngine.getSaveFile().getFlag("!BLOCK1_MOVED,!BLOCK2_MOVED,BLOCK3_MOVED,BLOCK4_MOVED");
	}
	
	@Override
	public void interact() {
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "puzzlelock")
			);
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
	}

}
