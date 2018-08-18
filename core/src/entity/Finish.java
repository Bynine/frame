package entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Finish extends Entity {

	public Finish(float x, float y, int width, int height) {
		super(x, y);
		hitbox.setSize(width, height);
	}

	@Override
	public void update(){
		super.update();
		if (touchingPlayer(hitbox)){
			System.out.println("FINISH???");
		}
	}

	@Override
	public TextureRegion getImage(){
		return null;
	}

	@Override
	public void dispose() {
		/**/
	}

}
