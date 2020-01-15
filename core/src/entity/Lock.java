package entity;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;
import main.FrameEngine;
import text.DialogueTree;

public class Lock extends InteractableEntity {
	
	private final TextureRegion 
		tex_closed = new TextureRegion(new Texture("sprites/objects/lock_closed.png")),
		tex_open = new TextureRegion(new Texture("sprites/objects/lock_open.png"));
	private final String id;
	private final int num;
	private final Sound unlock = Gdx.audio.newSound(Gdx.files.internal("sfx/unlock.wav"));

	public Lock(float x, float y, String id) {
		super(x, y, "");
		this.id = id;
		num = id.equals("LOCK_F1") ? 1 : 2;
		if (FrameEngine.getSaveFile().getFlag(id + "_OPEN")) {
			open();
		}
		else {
			image = tex_closed;
		}
		hitbox.width = FrameEngine.TILE * 2;
		interactHitbox.width = hitbox.width;
	}
	
	@SuppressWarnings("serial")
	@Override
	public void interact() {
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "lock", new HashMap<String, String>(){{
					put("WHICH_KEY_GUVNA", 
							"ITEM_KEY" + num);
				}})
			);
	}
	
	@Override
	public void getMessage(String message) {
		super.getMessage(message);
		if (message.equals("OPEN")) {
			 open();
			 AudioHandler.playVolumeSound(unlock, 0.5f);
			 FrameEngine.getInventory().removeItem("KEY" + num);
		}
	}
	
	private void open(){
		FrameEngine.getSaveFile().setFlag(id + "_OPEN", true);
		image = tex_open;
		hitbox.setSize(0);
		interactHitbox.setSize(0);
		collides = false;
		FrameEngine.getArea().refreshCollision();
		canInteract = false;
	}

	@Override
	public void dispose() {
		tex_closed.getTexture().dispose();
		tex_open.getTexture().dispose();
	}

}
