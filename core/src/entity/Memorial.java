package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.DialogueTree;

public class Memorial extends InteractableEntity {
	
	private TextureRegion texture = new TextureRegion(new Texture("sprites/objects/memorial.png"));
	private final boolean risen;

	public Memorial(float x, float y, float width, float height) {
		super(x, y, "");
		risen = Walkway.checkRisen();
		if (risen){
			setRemove();
		}
		hitbox.setSize(width, height);
	}
	
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(new DialogueTree(this, "memorial"));
	}
	
	@Override
	public void updateImage(){
		image = risen ? null : texture;
	}

	@Override
	public void dispose() {
		texture.getTexture().dispose();
	}

}
