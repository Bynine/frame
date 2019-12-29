package entity;

import main.FrameEngine;
import text.DialogueTree;

public class QuestHelper extends DialogueTrigger {

	public QuestHelper(float x, float y, float width, float height) {
		super(x, y, width, height, "quest_helper");
		dialogueTree = new DialogueTree(new NPC("KAMI", "quest_helper"), "quest_helper");
	}
	
	@Override
	protected boolean shouldActivate() {
		return touchingPlayer(hitbox) && (FrameEngine.getPlayer().dir == SIDE && !FrameEngine.getPlayer().isFlipped());
	}

}
