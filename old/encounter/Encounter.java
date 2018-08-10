package encounter;

import java.util.ArrayDeque;

import main.Button;
import main.Command;
import main.FrameEngine;
import main.Textbox;

/**
 * One on one encounter with some Being.
 */
public class Encounter {

	private final ArrayDeque<Textbox> textboxes = new ArrayDeque<>();
	private final ArrayDeque<Command> commands = new ArrayDeque<>();
	private final Being opponent;
	private boolean ended = false;
	private View view = new ViewNone();

	public Encounter(String ID){
		opponent = new Being(ID);
		putTextbox("It's a " + opponent.getName() + "!");
		changeView(new ViewOptions());
	}

	/**
	 * Core loop.
	 */
	public void update() {
		if (!textboxes.isEmpty()){
			textboxLoop();
		}
		else if (!commands.isEmpty()){
			while (!commands.isEmpty()){
				handleCommand(commands.pop());
			}
		}
		else if (ended){
			FrameEngine.endEncounter();
		}
		else{
			buttonLoop();
			// when player chooses action...
			//	evaluate their action
			//	check for end state
			//	evaluate enemy's action
			//	check for end state
			//
		}
	}

	/**
	 * While buttons are active, checks for activity on them.
	 */
	private void buttonLoop(){
		for (Button button: view.getButtons()){
			if (button.clicked()){
				Object output = button.getOutput();
				if (output instanceof Choice){
					handleChoice((Choice)output);
				}
				else if (output instanceof Action){
					handlePlayerAction((Action)output);
				}
				else{
					FrameEngine.logger.warning("Encounter can't handle button output: " + output.toString());
				}
			}
		}
	}

	/**
	 * Assesses the chosen button's output.
	 */
	private void handleChoice(Choice choice){
		switch(choice){
		case SKILL:{
			changeView(new ViewSkills());
		} break;
		case ITEM:{
			changeView(new ViewItems());
		} break;
		case END:{
			leaveEncounter();
		} break;
		}
	}
	
	/**
	 * Apply the effects of the command.
	 */
	private void handleCommand(Command command){
		switch(command.getID()){
		case "C_END":{
			endEncounter();
		} break;
		case "C_THANKS":{
			putTextbox("You received thanks!");
		} break;
		default:{
			System.out.println("Couldn't handle command: " + command.toString());
		} break;
		}
	}
	
	/**
	 * Opponent chooses reaction based on action
	 */
	private void handlePlayerAction(Action action){
		changeView(new ViewOptions());
		action.perform();
		opponent.chooseReaction(action).perform();
	}
	
	private void changeView(View view){
		this.view = view;
	}

	private void leaveEncounter(){
		putTextbox("You decided to leave.");
		endEncounter();
	}
	
	private void endEncounter(){
		changeView(new ViewNone());
		ended = true;
	}

	/**
	 * Handles inputs while a message is on screen.
	 */
	private void textboxLoop(){
		if (
				FrameEngine.getInputHandler().getPointer().thisFrame
				&& textboxes.peek().isFinished()
				){
			Textbox textbox = textboxes.remove();
			textbox.dispose();
		}
		if (null != getTextbox()){
			getTextbox().update();
		}
	}

	/**
	 * Gets the active message on top of the messages ArrayDeque.
	 */
	public Textbox getTextbox(){
		if (textboxes.isEmpty()) {
			return null;
		}
		return textboxes.peek();
	}

	/**
	 * Add a textbox to the front of the textboxes deque.
	 */
	public void putTextbox(String text){
		Textbox message = new Textbox(text);
		textboxes.add(message);
	}
	
	public void putCommand(Command command){
		commands.add(command);
	}

	public View getView(){
		return view;
	}

	public String getOpponentName() {
		return opponent.getName();
	}

	public enum Choice{
		SKILL, ITEM, END
	}

}
