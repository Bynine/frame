package main;

import java.util.ArrayList;

import area.Area;
import area.EntityLoader;
import entity.AudioLocation;
import entity.Currency;
import entity.Entity;
import entity.ImmobileEntity;
import entity.Item;
import entity.NPC;
import entity.Secret;

/**
 * Controls and updates overworld entities.
 */
public class EntityHandler {

	protected static final ArrayList<Entity> entities = new ArrayList<Entity>();
	protected static final ArrayList<Entity> entitiesToAdd = new ArrayList<Entity>();
	private static int numSecrets = 0;
	
	public static void update() {
		ArrayList<Entity> entities_to_remove = new ArrayList<Entity>();
		for (Entity en: entitiesToAdd){
			entities.add(en);
		}
		entitiesToAdd.clear();
		for (Entity en: entities){
			if (en.shouldDelete()){
				entities_to_remove.add(en);
			}
			else{
				en.update();
			}
		}
		for (Entity en: entities_to_remove){
			FrameEngine.getArea().removeFromCollision(en);
			entities.remove(en);
		}
	}
	
	public static void addEntity(Entity en){
		entitiesToAdd.add(en);
	}
	
	/**
	 * Only updates entity images, for dialogue and such.
	 */
	public static void updateImages() {
		for (Entity en: entities){
			en.updateImage();
		}
	}
	
	public static void initializeAreaEntities(Area area){
		entities.clear();
		numSecrets = 0;
		if (!entities.contains(FrameEngine.getPlayer())){
			entities.add(FrameEngine.getPlayer());
		}
		entities.addAll(new EntityLoader().createEntities(area.getMap()));
		ArrayList<AudioLocation> audioSources = new ArrayList<>();
		for (Entity en: entities){
			if (en instanceof AudioLocation){
				audioSources.add((AudioLocation)en);
			}
			if (en instanceof ImmobileEntity && en.collides()){
				en.update();
				area.addToCollision(en);
			}
			if (en instanceof Item || en instanceof Secret || en instanceof Currency){
				numSecrets++;
			}
		}
		AudioHandler.addAudioSources(audioSources);
	}
	
	public static void refreshEntityCollisions() {
		for (Entity en: entities){
			if (en instanceof ImmobileEntity && en.collides()){
				en.update();
				FrameEngine.getArea().addToCollision(en);
			}
		}
	}

	public static void dispose(){
		for (Entity en: entities){
			if (!en.equals(FrameEngine.getPlayer())) {
				en.setRemove();
				en.dispose();
			}
		}
	}
	
	public static ArrayList<Entity> getEntities(){
		return entities;
	}

	public static NPC getNPC(String string) {
		for (Entity entity: entities){
			if (entity instanceof NPC){
				NPC npc = (NPC) entity;
				if (npc.getID().equals(string)){
					return npc;
				}
			}
		}
		return null;
	}

	public static int getNumSecrets() {
		return numSecrets;
	}
}
