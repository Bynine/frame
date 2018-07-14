package overworld;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.TSVReader;
import main.FrameEngine;

public class NPC extends InteractableEntity{
	
	private final ArrayList<Animation<TextureRegion>> anim;
	private final TextureRegion texture;

	public NPC(float x, float y, 
			int interactYDisp,
			int width, int height, 
			String id, String image, String text) {
		super(x, y, text);
		String[] data = new TSVReader().loadDataByID(id, TSVReader.NPC_URL);
		voiceUrl = data[2];
		boolean animate = Boolean.parseBoolean(data[3]);
		if (null == image){
			anim = null;
			texture = null;
			hitbox.setSize(0, 0);
		}
		else if (animate){
			anim = Animator.create_animation(30, "sprites/overworld/" + image + ".png", 2, 1);
			texture = null;
			hitbox.setSize(width, height);
		}
		else{
			anim = null;
			texture = new TextureRegion(new Texture(
					Gdx.files.internal("sprites/overworld/" + image + ".png"
							)));
			hitbox.setSize(width, height);
		}
		interactHitbox.setSize(width, height);
		this.interactYDisp = interactYDisp;
	}

	@Override
	public TextureRegion getImage(){
		if (null == anim) {
			return texture;
		}
		else{
			return anim.get(0).getKeyFrame(FrameEngine.getTime());
		}
	}

	@Override
	public void dispose(){
		if (null != anim) {
			Animator.free_animation(anim);
		}
		if (null != texture) {
			texture.getTexture().dispose();
		}
	}

}
