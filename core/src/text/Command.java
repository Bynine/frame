package text;

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

	public void activate() {
		switch(ID){
		case ProgressionHandler.foundStatuette:{
			FrameEngine.getSaveFile().addToCounter(1, ProgressionHandler.foundStatuette);
		} break;
		case ProgressionHandler.checkStatuette:{
			FrameEngine.getSaveFile().setFlag(ProgressionHandler.checkStatuette, true);
		} break;
		default: {
			FrameEngine.logger.warning("Can't activate command with ID " + ID);
		} break;
		}
	}
}
