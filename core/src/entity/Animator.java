package entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

/**
 * Handles creation and destruction of animations.
 */
public class Animator {

	/**
	 * Create a list of animations.
	 */
	public static ArrayList<Animation<TextureRegion>> createAnimation(
			float frame, String path, int cols, int rows){
		ArrayList<Animation<TextureRegion>> lst = new ArrayList<Animation<TextureRegion>>();
		TextureRegion sheet = new TextureRegion(new Texture(path));
		TextureRegion[][] frames = 
				sheet.split(sheet.getRegionWidth()/cols, sheet.getRegionHeight()/rows);
		for (int row = 0; row < rows; ++row){
			Animation<TextureRegion> anim = new Animation<TextureRegion>(frame, frames[row]);
			anim.setPlayMode(PlayMode.LOOP);
			lst.add(anim);
		}
		return lst;
	}
	
	/**
	 * Disposes all frames of animation list.
	 */
	public static void freeAnimation(ArrayList<Animation<TextureRegion>> anims){
		for (Animation<TextureRegion> anim: anims){
			for (TextureRegion tr: anim.getKeyFrames()){
				tr.getTexture().dispose();
			}
		}
	}
	
}
