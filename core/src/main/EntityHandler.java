package main;

import java.util.ArrayList;

import overworld.Entity;
import overworld.Area;

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
			entities.remove(en);
		}
	}
	
	public static void init_area_entities(Area area){
		entities.add(FrameEngine.getPlayer());
		entities.addAll(area.getEntities());
	}

	public static void dispose(){
		for (Entity en: entities){
			if (!en.equals(FrameEngine.getPlayer())) en.dispose();
		}
	}
	
	public static ArrayList<Entity> getEntities(){
		return entities;
	}
}
