package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;

public class Statue extends InteractableEntity {

	private Color color = Color.GREY;
	private final Color correctColor;
	private final TextureRegion 
	grey = new TextureRegion(new Texture("sprites/shrine_statue.png")),
	red = new TextureRegion(new Texture("sprites/shrine_statue_red.png")),
	yellow = new TextureRegion(new Texture("sprites/shrine_statue_yellow.png")),
	green = new TextureRegion(new Texture("sprites/shrine_statue_green.png")),
	blue = new TextureRegion(new Texture("sprites/shrine_statue_blue.png"));

	public Statue(float x, float y, String cc) {
		super(x, y, "");
		image = new TextureRegion(new Texture("sprites/shrine_statue.png"));
		correctColor = Color.valueOf(cc);
		if (correctColor == Color.BLUE || correctColor == Color.GREEN) flipped = true;
	}

	@Override
	public void interact() {
		// TODO: play sound
		switch(color){
		case GREY: color = Color.RED; break;
		case RED: color = Color.YELLOW; break;
		case YELLOW: color = Color.GREEN; break;
		case GREEN: color = Color.BLUE; break;
		case BLUE: color = Color.RED; break;
		}
		FrameEngine.getSaveFile().setFlag(getFlag(correctColor), isCorrect());
	}

	@Override
	public void updateImage(){
		switch(color){
		case GREY: image = grey; break;
		case RED: image = red; break;
		case YELLOW: image = yellow; break;
		case GREEN: image = green; break;
		case BLUE: image = blue; break;
		}
	}

	@Override
	public void dispose() {
		grey.getTexture().dispose();
		red.getTexture().dispose();
		yellow.getTexture().dispose();
		green.getTexture().dispose();
		blue.getTexture().dispose();
	}

	private boolean isCorrect(){
		return color == correctColor;
	}
	
	/*
	 * The flag set by this statue when it's been set to the correct color.
	 */
	public static String getFlag(Color color){
		return "STATUE_" + color.toString() + "_CORRECT";
	}

	public enum Color{
		GREY, RED, YELLOW, GREEN, BLUE
	}

}
