package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.FrameEngine;

/*
 * Overworld entity. On touch, begins battle.
 */
public abstract class Enemy extends Entity {

	private final TextureRegion texture = new TextureRegion(new Texture("sprites/overworld/enemy.png"));
	private final String id;

	public Enemy(float x, float y, String id) {
		super(x, y);
		this.id = id;
	}

	@Override
	public void update(){
		super.update();
		if (touching_player(hitbox) && !FrameEngine.getPlayer().isInvincible()) {
			attemptStartBattle();
		}
	}
	
	protected void attemptStartBattle(){
		if (FrameEngine.attemptStartEncounter(id)){ // Only remove this enemy if the battle started!
			setDelete();
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
