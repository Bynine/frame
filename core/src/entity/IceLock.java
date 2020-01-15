package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class IceLock extends InteractableEntity {

	private final TextureRegion tex = new TextureRegion(new Texture("sprites/objects/icelock.png"));
	
	public IceLock(float x, float y) {
		super(x, y, "");
		image = tex;
		interactHitbox.width = 48;
	}
	
	@Override
	public void update() {
		super.update();
		if (FrameEngine.getSaveFile().getFlag("FOUND_FLAME")) {
			this.setRemove();
		}
	}
	
	@Override
	public void interact() {
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "icelock")
			);
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
	}

}
