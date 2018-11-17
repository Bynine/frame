package area;

import java.util.ArrayList;
import java.util.logging.Level;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;

import entity.Answer;
import entity.Artist;
import entity.AudioLocation;
import entity.Bird;
import entity.CameraTrigger;
import entity.WildCat;
import entity.Currency;
import entity.Description;
import entity.DialogueDescription;
import entity.DialogueTrigger;
import entity.Door;
import entity.Emitter;
import entity.Entity;
import entity.Entity.Layer;
import entity.Finish;
import entity.Fire;
import entity.Flower;
import entity.Freb;
import entity.FrebKing;
import entity.Glimmer;
import entity.Goal;
import entity.Grass;
import entity.GrubDoor;
import entity.GrubMom;
import entity.Instrument;
import entity.Secret;
import entity.Shopkeeper;
import entity.Item;
import entity.ItemHole;
import entity.Koi;
import entity.Memorial;
import entity.MoveLight;
import entity.NPC;
import entity.Painting;
import entity.Portal;
import entity.Portal.Direction;
import entity.PortalHole;
import entity.Pumpkin;
import entity.PumpkinCarve;
import entity.QuestionTrigger;
import entity.SaleSign;
import entity.ShrineDoor;
import entity.Stand;
import entity.SummonObject;
import entity.TrapDoor;
import entity.Walkway;
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
			String appear = properties.containsKey("APPEAR") ? properties.get("APPEAR", String.class) : "";
			if (!(appear != "" && !FrameEngine.getSaveFile().getFlag(appear))){
				loadEntity(properties, entity, entities);
			}
		}
		return entities;
	}

	private void loadEntity(MapProperties properties, MapObject entity, ArrayList<Entity> entities){
		int x = Math.round(properties.get("x", Float.class));
		int y = Math.round(properties.get("y", Float.class));
		int width = Math.round(properties.get("width", Float.class));
		int height = Math.round(properties.get("height", Float.class));
		String type = ((String)properties.get("TYPE")).toLowerCase();
		switch(type){
		case "portal": {
			String[] destination = properties.get("DEST", String.class).split(",");
			Direction dir = Direction.ANY;
			if (properties.containsKey("DIR")){
				dir = Direction.valueOf(properties.get("DIR", String.class));
			}
			double x_dest = Double.parseDouble(destination[1]);
			double y_dest = Double.parseDouble(destination[2]);
			boolean isDoor = properties.containsKey("DOOR");
			entities.add(new Portal(x, y, width, height, destination[0], x_dest, y_dest, dir, isDoor));
		} break;
		case "grubhole": 
		case "trapdoor":
		case "portalhole": {
			String[] destination = properties.get("DEST", String.class).split(",");
			String flag = properties.containsKey("FLAG") ? properties.get("FLAG", String.class) : "";
			Direction dir = Direction.ANY;
			if (properties.containsKey("DIR")){
				dir = Direction.valueOf(properties.get("DIR", String.class));
			}
			double x_dest = Double.parseDouble(destination[1]);
			double y_dest = Double.parseDouble(destination[2]);
			if (type.equals("grubhole")){
				entities.add(new GrubDoor(x, y, flag, destination[0], x_dest, y_dest));
			}
			else if (type.equals("trapdoor")){
				entities.add(new TrapDoor(x, y, destination[0], x_dest, y_dest));
			}
			else{
				entities.add(new PortalHole(x, y, flag, destination[0], x_dest, y_dest, dir));
			}
		} break;
		case "door": {
			String[] destination = properties.get("DEST", String.class).split(",");
			double x_dest = Double.parseDouble(destination[1]);
			double y_dest = Double.parseDouble(destination[2]);
			entities.add(new Door(x, y, destination[0], x_dest, y_dest));
		} break;
		case "npc": {
			String id = properties.get("ID", String.class);
			String dialoguePath = properties.get("DIALOGUE", String.class);
			int interactXDisp = 0;
			int interactYDisp = 0;
			Layer layer = Layer.NORMAL;
			if (properties.containsKey("INTERACTXDISP")) {
				interactXDisp = Integer.parseInt(properties.get("INTERACTXDISP", String.class));
			}
			if (properties.containsKey("INTERACTYDISP")) {
				interactYDisp = Integer.parseInt(properties.get("INTERACTYDISP", String.class));
			}
			if (properties.containsKey("LAYER")){
				layer = Layer.valueOf(properties.get("LAYER", String.class));
			}
			String imagePath = properties.get("IMAGE", String.class);
			if (id.equals("FREBKING")){
				entities.add(new FrebKing(x, y, width, height));
			}
			else if (id.equals("SHOPKEEPER")){
				entities.add(new Shopkeeper(
						x, y, 
						interactXDisp, interactYDisp,
						width, height,
						id, imagePath, dialoguePath));
			}
			else if (id.equals("GRUBMOM")){
				entities.add(new GrubMom(
						x, y, 
						interactXDisp, interactYDisp,
						width, height,
						id, imagePath, dialoguePath, layer));
			}
			else if (id.equals("ARTIST")){
				entities.add(new Artist(
						x, y, 
						interactXDisp, interactYDisp,
						width, height
						));
			}
			else if (id.equals("PUMPKIN")){
				entities.add(new Pumpkin(
						x, y, 
						interactXDisp, interactYDisp,
						width, height,
						id, imagePath, dialoguePath, layer));
			}
			else {
				entities.add(new NPC(
						x, y, 
						interactXDisp, interactYDisp,
						width, height,
						id, imagePath, dialoguePath, layer));
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
		case "goal":{
			String flag = properties.get("FLAG", String.class);
			if (checkFlag(entity, flag)) break;
			entities.add(new Goal(x, y, flag));
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
			String dialoguePath = properties.get("DIALOGUE", String.class);
			entities.add(new DialogueTrigger(x, y, width, height, dialoguePath));
		} break;
		case "summonobject":{
			String image = properties.get("IMAGE", String.class);
			String flag = properties.get("FLAG", String.class);
			entities.add(new SummonObject(x, y, image, flag));
		} break;
		case "stand":{
			String id = properties.get("ID", String.class);
			entities.add(new Stand(x, y, id));
		} break;
		case "flower":{
			String id = properties.get("ID", String.class);
			entities.add(new Flower(x, y, id));
		} break;
		case "walkway":{
			entities.add(new Walkway(x, y, width, height));
		} break;
		case "memorial":{
			entities.add(new Memorial(x, y, width, height));
		} break;
		case "painting":{
			String id = properties.get("ID", String.class);
			String text = properties.get("TEXT", String.class);
			if (FrameEngine.getSaveFile().getFlag("WORLD_REWARD")){
				entities.add(new Painting(x, y, text, id));
			}
		} break;
		case "camera_trigger":{
			int yChange = Math.round(Float.parseFloat(properties.get("Y_OFFSET", String.class)));
			entities.add(new CameraTrigger(x, y, width, height, yChange));
		} break;
		case "answer":{
			String id = properties.get("ID", String.class);
			entities.add(new Answer(x, y, width, height, id));
		} break;
		case "sale_sign":{
			entities.add(new SaleSign(x, y));
		} break;
		case "question_trigger":{
			entities.add(new QuestionTrigger(x, y, width, height));
		} break;
		case "cat":{
			entities.add(new WildCat(x, y));
		} break;
		case "instrument":{
			String sfx = properties.get("SFX", String.class);
			entities.add(new Instrument(x, y, sfx));
		} break;
		case "fire":{
			String id = properties.get("ID", String.class);
			entities.add(new Fire(x, y, id));
		} break;
		case "pumpkin":{
			entities.add(new PumpkinCarve(x, y));
		} break;
		case "grass":{
			entities.add(new Grass(x, y));
		} break;
		case "koi":{
			entities.add(new Koi(x, y));
		} break;
		case "movelight":{
			entities.add(new MoveLight(x, y));
		} break;
		default: {
			FrameEngine.logger.log( Level.WARNING, 
					"Couldn't parse type " + type + " of " + entity.getName() + "");
		}
		}
	}

	private boolean checkFlag(MapObject entity, String flag){
		if (null == flag){
			FrameEngine.logger.log( Level.WARNING, 
					"Couldn't find flag of " + entity.getName() + "");
		}
		return (FrameEngine.getSaveFile().getFlag(flag));
	}

}
