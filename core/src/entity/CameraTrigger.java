package entity;

import com.badlogic.gdx.math.Vector2;

import main.GraphicsHandler;

public class CameraTrigger extends ImmobileEntity{
	
	Vector2 change = new Vector2();

	public CameraTrigger(float x, float y, float width, float height, int yChange) {
		super(x, y);
		hitbox.setSize(width, height);
		change.y = yChange;
	}
	
	@Override
	public void update(){
		super.update();
		if (touchingPlayer(hitbox)){
			GraphicsHandler.setOffset(change);
		}
	}
	
	@Override
	public void updateImage(){
		image = null;
	}

	@Override
	public void dispose() {
		/**/
	}

}
