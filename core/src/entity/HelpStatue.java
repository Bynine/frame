package entity;

import main.FrameEngine;
import text.DialogueTree;

public class HelpStatue extends InteractableEntity {

	public HelpStatue(float x, float y) {
		super(x, y, "");
		image = null;
	}
	
	@Override
	public void interact() {
		if (FrameEngine.getSaveFile().getFlag("FOUND_GOAL") && !FrameEngine.getSaveFile().getFlag("GOT_ALL_TREASURES")) {
			StringBuilder text = new StringBuilder();
			text.append(addHint("FREBKING_REWARD", "Bequeath shells to the beach king's disciples.\n"));
			text.append(addHint("WORLD_REWARD", "Inspire the artist with a blooming flower.\n"));
			text.append(addHint("CAFE_REWARD", "Assist the troubled cafe owner.\n"));
			text.append(addHint("GRUB_REWARD", "Find the missing children of the tailor.\n"));
			text.append(addHint("GHOST_REWARD", "Reclaim the memories of the spirit northeast of town.\n"));
			text.append(addHint("CURSE_REWARD", "Plant each season's flowers then go north of the grave.\n"));
			FrameEngine.startDialogueTree(new DialogueTree(text.toString()));
		}
		else if (FrameEngine.getSaveFile().getFlag("GOT_ALL_TREASURES")) {
			FrameEngine.startDialogueTree(new DialogueTree(this, "help_statue_noice"));
		}
		else {
			FrameEngine.startDialogueTree(new DialogueTree(this, "help_statue_normal"));
		}
	}
	
	private String addHint(String flag, String hint) {
		if (!FrameEngine.getSaveFile().getFlag(flag)) {
			return hint;
		}
		return "";
	}

	@Override
	public void dispose() {}

}
