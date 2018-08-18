package area;

import java.util.ArrayList;
import java.util.logging.Level;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;

import entity.AudioLocation;
import entity.Bird;
import entity.Description;
import entity.DialogueDescription;
import entity.Door;
import entity.Emitter;
import entity.Entity;
import entity.Finish;
import entity.Glimmer;
import entity.Secret;
import entity.Item;
import entity.NPC;
import entity.Portal;
import entity.ShrineDoor;
import entity.Statue;
import main.FrameEngine;

/**
 * Loads entities from given map.
 */
public class EntityLoader {

	/**
	 * Looks at the map data to generate entities for EntityHandler to use.
	 */
	public ArrayList<Entity> createEntities(TiledMap map){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		MapObjects map_entities = map.getLayers().get("ENTITIES").getObjects();
		for (int ii = 0; ii < map_entities.getCount(); ++ii){
			MapObject entity = map_entities.get(ii);
			MapProperties properties = entity.getProperties();
			int x = Math.round(properties.get("x", Float.class));
			int y = Math.round(properties.get("y", Float.class));
			int width = Math.round(properties.get("width", Float.class));
			int height = Math.round(properties.get("height", Float.class));
			String type = (String)properties.get("TYPE");
			switch(type.toLowerCase()){
			case "portal": {
				String[] destination = properties.get("DEST", String.class).split(",");
				double x_dest = Double.parseDouble(destination[1]);
				double y_dest = Double.parseDouble(destination[2]);
				entities.add(new Portal(x, y, width, height, destination[0], x_dest, y_dest));
			} break;
			case "door": {
				String[] destination = properties.get("DEST", String.class).split(",");
				double x_dest = Double.parseDouble(destination[1]);
				double y_dest = Double.parseDouble(destination[2]);
				entities.add(new Door(x, y, destination[0], x_dest, y_dest));
			} break;
			case "npc": {
				String dialoguePath = properties.get("DIALOGUE", String.class);
				String id = properties.get("ID", String.class);
				int interactXDisp = 0;
				int interactYDisp = 0;
				if (properties.containsKey("INTERACTXDISP")) {
					interactXDisp = Integer.parseInt(properties.get("INTERACTXDISP", String.class));
				}
				if (properties.containsKey("INTERACTYDISP")) {
					interactYDisp = Integer.parseInt(properties.get("INTERACTYDISP", String.class));
				}
				String imagePath = properties.get("IMAGE", String.class);
				
				entities.add(new NPC(
						x, y, 
						interactXDisp, interactYDisp,
						width, height,
						id, imagePath, dialoguePath));
			} break;
			case "desc": {
				String text = properties.get("TEXT", String.class);
				entities.add(new Description(x, y, width, height, text));
			} break;
			case "dialoguedesc": {
				String dialogue = properties.get("DIALOGUE", String.class);
				entities.add(new DialogueDescription(x, y, width, height, dialogue));
			} break;
			case "item": {
				String id = properties.get("ID", String.class);
				entities.add(new Item(x, y, id));
			} break;
			case "secret": {
				String id = properties.get("ID", String.class);
				entities.add(new Secret(x, y, id));
			} break;
			case "audio": {
				String audio = properties.get("AUDIO", String.class);
				String id = properties.get("ID", String.class);
				entities.add(new AudioLocation(x, y, audio, id));
			} break;
			case "emitter":{
				String graphic = properties.get("GRAPHIC", String.class);
				int interval = Integer.parseInt(properties.get("INTERVAL", String.class));
				int duration = Integer.parseInt(properties.get("DURATION", String.class));
				entities.add(new Emitter(x, y, interval, duration, graphic));
			} break;
			case "bird":{
				entities.add(new Bird(x, y));
			} break;
			case "statue":{
				String cc = properties.get("CORRECTCOLOR", String.class);
				entities.add(new Statue(x, y, cc));
			} break;
			case "shrine_door":{
				entities.add(new ShrineDoor(x, y));
			} break;
			case "glimmer":{
				String flag = properties.get("FLAG", String.class);
				entities.add(new Glimmer(x, y, flag));
			} break;
			case "finish":{
				entities.add(new Finish(x, y, width, height));
			} break;
			default: {
				FrameEngine.logger.log( Level.WARNING, 
						"Couldn't parse type " + type + " of " + entity.getName() + "");
			} break;
			}
		}
		return entities;
	}
	
}
