package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Painting extends InteractableEntity {

	public Painting(float x, float y, String text, String id) {
		super(x, y, text);
		image = new TextureRegion(new Texture("sprites/paintings/" + id + ".png"));
	}

	@Override
	public void dispose() {
		image.getTexture().dispose();
	}

}
