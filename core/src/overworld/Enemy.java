package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.FrameEngine;

/*
 * Overworld entity. On touch, begins battle.
 */
public abstract class Enemy extends Entity {

	private final TextureRegion texture = new TextureRegion(new Texture("sprites/overworld/enemy.png"));

	public Enemy(float x, float y) {
		super(x, y);
	}

	@Override
	public void update(){
		super.update();
		if (touching_player() && !FrameEngine.getPlayer().isInvincible()) attempt_start_battle();
	}
	
	protected void attempt_start_battle(){
		if (FrameEngine.attempt_start_battle()){ // Only remove this enemy if the battle started!
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
