package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import main.FrameEngine;

public class MoveLight extends ImmobileEntity {
	
	private final TextureRegion light = new TextureRegion(new Texture("sprites/objects/movelight.png"));
	private float movement;
	private final Vector2 initPos;

	public MoveLight(float x, float y) {
		super(x, y);
		image = light;
		layer = Layer.OVERHEAD;
		initPos = new Vector2(position);
	}
	
	@Override
	public void update(){
		movement += 1.25f * FrameEngine.elapsedTime;
		position.x = initPos.x + (movement % FrameEngine.getArea().mapWidth);
		//System.out.println(position.toString());
		position.y += 0.15f * Math.sin(FrameEngine.getTime()/100.0f) * FrameEngine.elapsedTime;
	}

	@Override
	public void dispose() {
		light.getTexture().dispose();
	}

}
