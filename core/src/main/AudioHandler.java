package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import overworld.AudioSource;

/**
 * Handles all audio (Music and Sound).
 */
public class AudioHandler {
	
	/**
	 * A certain period after a newly created sound is played, it gets disposed.
	 */
	private static HashMap<TimerDuration, Sound> sound_disposal = new HashMap<>();
	private static ArrayList<AudioSource> audioSources = new ArrayList<>();
	private static Music curr_audio = null;
	public static float VOLUME = 1.0f;
	
	/**
	 * Any start-up calls.
	 */
	public static void initialize(){
		if (FrameEngine.mute) {
			VOLUME = 0.0f;
		}
	}
	
	/**
	 * EntityHandler gives all audio sources.
	 */
	public static void addAudioSources(ArrayList<AudioSource> sources){
		audioSources.addAll(sources);
	}
	
	/**
	 * Clean up audio sources from previous area.
	 */
	public static void clearAudioSources(){
		audioSources.clear();
	}
	
	/**
	 * Counts up each sound in sound_disposal.
	 */
	public static void update(){
		Iterator<TimerDuration> sound_iter = sound_disposal.keySet().iterator();
		while (sound_iter.hasNext()){
			TimerDuration timer = sound_iter.next();
			timer.countUp();
			if (timer.timeUp()){
				sound_disposal.get(timer).dispose();
				sound_iter.remove();
			}
		}
		for (AudioSource audioSource: audioSources){
			audioSource.getAudio().setVolume(VOLUME * audioSource.getVolume());
			audioSource.getAudio().play();
		}
	}

	/**
	 * Stops the current Music, then plays the Music specified by the path.
	 */
	public static void start_new_audio(String path){
		if (null != curr_audio){
			curr_audio.stop();
		}
		curr_audio = Gdx.audio.newMusic(Gdx.files.internal(path));
		curr_audio.setVolume(VOLUME);
		curr_audio.setLooping(true);
		curr_audio.play();
	}
	
	/**
	 * Plays the sound effect specified by the path and puts it in sound_disposal for later removal.
	 */
	public static void play_temporary_sfx(String path){
		Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
		final int time_before_dispose_sound = 1000;
		sound_disposal.put(new TimerDuration(time_before_dispose_sound), sound);
		sound.play(VOLUME);
	}
	
	/**
	 * Plays a preloaded sound. The calling class needs to dispose the sound of its own accord.
	 */
	public static void play_sfx(Sound sound){
		final float pitchDisparity = 10.0f;
		float pitch = (1.0f - 2.0f/pitchDisparity) + (float) (Math.random()/pitchDisparity);
		sound.play(VOLUME, pitch, 0);
	}
	
}