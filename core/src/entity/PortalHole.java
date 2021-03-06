package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import entity.Portal.Direction;
import main.FrameEngine;
import text.DialogueTree;

public class PortalHole extends InteractableEntity {

	protected boolean opened;
	protected TextureRegion marker = new TextureRegion(new Texture("sprites/objects/hole_marker.png"));
	protected TextureRegion hole = new TextureRegion(new Texture("sprites/objects/hole.png"));
	final String destArea;
	final Vector2 destLocation = new Vector2();
	final String flag;
	private final Direction direction;

	public PortalHole(float x, float y, String flag, String destination, 
			double destX, double destY, Direction direction) {
		super(x, y, "");
		opened = FrameEngine.getSaveFile().getFlag(flag);
		updateImage();
		this.destArea = destination;
		this.direction = direction;
		this.flag = flag;
		destLocation.set(
				(int) (destX * FrameEngine.TILE), 
				(int) ( (destY + 1) * FrameEngine.TILE)
				);
		interactHitbox.setSize(32);
		hitbox.setSize(32);
		collides = false;
		layer = Layer.BACK;
	}

	public void updateImage(){
		image = opened ? hole : marker;
	}

	public void interact(){
		if (!opened){
			FrameEngine.startDialogueTree(new DialogueTree(this, "hole"));
		}
		else{
			enter();
		}
	}
	
	protected void enter(){
		FrameEngine.startDialogueTree(new DialogueTree(this, "enter_hole"));
	}

	@Override
	public void getMessage(String message){
		if (message.equals("DIG")){
			//System.out.println("This hole to " + destArea + " is open!");
			opened = true;
			FrameEngine.getSaveFile().setFlag(flag, true);
			FrameEngine.startDialogueTree(new DialogueTree("It looks just wide enough to enter."));
		}
		if (message.equals("ENTER")){
			FrameEngine.endDialogueTree();
			FrameEngine.initiateAreaChange(destArea, destLocation, direction);
		}
	}

	public void dispose(){
		marker.getTexture().dispose();
		hole.getTexture().dispose();
	}

}
