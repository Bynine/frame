package entity;

import main.FrameEngine;
import text.DialogueTree;

public class AncientDescription extends Description {

	public AncientDescription(float x, float y, float width, float height, String text) {
		super(x, y, width, height, text);
	}

	@Override
	public void interact() {
		if (FrameEngine.getInventory().hasItem("BOOK")) {
			super.interact();
		}
		else {
			FrameEngine.startDialogueTree(
					new DialogueTree(this, "untranslated")
				);
		}
	}
}
