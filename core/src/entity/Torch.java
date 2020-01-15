package entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import entity.Portal.Direction;
import main.Animator;
import main.AudioHandler;
import main.FrameEngine;
import text.DialogueTree;

public class Torch extends InteractableEntity {

	private final TextureRegion tex = new TextureRegion(new Texture("sprites/objects/torch.png"));
	private final ArrayList<Animation<TextureRegion>> anim = 
			Animator.createAnimation(15, "sprites/objects/torch_lit.png", 2, 1);
	private boolean lit;
	private final Sound lightSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/ignite.wav"));
	
	public Torch(float x, float y) {
		super(x, y, "");
		hitbox.width = tex.getRegionWidth();
		lit = FrameEngine.getSaveFile().getFlag("FOUND_FLAME");
	}
	
	@Override
	public void getMessage(String message) {
		super.getMessage(message);
		if (message.equals("LIGHT_FLAME")) {
			FrameEngine.getInventory().removeItem("FLAME");
			AudioHandler.playPitchedSound(lightSFX, 1.0f, 1.0f, true);
			lit = true;
		}
		if (message.equals("REFRESH")) {
			FrameEngine.initiateAreaChange(
					"DUNGEON_TOP", 
					new Vector2(11.5f * FrameEngine.TILE, 11.5f * FrameEngine.TILE),
					Direction.UP);
		}
	}
	
	@Override
	public void updateImage() {
		image = lit ? anim.get(0).getKeyFrame(FrameEngine.getTime()) : tex;
	}
	
	@Override
	public void interact() {
		FrameEngine.startDialogueTree(
				new DialogueTree(this, "torch")
			);
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
		Animator.freeAnimation(anim);
		lightSFX.dispose();
	}

}
