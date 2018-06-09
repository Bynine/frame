package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class Portal extends Entity {
	
	private final TextureRegion texture = new TextureRegion(new Texture("sprites/overworld/portal.png"));
	final String id;

	public Portal(int x, int y, String id) {
		super(x, y);
		this.id = id;
	}
	
	@Override
	public void update(){
		super.update();
		if (touching_player()){
			FrameEngine.initiate_area_change(id);
		}
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
