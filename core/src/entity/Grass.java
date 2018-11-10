package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Grass extends ImmobileEntity {

	private final TextureRegion grass = new TextureRegion(new Texture("sprites/objects/grass.png"));
	private final TextureRegion flat = new TextureRegion(new Texture("sprites/objects/grass_flat.png"));

	public Grass(float x, float y) {
		super(x, y);
		collides = false;
	}
	
	@Override
	public void update(){
		super.update();
		hitbox.x += 32;
	}

	@Override
	public void updateImage(){
		if (touchingPlayer(hitbox)) {
			image = flat;
		}
		else{
			image = grass;
		}
	}

	@Override
	public void dispose() {
		grass.getTexture().dispose();
		flat.getTexture().dispose();
	}

}
