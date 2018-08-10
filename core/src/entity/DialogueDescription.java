package entity;

import main.FrameEngine;
import text.DialogueTree;

public class DialogueDescription extends Description {
	
	private final String dialoguePath;

	public DialogueDescription(float x, float y, float width, float height, String dialoguePath) {
		super(x, y, width, height, "");
		this.dialoguePath = dialoguePath;
	}
	
	@Override
	public void interact(){
		FrameEngine.startDialogueTree(new DialogueTree(null, dialoguePath));
	}

}
