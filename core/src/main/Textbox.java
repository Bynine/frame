package main;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import overworld.InteractableEntity;

public class Textbox {

	private Timer text_timer = new Timer(0);
	private int text_pos = 0;
	private final ArrayList<Object> characters = new ArrayList<>();
	private Sound text_sound;
	/**
	 * How many frames it takes to draw a new character.
	 */
	private static final float TEXT_SPEED = 1.5f;
	private static final float TALK_SPEED = 8.0f;

	public Textbox(String text){
		parseText(text);
		text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/blip.wav"));
	}

	public Textbox(InteractableEntity speaker){
		this(speaker.getText());
		text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/" + speaker.getVoiceUrl() + ".wav"));
	}

	/**
	 * Takes the input string and parses it into a series of chars and commands.
	 */
	private void parseText(String text){
		boolean addCommand = false;
		String commandID = "";
		for (char c: text.toCharArray()){
			if (addCommand){
				if (c == '>'){ // end of the command
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
			else if (c == '<'){ // begin the command
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
		if (commandID.equals("NAME")){
			return "the " + FrameEngine.getCurrentEncounter().getOpponentName();
		}
		return new Command(commandID);
	}

	/**
	 * Change many characters are drawn to screen and play voice clip.
	 */
	public void update(){
		text_timer.countUp();
		if (!isFinished() && text_timer.getCounter()/TEXT_SPEED > text_pos){ 
			if (characters.get(text_pos) instanceof Command){
				FrameEngine.getCurrentEncounter().putCommand((Command)characters.get(text_pos));
			}
			text_pos++;
			if (text_pos % TALK_SPEED == 0){
				AudioHandler.playSound(text_sound);
			}
		}
	}

	/**
	 * The entire string will now be drawn.
	 * TODO: Remove if no longer used
	 */
	void complete(){
		//text_pos = text_end;
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
		for (int ii = 0; ii < text_pos; ++ii){
			Object obj = characters.get(ii);
			if (obj instanceof Character){
				text = text + ((char)obj);
			}
		}
		return text;
	}

}
