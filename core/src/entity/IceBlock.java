package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import entity.Player.ImageState;
import main.AudioHandler;
import main.FrameEngine;
import text.DialogueTree;
import timer.DurationTimer;

public class IceBlock extends InteractableEntity {
	
	private final TextureRegion tex = new TextureRegion(new Texture("sprites/objects/iceblock.png"));
	private final TextureRegion tex2 = new TextureRegion(new Texture("sprites/objects/iceblock2.png"));
	private final TextureRegion breakTex = new TextureRegion(new Texture("sprites/objects/iceblock_break.png"));
	private final String id;
	private final boolean b;
	private final DurationTimer shatterTimer = new DurationTimer(30);
	private final Sound shatter = Gdx.audio.newSound(Gdx.files.internal("sfx/shatter.wav"));

	public IceBlock(float x, float y, String id, boolean b) {
		super(x, y, "");
		image = b ? tex2 : tex;
		this.b = b;
		this.id = id;
		hitbox.width  = b ? FrameEngine.TILE : FrameEngine.TILE * 2;
		hitbox.height = b ? FrameEngine.TILE * 2 : FrameEngine.TILE;
		interactHitbox.setSize(hitbox.width, hitbox.height);
	}
	
	@Override
	public void update() {
		super.update();
		if (shatterTimer.timeUp()) {
			Player.setImageState(ImageState.NORMAL);
			this.setRemove();
		}
	}
	
	@Override
	public void updateImage() {
		image = timerList.contains(shatterTimer) ? breakTex : (b ? tex2 : tex);
	}
	
	@Override
	public void interact() {
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "iceblock")
			);
	}
	
	@Override
	public void getMessage(String message) {
		super.getMessage(message);
		if (message.equals("SHATTER")) {
			AudioHandler.playSoundVariedPitch(shatter, 0.5f);
			timerList.add(shatterTimer);
			FrameEngine.getSaveFile().setFlag("SHATTERED_" + id, true);
			Player.setImageState(ImageState.HAMMER);
		}

	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
		tex2.getTexture().dispose();
		breakTex.getTexture().dispose();
		shatter.dispose();
	}

}
