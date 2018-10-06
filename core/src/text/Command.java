package text;

import entity.Ghost;
import entity.NPC;
import main.EntityHandler;
import main.FrameEngine;
import main.ProgressionHandler;

public class Command {
	
	private final String ID;

	public Command(String ID){
		this.ID = ID;
	}
	
	public String getID(){
		return ID;
	}

	/**
	 * Activates based on progression-based commands.
	 */
	public void progressionActivate() {
		switch(ID){
		case ProgressionHandler.foundStatuette:{
			FrameEngine.getSaveFile().addToCounter(1, ProgressionHandler.foundStatuette);
		} break;
		case ProgressionHandler.checkStatuette:{
			FrameEngine.getSaveFile().setFlag(ProgressionHandler.checkStatuette, true);
		} break;
		case ProgressionHandler.shellFreb:{
			FrameEngine.getSaveFile().addToCounter(1, ProgressionHandler.shellFreb);
		} break;
		case ProgressionHandler.ghostThing:{
			FrameEngine.startDialogueTree(new DialogueTree(new NPC("GHOST", "ghost_thing"), "ghost_thing"));
		} break;
		case ProgressionHandler.ghostEvent:{
			FrameEngine.getInventory().removeItem("GHOST");
			EntityHandler.addEntity(new Ghost());
		}
		default: {
			FrameEngine.logger.fine("Can't activate command with ID " + ID);
		} break;
		}
	}
}
