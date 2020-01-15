package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import main.FrameEngine;
import text.DialogueTree;

public class WallCrack extends InteractableEntity {
	
	private final TextureRegion tex = new TextureRegion(new Texture("sprites/objects/wallcrack.png"));
	private final TextureRegion tex_open = new TextureRegion(new Texture("sprites/objects/wallcrack_open.png"));
	private final Sound crack = Gdx.audio.newSound(Gdx.files.internal("sfx/crack.wav"));

	public WallCrack(float x, float y) {
		super(x, y, "");
		boolean opened = FrameEngine.getSaveFile().getFlag("BROKE_WALL");
		image = opened ? tex_open : tex;
	}
	
	@Override
	public void interact() {
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "wallcrack")
			);
	}
	
	@Override
	public void getMessage(String message) {
		super.getMessage(message);
		if (message.equals("SHATTER")) {
			AudioHandler.playSoundVariedPitch(crack, 0.2f);
			image = tex_open;
		}
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
		tex_open.getTexture().dispose();
		crack.dispose();
	}

}
