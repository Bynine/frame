package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Flower extends InteractableEntity {
	
	private final String id;
	private final TextureRegion dirt = new TextureRegion(new Texture("sprites/objects/dirt.png"));

	public Flower(float x, float y, String id) {
		super(x, y, "");
		this.id = id;
		image = new Sprite(dirt);
		collides = false;
		layer = Layer.BACK;
	}

	@Override
	public void dispose() {
		dirt.getTexture().dispose();
	}

}
