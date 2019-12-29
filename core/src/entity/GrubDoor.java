package entity;

import java.util.HashMap;

import main.EntityHandler;
import main.FrameEngine;
import text.DialogueTree;

public class GrubDoor extends Door {

	private boolean grub1, grub2, grub3 = false;

	public GrubDoor(float x, float y, String flag, String destination, double destX, double destY) {
		super(x, y, destination, destX, destY);
	}

	@SuppressWarnings("serial")
	@Override
	public void interact(){
		grub1 = checkGrub(1);
		grub2 = checkGrub(2);
		grub3 = checkGrub(3);
		if (
				FrameEngine.getSaveFile().getFlag("RETURNED_GRUB1") &&
				FrameEngine.getSaveFile().getFlag("RETURNED_GRUB2") &&
				FrameEngine.getSaveFile().getFlag("RETURNED_GRUB3")
				){
			FrameEngine.getSaveFile().setFlag("GRUB_REWARD", true);
		}
		if (grub1 || grub2 || grub3){
			FrameEngine.startDialogueTree(new DialogueTree(this, "enter_grub_hole", new HashMap<String, String>(){{
				put("CHECK_GRUB1", grub1 ? "[JUMPIN_GRUB1]" : "");
				put("CHECK_GRUB2", grub2 ? "[JUMPIN_GRUB2]" : "");
				put("CHECK_GRUB3", grub3 ? "[JUMPIN_GRUB3]" : "");
			}}));
		}
		else{
			super.interact();
		}
	}

	private boolean checkGrub(int num){
		String daGrub = "GRUB" + num;
		if (FrameEngine.getInventory().hasItem(daGrub)){
			FrameEngine.getSaveFile().setFlag("RETURNED_GRUB" + num, true);
			return true;
		}
		return false;
	}

	@Override
	public void getMessage(String message){
		super.getMessage(message);
		if (message.equalsIgnoreCase("ENTER")){
			super.interact();
		}
		if (message.startsWith("JUMPIN")){
			String daGrub = message.split("_")[1];
			int dur = 30;
			EntityHandler.addEntity(
					new Toss(
							FrameEngine.getPlayer().getPosition().x,
							FrameEngine.getPlayer().getPosition().y,
							daGrub,
							dur,
							this.getPosition()
							));
			FrameEngine.getInventory().removeItem(daGrub);
		}
	}

}
