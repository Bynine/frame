package text;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import entity.NPC;
import main.AudioHandler;
import main.Timer;

public class Textbox {

	private Timer text_timer = new Timer(0);
	private int text_pos = 0;
	private final ArrayList<Object> characters = new ArrayList<>();
	private Sound text_sound;
	private final NPC speaker;
	/**
	 * Whether to animate player talking.
	 */
	private boolean talking;
	/**
	 * How many frames it takes to draw a new character.
	 */
	private static float TEXT_SPEED = 1.5f;
	private static float TALK_SPEED = 8.0f;

	public Textbox(String text){
		parseText(text);
		text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/blip.wav"));
		this.speaker = null;
	}

	public Textbox(String text, NPC speaker){
		parseText(text);
		text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/" + speaker.getVoiceUrl() + ".wav"));
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

	/**
	 * Change many characters are drawn to screen and play voice clip.
	 */
	public void update(){
		text_timer.countUp();
		if (!isFinished() && text_timer.getCounter()/TEXT_SPEED > text_pos){ 
			if (characters.get(text_pos) instanceof Command){
				handleCommand(((Command)characters.get(text_pos)));
			}
			text_pos++;
			if (text_pos % TALK_SPEED == 0){
				AudioHandler.playSound(text_sound);
			}
		}
	}
	
	private void handleCommand(Command command){
		switch(command.getID()){
		case "SLOW":{
			TEXT_SPEED = 4.0f;
		} break;
		case "TALK":{
			talking = true;
		} break;
		default:{
			command.activate();
		} break;
		}
	}

	/**
	 * The entire string will now be drawn.
	 */
	public void complete(){
		text_pos = characters.size();
		AudioHandler.playSound(text_sound);
	}

	/**
	 * Whether this textbox is displaying all of its text.
	 */
	public boolean isFinished(){
		return text_pos == characters.size();
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
		String text = "";
		if (null != speaker) text = speaker.getName() + ": " + text;
		for (int ii = 0; ii < text_pos; ++ii){
			Object obj = characters.get(ii);
			if (obj instanceof Character){
				text = text + ((char)obj);
			}
		}
		return text;
	}

	public boolean playerTalking() {
		return talking;
	}

}
