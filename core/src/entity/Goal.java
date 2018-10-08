package entity;

import main.FrameEngine;
import text.DialogueTree;

public class Goal extends Item {
	
	public static final String found = "FOUND_GOAL";

	public Goal(float x, float y, String flag) {
		super(x, y, "GOAL", flag);
	}
	
	@Override
	public void interact(){
		super.interact();
		FrameEngine.getSaveFile().setFlag(found, true);
	}
	
	@Override
	protected void get(){
		FrameEngine.startDialogueTree(new DialogueTree(new NPC("KAMI", "found_goal"), "found_goal"));
	}

}
