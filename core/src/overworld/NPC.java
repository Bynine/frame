package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NPC extends InteractableEntity{
	
	private final TextureRegion texture = new TextureRegion(new Texture("sprites/overworld/enemy.png"));

	public NPC(float x, float y, String text) {
		super(x, y, text);
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
