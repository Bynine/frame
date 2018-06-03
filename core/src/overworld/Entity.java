package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;

public abstract class Entity {

	protected final Vector2 position = new Vector2();
	protected final Vector2 velocity = new Vector2();
	private final TextureRegion sample = new TextureRegion(new Texture("sprites/overworld/dummy.png"));
	private final float speed = 5.0f;
	
	public void update(){
		position.add((FrameEngine.elapsed_time) * speed * velocity.x,
				(FrameEngine.elapsed_time) * speed * velocity.y);
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public Vector2 getVelocity(){
		return velocity;
	}
	
	/**
	 * Gets the current texture region.
	 */
	public TextureRegion getImage(){
		return sample;
	}
	
	/**
	 * Dispose all contained texture regions.
	 */
	public void dispose(){
		sample.getTexture().dispose();
	}
}
