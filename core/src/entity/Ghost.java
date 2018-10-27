package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.Animator;
import main.FrameEngine;

public class Ghost extends ImmobileEntity {
	
	private final ArrayList<Animation<TextureRegion>> ascend = 
			Animator.createAnimation(30, "sprites/npcs/ghost/ascend.png", 2, 1);

	public Ghost() {
		super(FrameEngine.getPlayer().getPosition().x, FrameEngine.getPlayer().getPosition().y + 20);
		collides = false;
		layer = Layer.OVERHEAD;
	}
	
	@Override
	public void updateImage(){
		image = ascend.get(0).getKeyFrame(FrameEngine.getTime());
	}
	
	@Override
	public void update(){
		super.update();
		zPosition += 2 * FrameEngine.elapsedTime;
		position.x += 2 * Math.sin(FrameEngine.getTime()/12.0);
	}

	@Override
	public void dispose() {
		Animator.freeAnimation(ascend);
	}

}
