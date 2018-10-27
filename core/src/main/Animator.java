package main;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

/**
 * Handles creation and destruction of animations.
 */
public class Animator {

	/**
	 * Create a list of animations that loop.
	 */
	public static ArrayList<Animation<TextureRegion>> createAnimation(
			float frame, String path, int cols, int rows){
		return createAnimation(frame, path, cols, rows, PlayMode.LOOP);
	}
	
	/**
	 * Create a list of animations.
	 */
	public static ArrayList<Animation<TextureRegion>> createAnimation(
			float frame, String path, int cols, int rows, PlayMode playMode){
		ArrayList<Animation<TextureRegion>> lst = new ArrayList<Animation<TextureRegion>>();
		TextureRegion sheet = new TextureRegion(new Texture(path));
		TextureRegion[][] frames = 
				sheet.split(sheet.getRegionWidth()/cols, sheet.getRegionHeight()/rows);
		for (int row = 0; row < rows; ++row){
			Animation<TextureRegion> anim = new Animation<TextureRegion>(frame, frames[row]);
			anim.setPlayMode(playMode);
			lst.add(anim);
		}
		return lst;
	}
	
	

	/**
	 * Disposes all frames of animation list.
	 */
	@SafeVarargs
	public static void freeAnimation(ArrayList<Animation<TextureRegion>>... anims){
		for (ArrayList<Animation<TextureRegion>> subAnims: anims){
			for (Animation<TextureRegion> anim: subAnims){
				for (TextureRegion tr: anim.getKeyFrames()){
					tr.getTexture().dispose();
				}
			}
		}
	}

	/**
	 * Creates a list of animation lists for an NPC to use to change their sprite dynamically.
	 */
	public static ArrayList<ArrayList<Animation<TextureRegion>>> createAnimationList(
			String[] data, 
			String imagePath
			) {
		ArrayList<ArrayList<Animation<TextureRegion>>> anims = new ArrayList<>();
		int animNumber = Integer.parseInt(data[4]);
		int animSpeed = Integer.parseInt(data[5]);
		String directoryPath = "sprites/npcs/" + imagePath + "/";
		FileHandle[] files = Gdx.files.internal(directoryPath).list();
		int ii = 0;
		if (files != null){
			try{
				for (ii = 0; ii < Integer.MAX_VALUE; ++ii){
					String path = directoryPath + ii + ".png";
					anims.add(createAnimation(
							animSpeed, 
							path, 
							2, 
							animNumber));
				}
			}
			/**
			 * Yes, I'm using an exception here as a way to end a loop.
			 * So sue me!
			 */
			catch (GdxRuntimeException e){
				if (ii == 0){
					FrameEngine.logger.warning(e.getClass() + " " + e.getMessage());
				}
				else if (FrameEngine.LOG){
					System.out.println(data[0] + " has " + (ii) + " animation(s)");
				}
			}
		}
		else{
			FrameEngine.logger.warning("No files found in directory: " + directoryPath);
		}
		return anims;
	}

}
