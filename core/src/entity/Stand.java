package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import main.AudioHandler;
import main.FrameEngine;
import main.ItemDescription;
import text.DialogueTree;

public class Stand extends InteractableEntity {

	private final String id;
	private ItemDescription placedItem = null;
	private final Sound ignite = Gdx.audio.newSound(Gdx.files.internal("sfx/ignite.wav"));

	public Stand(float x, float y, String id) {
		super(x, y, "");
		this.id = id;
		String placedItemId = FrameEngine.getSaveFile().getMapping(id);
		if (!placedItemId.isEmpty()){
			placedItem = new ItemDescription(placedItemId);
		};
		interactYDisp = -32;
		layer = Layer.FRONT;
		collides = false;
	}
	
	@Override
	public void updateImage(){
		if (hasItem()){
			image = placedItem.icon;
		}
		else{
			image = null;
		}
	}
	
	@Override
	public void interact(){
		if (hasItem()){
			FrameEngine.startDialogueTree(new DialogueTree(this, "stand_remove"));
		}
		else{
			FrameEngine.startDialogueTree(new DialogueTree(this, "stand"));
		}
	}

	@Override
	public void getMessage(String message){
		super.getMessage(message);
		if (message.equals("PLACED")){
			AudioHandler.playSound(ignite);
			placedItem = new ItemDescription(FrameEngine.getGivenItemID());
			FrameEngine.getSaveFile().setMapping(id, FrameEngine.getGivenItemID());
		}
		if (message.equals("RETRIEVE_ITEM")){
			FrameEngine.getInventory().addItem(placedItem.id);
			FrameEngine.getSaveFile().setMapping(id, "");
			placedItem = null;
		}
	}
	
	private boolean hasItem(){
		return null != placedItem;
	}

	@Override
	public void dispose() {
		if (hasItem()){
			placedItem.dispose();
		}
		ignite.dispose();
	}

}
