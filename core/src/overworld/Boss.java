package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class Boss extends Enemy {
	
	private final TextureRegion texture = new TextureRegion(new Texture("sprites/overworld/boss.png"));

	public Boss(float x, float y) {
		super(x, y);
	}
	
	@Override
	protected void attempt_start_battle(){
		if (FrameEngine.attempt_start_boss_battle()){
			mark_delete();
			FrameEngine.getPlayer().reset_invincibility();
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
