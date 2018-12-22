package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.AudioHandler;

public class Grass extends ImmobileEntity {

	private final TextureRegion grass = new TextureRegion(new Texture("sprites/objects/grass.png"));
	private final TextureRegion flat = new TextureRegion(new Texture("sprites/objects/grass_flat.png"));
	private final Sound 
	walkThrough = Gdx.audio.newSound(Gdx.files.internal("sfx/grass.wav"));
	private boolean justWalkedThrough = true;

	public Grass(float x, float y) {
		super(x, y);
		collides = false;
		hitbox.height = 12;
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
			if (justWalkedThrough){
				AudioHandler.playSoundVariedPitch(walkThrough);
				justWalkedThrough = false;
			}
		}
		else{
			image = grass;
			justWalkedThrough = true;
		}
	}

	@Override
	public void dispose() {
		grass.getTexture().dispose();
		flat.getTexture().dispose();
		walkThrough.dispose();
	}

}
