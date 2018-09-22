package text;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import entity.InteractableEntity;
import entity.NPC;
import main.AudioHandler;
import main.FrameEngine;
import timer.Timer;

public class Textbox {

	private Timer text_timer = new Timer(0);
	private int textPos = 0;
	private final ArrayList<Object> characters = new ArrayList<>();
	private Sound text_sound;
	private InteractableEntity speaker;
	/**
	 * Whether to animate player talking.
	 */
	private boolean talking;
	/**
	 * How many frames it takes to draw a new character.
	 */
	public static final float DEFAULT_TEXT_SPEED = 1.5F;
	private float TEXT_SPEED = DEFAULT_TEXT_SPEED;
	//private float TALK_SPEED = 6.0f;

	public Textbox(String text){
		parseText(text);
		text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/blip.wav"));
		this.speaker = null;
	}

	public Textbox(String text, InteractableEntity speaker){
		parseText(text);
		if (speaker instanceof NPC){
			text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/" + speaker.getVoiceUrl() + ".wav"));
		}
		else{
			text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/blip.wav"));
		}
		this.speaker = speaker;
	}

	/**
	 * Takes the input string and parses it into a series of chars and commands.
	 */
	private void parseText(String text){
		boolean addCommand = false;
		String commandID = "";
		for (char c: text.toCharArray()){
			if (addCommand){
				if (c == ']'){ // end of the command
					Object command = parseCommand(commandID);
					if (command instanceof String){
						for (char c2: ((String)(command)).toCharArray()){
							characters.add(c2);
						}
					}
					else if (command instanceof Command){
						characters.add((Command)command);
					}
					commandID = "";
					addCommand = false;
				}
				else{
					commandID += c;
				}
			}
			else if (c == '['){ // begin the command
				addCommand = true;
			}
			else if (!addCommand){
				characters.add(c);
			}
		}
	}

	/**
	 * Returns a String if the command evaluates to a string, and a new Command otherwise.
	 */
	private Object parseCommand(String commandID){
		return new Command(commandID);
	}
	
	private int talkTime = 0;
	private final int talkSpeed = 6;

	/**
	 * Change many characters are drawn to screen and play voice clip.
	 */
	public void update(){
		text_timer.countUp();
		if (!isFinished() && text_timer.getCounter()/TEXT_SPEED > textPos){ 
			Object currChar = characters.get(textPos);
			if (currChar instanceof Command){
				handleCommand(((Command)characters.get(textPos)));
			}
			else{
				Character c = (Character)currChar;
				if (!Character.isWhitespace(c)){
					talkTime ++;
				}
				if (talkTime % talkSpeed == (talkSpeed-1)){
					AudioHandler.playSound(text_sound);
					talkTime = 0;
				}
			}
			textPos++;
		}
	}

	private void handleCommand(Command command){
		if (null != speaker){
			speaker.getMessage(command.getID());
		}
		if (command.getID().startsWith("SPEED")){
			String[] speedData = command.getID().split("=");
			TEXT_SPEED = Float.parseFloat(speedData[1]);
		}
		else if (command.getID().startsWith("GIVE_")){
			String[] itemData = command.getID().split("_");
			FrameEngine.getInventory().addItem(itemData[1]);
		}
		else if (command.getID().equals("NOSPEAKER")){
			speaker = null;
			text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/blip.wav"));
		}
		else if (command.getID().equals("REMOVE_ITEM")){
			FrameEngine.getInventory().removeItem(FrameEngine.getGivenItemID());
		}
		else{
			switch(command.getID()){
			case "NSPEED":{
				TEXT_SPEED = DEFAULT_TEXT_SPEED;
			} break;
			case "TALK":{
				talking = true;
			} break;
			default:{
				command.activate();
			} break;
			}
		}
	}

	/**
	 * The entire string will now be drawn.
	 */
//	public void complete(){
////		textPos = characters.size();
////		AudioHandler.playSound(text_sound);
//	}

	/**
	 * Whether this textbox is displaying all of its text.
	 */
	public boolean isFinished(){
		return textPos == characters.size();
	}

	/**
	 * Eliminates this textbox.
	 */
	public void dispose(){
		text_sound.dispose();
	}

	/**
	 * Returns what text will be displayed.
	 */
	public String getDisplayedText(){
		return getText(textPos);
	}

	public boolean playerTalking() {
		return talking;
	}

	public String getAllText() {
		return getText(characters.size());
	}

	private String getText(int length){
		StringBuilder text = new StringBuilder();
		if (null != speaker && speaker instanceof NPC) {
			boolean name = true;
			Object firstChar = characters.get(0);
			if (firstChar instanceof Command){
				Command firstCommand = (Command)firstChar;
				name = (!firstCommand.getID().equals("NOSPEAKER"));
			}
			if (name) text.append(((NPC)speaker).getName() + ": ");
		}
		for (int ii = 0; ii < length; ++ii){
			Object obj = characters.get(ii);
			if (obj instanceof Character){
				text.append((char)obj);
			}
		}
		return text.toString();
	}

}
