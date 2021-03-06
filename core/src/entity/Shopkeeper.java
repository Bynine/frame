package entity;

import main.FrameEngine;
import text.DialogueTree;

public class Shopkeeper extends NPC {

	public Shopkeeper(float x, float y, int interactXDisp, int interactYDisp, int width, int height, String id,
			String imagePath, String dialoguePath) {
		super(x, y, interactXDisp, interactYDisp, width, height, id, imagePath, dialoguePath, Layer.NORMAL);
	}

	@Override
	public void interact(){
		FrameEngine.getShopMenu().open();
		if (!FrameEngine.getArea().frost && FrameEngine.getSaveFile().getFlag("ENTERED_SHRINE")){
			if (FrameEngine.getShopMenu().outOfStock()){
				FrameEngine.getSaveFile().setFlag("OUT_OF_STOCK", true);
			}
			FrameEngine.startDialogueTree(new DialogueTree(this, "shop_menu"));
		}
		else super.interact();
	}

	@Override
	public void getMessage(String message){
		super.getMessage(message);
		if (message.equals("BUY")){
			FrameEngine.startShop();
		}
	}

}
