package main;

import text.DialogueTree;

/**
 * Checks flags for important story events.
 * It's kludgey, but hey, it works...
 * @author Tyler
 *
 */
public class ProgressionHandler {
	
	public static final String 
	shellFreb = "SHELL_FREB",
	foundStatuette = "FOUND_STATUETTE",
	checkStatuette = "CHECK_STATUETTE";
	
	SaveFile sf;
	
	ProgressionHandler(){
		sf = FrameEngine.getSaveFile();
	}

	void update(){
		statuetteFoundHelper(1);
		statuetteFoundHelper(2);
		statuetteFoundHelper(3);
		checkStatuetteHelper();
	}
	
	/**
	 * If statuette is discovered for first time,
	 * increments number of statuettes found.
	 */
	private void statuetteFoundHelper(int n){
		if (sf.getCounter(foundStatuette) == n && !sf.getFlag(foundStatuette+"_"+n)){
			checkStatuetteDialog();
			sf.setFlag(foundStatuette+"_"+n, true);
		}
	}
	
	/**
	 * Displays check statuette dialog once.
	 */
	private void checkStatuetteHelper(){
		if (sf.getFlag(checkStatuette)){
			checkStatuetteDialog();
			sf.setFlag(checkStatuette, false);
		}
	}

	/**
	 * Provides dialog for checking statuette based on
	 * number of statuettes found.
	 */
	private void checkStatuetteDialog() {
		switch (sf.getCounter(foundStatuette)){
		case 1:{
			FrameEngine.startDialogueTree(new DialogueTree(
					"A mysterious voice echoes...\n"
					+ "\"Two... remain...\""));
		} break;
		case 2:{
			FrameEngine.startDialogueTree(new DialogueTree(
					"A mysterious voice echoes...\n"
					+ "\"One... remains...\""));
		} break;
		case 3:{
			FrameEngine.startDialogueTree(new DialogueTree(
					"A mysterious voice echoes...\n"
					+ "\"I can see your resolve... The Shrine has opened. Come North, young traveller.\""));
		} break;
		}
	}
	
}
