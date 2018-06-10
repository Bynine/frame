package overworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import battle.Monster;
import main.CSVReader;
import main.FrameEngine;

/**
 * Contains all info about a particular overworld area.
 */
public class Area {

	private final TmxMapLoader tmx_map_loader = new TmxMapLoader();
	private final TiledMap map;
	private final ArrayList<Rectangle> collision = new ArrayList<>();
	private final int map_width;
	private final int map_height;
	private final String id;

	public Area(String id){
		this.id = id;
		map = tmx_map_loader.load("maps/" + id + ".tmx");
		map_width  = getMap().getProperties().get("width",  Integer.class) * FrameEngine.TILE;
		map_height = getMap().getProperties().get("height", Integer.class) * FrameEngine.TILE;
		MapObjects map_objects = map.getLayers().get("COLLISION").getObjects();
		for (int ii = 0; ii < map_objects.getCount(); ++ii){
			RectangleMapObject rectangle_map_object = (RectangleMapObject) map_objects.get(ii);
			collision.add(new Rectangle(rectangle_map_object.getRectangle()));
		}
	}

	/**
	 * Generates an enemy encounter based on the map data.
	 */
	public ArrayList<Monster> generate_encounter(){
		ArrayList<Monster> encounter = new ArrayList<Monster>();
		String[] data = new CSVReader().load_map_data(id);
		String[] encounters_data = data[2].split(CSVReader.long_split);
		String[] range = data[3].split(CSVReader.short_split);
		int num_enemies = FrameEngine.get_rand_num_in_range(
				Integer.parseInt(range[0]),
				Integer.parseInt(range[1]));
		HashMap<Integer, Vector2> monster_odds = make_monster_odds(encounters_data);
		for (int ii = 0; ii < num_enemies; ++ii){
			int chosen_monster_pos = get_monster_from_odds(encounters_data, monster_odds);
			String[] monster_data = encounters_data[chosen_monster_pos].split(CSVReader.short_split);
			String species = monster_data[0];
			int level = FrameEngine.get_rand_num_in_range(Integer.parseInt(monster_data[1]), 
					Integer.parseInt(monster_data[2]));
			encounter.add(new Monster(species, level));
		}
		return encounter;
	}
	
	/**
	 * Creates a hashmap detailing the odds of finding each particular species in an area.
	 */
	private HashMap<Integer, Vector2> make_monster_odds(String[] encounters_data){
		float odds_total = 0;
		HashMap<Integer, Vector2> monster_pos_odds = new HashMap<Integer, Vector2>();
		for (int ii = 0; ii < encounters_data.length; ++ii){
			int odds = Integer.parseInt(encounters_data[ii].split(CSVReader.short_split)[3]);
			Vector2 chance_range = new Vector2(odds_total, odds_total + odds);
			odds_total += odds;
			monster_pos_odds.put(ii, chance_range);
		}
		for (int ii = 0; ii < encounters_data.length; ++ii){
			monster_pos_odds.get(ii).scl(1.0f/odds_total); // All odds are now out of 1
		}
		return monster_pos_odds;
	}
	
	/**
	 * Picks out a position representing a species at random from a 
	 * hashmap of vector2s representing probability ranges.
	 */
	private int get_monster_from_odds(String[] encounters_data, HashMap<Integer, Vector2> monster_odds){
		float rand_val = (float) Math.random();
		int chosen_monster_pos = -1;
		for (int jj = 0; jj < encounters_data.length; ++jj){
			if (rand_val >= monster_odds.get(jj).x && rand_val < monster_odds.get(jj).y){
				chosen_monster_pos = jj;
			}
		}
		if (chosen_monster_pos == -1){
			FrameEngine.logger.log(Level.SEVERE, "Monster odds table couldn't find a result!");
		}
		return chosen_monster_pos;
	}

	/**
	 * Looks at the map data to generate entities for EntityHandler to use.
	 */
	public ArrayList<Entity> create_entities(){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		MapObjects map_entities = map.getLayers().get("ENTITIES").getObjects();
		for (int ii = 0; ii < map_entities.getCount(); ++ii){
			MapObject entity = map_entities.get(ii);
			MapProperties properties = entity.getProperties();
			int x = Math.round(properties.get("x", Float.class));
			int y = Math.round(properties.get("y", Float.class));
			switch(entity.getName().toLowerCase()){
			case "enemy": {
				entities.add(new Chaser(x, y));
			} break;
			case "boss": {
				entities.add(new Boss(x, y));
			} break;
			case "portal": {
				String id = properties.get("DEST", String.class);
				entities.add(new Portal(x, y, id));
			} break;
			default: {
				FrameEngine.logger.log(Level.WARNING, "Unsure what " + entity.getName() + " is.");
			}break;
			}
		}
		return entities;
	}

	public TiledMap getMap() {
		return map;
	}

	public ArrayList<Rectangle> getCollision(){
		return collision;
	}

	public int getWidth(){
		return map_width;
	}

	public int getHeight(){
		return map_height;
	}

}
