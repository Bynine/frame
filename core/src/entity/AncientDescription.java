package entity;

import main.FrameEngine;
import text.DialogueTree;

public class AncientDescription extends Description {
	
	private final boolean isTree;

	public AncientDescription(float x, float y, float width, float height, String text, boolean isTree) {
		super(x, y, width, height, text);
		this.isTree = isTree;
	}

	@Override
	public void interact() {
		if (FrameEngine.getInventory().hasItem("BOOK")) {
			if (isTree) {
				FrameEngine.startDialogueTree(
						new DialogueTree(this, text)
					);
			}
			else {
				super.interact();
			}
		}
		else {
			FrameEngine.startDialogueTree(
					new DialogueTree(this, "untranslated")
				);
		}
	}
}
