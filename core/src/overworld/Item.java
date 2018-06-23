package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class Item extends InteractableEntity {
	
	private final TextureRegion texture = new TextureRegion(new Texture("sprites/overworld/item.png"));

	public Item(float x, float y, String id) {
		super(x, y, id);
		hitbox.setSize(24, 16);
	}
	
	@Override
	public void interact() {
		setDelete();
		FrameEngine.setTextbox("Wow, you picked up " + text + "!");
	}

	@Override
	public TextureRegion getImage(){
		return texture;
	}

	@Override
	public void dispose(){
		texture.getTexture().dispose();
	}

}
