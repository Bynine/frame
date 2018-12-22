package main;

import entity.NPC;
import text.DialogueTree;

/**
 * Checks flags for important story events.
 * It's kludgey, but hey, it works...
 * @author Tyler
 *
 */
public class ProgressionHandler {

	public static final String 
	ghostShopkeeper = "GHOST_SHOPKEEPER",
	ghostLibrarian = "GHOST_LIBRARIAN",
	ghostLeslie = "GHOST_LESLIE",
	ghostThing = "GHOST_THING",
	ghostEvent = "GHOST_EVENT",
	beforeShrineWarning = "BEFORE_SHRINE_WARNING",
	shellFreb = "SHELL_FREB",
	foundStatuette = "FOUND_STATUETTE",
	checkStatuette = "CHECK_STATUETTE";

	ProgressionHandler(){
	}

	void update(){
		checkOutOfWoodsBeforeEnteredShrine();
		checkGotAllTreasures();
	}

	/**
	 * Provides dialog for checking statuette based on
	 * number of statuettes found.
	 */
	public void startStatuetteDialog() {
		int counter = FrameEngine.getSaveFile().getCounter(foundStatuette);
		if (FrameEngine.LOG) System.out.println("Starting statuette dialog: " + counter);
		switch (counter){
		case 1:{
			FrameEngine.startDialogueTree(new DialogueTree(
					"[SPEAKER=KAMI]Three of us... remain..."));
		} break;
		case 2:{
			FrameEngine.startDialogueTree(new DialogueTree(
					"[SPEAKER=KAMI]Two of us... remain..."));
		} break;
		case 3:{
			FrameEngine.startDialogueTree(new DialogueTree(
					"[SPEAKER=KAMI]One of us... remains..."));
		} break;
		case 4:{
			FrameEngine.startDialogueTree(new DialogueTree(
					"[SPEAKER=KAMI]I can see your resolve... The Shrine has opened. Come North, traveller."));
		} break;
		}
	}

	private void checkOutOfWoodsBeforeEnteredShrine(){
		if (
				(FrameEngine.getArea().getID().equals("BEACH") || 
						FrameEngine.getArea().getID().equals("ORCHARD")) &&
				!FrameEngine.getSaveFile().getFlag("ENTERED_SHRINE") &&
				!FrameEngine.getSaveFile().getFlag(beforeShrineWarning)
				){
			FrameEngine.getSaveFile().setFlag(beforeShrineWarning, true);
			FrameEngine.startDialogueTree(
					new DialogueTree(
							new NPC("KAMI", "before_shrine_warning"),
							"before_shrine_warning"
							));
		}
	}

	private void checkGotAllTreasures(){
		final String gotAllTreasures = "GOT_ALL_TREASURES";
		if (!FrameEngine.getSaveFile().getFlag(gotAllTreasures)){
			if (
					FrameEngine.getSaveFile().getFlag("FREBKING_REWARD") &&
					FrameEngine.getSaveFile().getFlag("GRUB_REWARD") &&
					FrameEngine.getSaveFile().getFlag("CAFE_REWARD") &&
					FrameEngine.getSaveFile().getFlag("WORLD_REWARD") &&
					FrameEngine.getSaveFile().getFlag("GHOST_REWARD") &&
					FrameEngine.getSaveFile().getFlag("CURSE_REWARD")
					){
				FrameEngine.getSaveFile().setFlag(gotAllTreasures, true);
				String dialogue = FrameEngine.getSaveFile().getFlag("FOUND_GOAL") ?
						"got_all_treasures_goal" : "got_all_treasures";
				FrameEngine.startDialogueTree(
						new DialogueTree(
								new NPC("KAMI", dialogue),
								dialogue
								));
			}
		}
	}


}
