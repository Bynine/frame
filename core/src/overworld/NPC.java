package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NPC extends InteractableEntity{
	
	private final TextureRegion texture;

	public NPC(float x, float y, String id, String text) {
		super(x, y, text);
		if (id.equals("DESC")){
			texture = null;
		}
		else{
			texture = new TextureRegion(new Texture("sprites/overworld/" + id + ".png"));
		}
	}

	@Override
	public TextureRegion getImage(){
		return texture;
	}

	@Override
	public void dispose(){
		if (texture != null){
			texture.getTexture().dispose();
		}
	}

}
