package overworld;

import java.util.ArrayList;
import java.util.logging.Level;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;

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
			case "encounter": {
				String id = properties.get("ID", String.class);
				entities.add(new Chaser(x, y, id));
			} break;
			case "portal": {
				String[] destination = properties.get("DEST", String.class).split(",");
				double x_dest = Double.parseDouble(destination[1]);
				double y_dest = Double.parseDouble(destination[2]);
				entities.add(new Portal(x, y, width, height, destination[0], x_dest, y_dest));
			} break;
			case "npc": {
				String text = properties.get("TEXT", String.class);
				String id = properties.get("ID", String.class);
				String image = null;
				int interactYDisp = 0;
				if (properties.containsKey("INTERACTYDISP")) {
					interactYDisp = Integer.parseInt(properties.get("INTERACTYDISP", String.class));
				}
				if (properties.containsKey("IMAGE")) {
					image = properties.get("IMAGE", String.class);
				}
				
				entities.add(new NPC(
						x, y, 
						interactYDisp,
						width, height,
						id, image, text));
			} break;
			case "item": {
				String id = properties.get("ID", String.class);
				entities.add(new Item(x, y, id));
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
			default: {
				FrameEngine.logger.log( Level.WARNING, 
						"Couldn't parse type " + type + " of " + entity.getName() + "");
			} break;
			}
		}
		return entities;
	}
	
}
