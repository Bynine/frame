package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import main.EntityHandler;
import main.FrameEngine;
import text.DialogueTree;

public class Block extends InteractableEntity {
	
	private final TextureRegion tex = new TextureRegion(new Texture("sprites/objects/block.png"));
	private final String id;
	private boolean moved, movingLeft, movingRight;
	private final float speed = 0.75f;
	private final Sound moveSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/move.wav"));

	public Block(float x, float y, String id) {
		super(x, y, "");
		this.id = id;
		image = tex;
		hitbox.height = 28;
		moved = FrameEngine.getSaveFile().getFlag(id + "_MOVED");
		if (moved) {
			position.x += FrameEngine.TILE;
		}
		collides = false;
	}
	
	@Override
	public void update() {
		super.update();
		if (movingLeft || movingRight) {
			final float dirMod = movingLeft ? -1 : 1;
			velocity.x = dirMod * speed;
			for (Entity en: EntityHandler.getEntities()) {
				if (en instanceof BumperBounce) {
					if (this.hitbox.overlaps(en.hitbox)) {
						this.position.x = en.getPosition().x - (dirMod * FrameEngine.TILE);
						movingLeft = false;
						movingRight = false;
						velocity.x = 0;
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void interact() {
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "block")
			);
	}
	
	@Override
	public void getMessage(String message) {
		super.getMessage(message);
		if (message.equals("MOVE")) {
			AudioHandler.playSoundVariedPitch(moveSFX);
			if (moved) movingLeft = true;
			else movingRight = true;
			moved = !moved;
			FrameEngine.getSaveFile().setFlag(id + "_MOVED", moved);
		}
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
	}

}
