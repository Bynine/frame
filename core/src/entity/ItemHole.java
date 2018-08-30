package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class ItemHole extends Item {
	
	private final TextureRegion marker = new TextureRegion(new Texture("sprites/objects/hole_marker.png"));

	public ItemHole(float x, float y, String id, String flag) {
		super(x, y, id, flag);
		image = marker;
		collides = false;
		layer = Layer.BACK;
	}
	
	public void interact(){
		FrameEngine.startDialogueTree(new DialogueTree(this, "hole"));
	}
	
	@Override
	public void getMessage(String message){
		if (message.equals("DIG")){
			super.interact();
		}
	}
	
	public void dispose(){
		marker.getTexture().dispose();
	}

}
