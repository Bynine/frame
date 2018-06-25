package main;

import java.util.ArrayList;

import overworld.Entity;
import overworld.EntityLoader;
import overworld.InteractableEntity;
import overworld.Area;
import overworld.AudioSource;

/**
 * Controls and updates overworld entities.
 */
public class EntityHandler {

	protected static final ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public static void update() {
		ArrayList<Entity> entities_to_remove = new ArrayList<Entity>();
		for (Entity en: entities){
			if (en.should_delete()){
				entities_to_remove.add(en);
			}
			else{
				en.update();
			}
		}
		for (Entity en: entities_to_remove){
			FrameEngine.getCurrentArea().removeFromCollision(en);
			entities.remove(en);
		}
	}
	
	public static void init_area_entities(Area area){
		if (!entities.contains(FrameEngine.getPlayer())){
			entities.add(FrameEngine.getPlayer());
		}
		entities.addAll(new EntityLoader().create_entities(area.getMap()));
		ArrayList<AudioSource> audioSources = new ArrayList<>();
		for (Entity en: entities){
			if (en instanceof AudioSource){
				audioSources.add((AudioSource)en);
			}
			if (en instanceof InteractableEntity){
				en.update();
				area.addToCollision(en);
			}
		}
		AudioHandler.addAudioSources(audioSources);
	}

	public static void dispose(){
		for (Entity en: entities){
			if (!en.equals(FrameEngine.getPlayer())) {
				en.setDelete();
				en.dispose();
			}
		}
	}
	
	public static ArrayList<Entity> getEntities(){
		return entities;
	}
}
