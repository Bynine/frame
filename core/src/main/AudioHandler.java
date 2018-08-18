package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import entity.AudioLocation;

/**
 * Handles all audio (Music and Sound).
 */
public class AudioHandler {

	/**
	 * A certain period after a newly created sound is played, it gets disposed.
	 */
	private static HashMap<TimerDuration, Sound> soundDisposal = new HashMap<>();
	private static HashSet<AudioSource> audioSources = new HashSet<>();
	private static Music currAudio = null;
	private static String currAudioName = "";
	public static float VOLUME = 0.4f;

	/**
	 * Any start-up calls.
	 */
	public static void initialize(){
		if (FrameEngine.MUTE) {
			VOLUME = 0.0f;
		}
	}

	/**
	 * Counts up each sound in sound_disposal.
	 */
	public static void update(){
		Iterator<TimerDuration> sound_iter = soundDisposal.keySet().iterator();
		while (sound_iter.hasNext()){
			TimerDuration timer = sound_iter.next();
			timer.countUp();
			if (timer.timeUp()){
				soundDisposal.get(timer).dispose();
				sound_iter.remove();
			}
		}
		for (AudioSource audioSource: audioSources){
			audioSource.audio.setVolume(VOLUME * audioSource.getVolume());
			audioSource.audio.play();
		}
	}

	/**
	 * Stops the current Music, then plays the Music specified by the path.
	 */
	public static void startNewAudio(String audioName){
		if (!currAudioName.equals(audioName)){ // Only change music if it's different
			currAudioName = audioName;
			if (null != currAudio){
				currAudio.stop();
			}
			currAudio = Gdx.audio.newMusic(Gdx.files.internal(audioName));
			currAudio.setVolume(VOLUME);
			currAudio.setLooping(true);
			currAudio.play();
		}
	}

//	/**
//	 * Plays the sound effect specified by the path and puts it in sound_disposal for later removal.
//	 */
//	public static void playTemporarySound(String path){
//		Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
//		final int time_before_dispose_sound = 1000;
//		soundDisposal.put(new TimerDuration(time_before_dispose_sound), sound);
//		sound.play(VOLUME);
//	}

	/**
	 * Plays a preloaded sound. The calling class needs to dispose the sound of its own accord.
	 */
	public static void playSound(Sound sound){
		final float pitchDisparity = 20.0f;
		float pitch = (1.0f - 0.5f/pitchDisparity) + (float) (Math.random()/pitchDisparity);
		sound.play(VOLUME, pitch, 0);
	}
	
	/**
	 * Takes AudioLocations from EntityHandler and puts them into AudioSources.
	 */
	public static void addAudioSources(ArrayList<AudioLocation> locations){
		for (AudioLocation location: locations){
			boolean added = false;
			for(AudioSource source: audioSources){
				if (source.id.equals(location.id)){
					source.addLocation(location);
					added = true;
					break;
				}
			}
			if (!added) {
				AudioSource source = new AudioSource(location.audioFileName, location.id);
				source.addLocation(location);
				audioSources.add(source);
			}
		}
	}

	/**
	 * Clean up audio sources from previous area.
	 */
	public static void clearAudioSources(){
		for (AudioSource source: audioSources){
			source.dispose();
		}
		audioSources.clear();
	}

	/**
	 * A collection of AudioLocations. This class determines the closest one and uses that
	 * for the volume.
	 */
	private static class AudioSource{

		private final HashSet<AudioLocation> sourcelets = new HashSet<>();
		private final String id;
		private final Music audio;

		AudioSource(String audioFileName, String id){
			audio = Gdx.audio.newMusic(Gdx.files.internal("music/" + audioFileName + ".ogg"));
			this.id = id;
		}
		
		float getVolume(){
			float volume = 0.0f;
			for (AudioLocation sourcelet: sourcelets){
				float sourceletVolume = sourcelet.getVolume();
				if (sourceletVolume > volume) volume = sourceletVolume;
			}
			return volume;
		}

		void addLocation(AudioLocation sourcelet){
			sourcelets.add(sourcelet);
		}
		
		void dispose(){
			for (AudioLocation sourcelet: sourcelets){
				sourcelet.dispose();
			}
			sourcelets.clear();
			audio.stop();
			audio.dispose();
		}

	}

}