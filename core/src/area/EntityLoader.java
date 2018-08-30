package area;

import java.util.ArrayList;
import java.util.logging.Level;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;

import entity.AudioLocation;
import entity.Bird;
import entity.Currency;
import entity.Description;
import entity.DialogueDescription;
import entity.DialogueTrigger;
import entity.Door;
import entity.Emitter;
import entity.Entity;
import entity.Finish;
import entity.Freb;
import entity.FrebKing;
import entity.Glimmer;
import entity.Secret;
import entity.Item;
import entity.ItemHole;
import entity.NPC;
import entity.Portal;
import entity.PortalHole;
import entity.ShrineDoor;
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
			case "portalhole": {
				String[] destination = properties.get("DEST", String.class).split(",");
				String flag = properties.get("FLAG", String.class);
				double x_dest = Double.parseDouble(destination[1]);
				double y_dest = Double.parseDouble(destination[2]);
				entities.add(new PortalHole(x, y, flag, destination[0], x_dest, y_dest));
			} break;
			case "door": {
				String[] destination = properties.get("DEST", String.class).split(",");
				double x_dest = Double.parseDouble(destination[1]);
				double y_dest = Double.parseDouble(destination[2]);
				entities.add(new Door(x, y, destination[0], x_dest, y_dest));
			} break;
			case "npc": {
				String flag = properties.containsKey("FLAG") ? properties.get("FLAG", String.class) : "";
				if (flag != "" && !FrameEngine.getSaveFile().getFlag(flag)){
					break;
				}
				String id = properties.get("ID", String.class);
				if (id.equals("FREBKING")){
					entities.add(new FrebKing(x, y, width, height));
				}
				else{
					String dialoguePath = properties.get("DIALOGUE", String.class);
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
				}
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
				String flag = properties.get("FLAG", String.class);
				if (checkFlag(entity, flag)) break;
				String id = properties.get("ID", String.class);
				entities.add(new Item(x, y, id, flag));
			} break;
			case "itemhole": {
				String flag = properties.get("FLAG", String.class);
				if (checkFlag(entity, flag)) break;
				String id = properties.get("ID", String.class);
				entities.add(new ItemHole(x, y, id, flag));
			} break;
			case "secret": {
				String flag = properties.get("FLAG", String.class);
				if (checkFlag(entity, flag)) break;
				int amount = Integer.parseInt(properties.get("AMOUNT", String.class));
				entities.add(new Secret(x, y, amount, flag));
			} break;
			case "currency":{
				String flag = properties.get("FLAG", String.class);
				if (checkFlag(entity, flag)) break;
				int amount = Integer.parseInt(properties.get("AMOUNT", String.class));
				entities.add(new Currency(x, y, amount, flag));
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
			case "freb":{
				String flag = properties.get("FLAG", String.class);
				entities.add(new Freb(x, y, width, height, flag));
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
			case "dialogue_trigger":{
				String flag = properties.get("FLAG", String.class);
				if (checkFlag(entity, flag)) break;
				String dialoguePath = properties.get("DIALOGUE", String.class);
				entities.add(new DialogueTrigger(x, y, width, height, dialoguePath));
			} break;
			default: {
				FrameEngine.logger.log( Level.WARNING, 
						"Couldn't parse type " + type + " of " + entity.getName() + "");
			} break;
			}
		}
		return entities;
	}
	
	private boolean checkFlag(MapObject entity, String flag){
		if (null == flag){
			FrameEngine.logger.log( Level.WARNING, 
					"Couldn't find flag of " + entity.getName() + "");
		}
		return (FrameEngine.getSaveFile().getFlag(flag));
	}

}
