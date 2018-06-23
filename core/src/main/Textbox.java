package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Textbox {

	private Timer text_timer = new Timer(0);
	private int text_pos = 0;
	private final String text;
	private final int text_end;
	private final Sound text_sound;	// TODO: Set sound by NPC data.
	/**
	 * How many frames it takes to draw a new character.
	 */
	private static final float TEXT_SPEED = 2;
	private static final float TALK_SPEED = 5;

	Textbox(String text){
		this.text = text;
		text_end = text.length();
		text_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/blip.wav"));
	}

	/**
	 * Change many characters are drawn to screen and play voice clip.
	 */
	void update(){
		text_timer.countUp();
		if (text_pos < text_end && text_timer.getCounter()/TEXT_SPEED > text_pos){ 
			text_pos++;
			if (text_pos % TALK_SPEED == 0){
				AudioHandler.play_sfx(text_sound);
			}
		}
	}
	
	/**
	 * The entire string will now be drawn.
	 */
	void complete(){
		text_pos = text_end;
	}
	
	/**
	 * Whether this textbox is displaying all of its text.
	 */
	boolean isFinished(){
		return text_pos == text_end;
	}
	
	/**
	 * Eliminates this textbox.
	 */
	void dispose(){
		text_sound.dispose();
	}

	/**
	 * Returns what text will be displayed.
	 */
	public String getDisplayedText(){
		return text.substring(0, text_pos);
	}

}
