package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class SaleSign extends InteractableEntity {
	
	private final TextureRegion sign = new TextureRegion(new Texture("sprites/objects/sale_sign.png"));

	public SaleSign(float x, float y) {
		super(x, y, "");
		image = sign;
	}
	
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(new DialogueTree(this, "sale_sign"));
	}

	@Override
	public void dispose() {
		sign.getTexture().dispose();
	}

}
