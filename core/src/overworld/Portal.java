package overworld;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;

public class Portal extends ImmobileEntity {

	final String destArea;
	final Vector2 destLocation = new Vector2();

	public Portal(float x, float y, float width, float height, 
			String destination, double x_dest, double y_dest) {
		super(x, y);
		this.destArea = destination;
		destLocation.set(
				(int) (x_dest * FrameEngine.TILE), 
				(int) ( (y_dest + 1) * FrameEngine.TILE)
				);
		hitbox.setSize(width, height);
	}

	@Override
	public void update(){
		super.update();
		if (touching_player(hitbox)){
			FrameEngine.initiate_area_change(destArea, destLocation);
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
