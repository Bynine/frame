package overworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import encounter.Monster;
import main.CSVReader;
import main.FrameEngine;
import main.AudioHandler;

/**
 * Contains all info about a particular overworld area.
 */
public class Area {
	private final TmxMapLoader tmx_map_loader = new TmxMapLoader();
	private final HashSet<Rectangle> areaCollision = new HashSet<>();
	private final HashSet<Rectangle> slopes = new HashSet<>();
	private final HashMap<Entity, Rectangle> hitboxCollision = new HashMap<>();
	public final TiledMap map;
	public final int map_width;
	public final int map_height;
	public final boolean cameraFixed;
	public final String overlayString;

	public Area(String id){
		String[] data = new CSVReader().load_map_data(id);
		AudioHandler.start_new_audio("music/" + data[2] + ".mp3");
		cameraFixed = Boolean.parseBoolean(data[3].toLowerCase());
		overlayString = data[4].toLowerCase();
		map = tmx_map_loader.load("maps/" + id + ".tmx");
		map_width  = getMap().getProperties().get("width",  Integer.class) * FrameEngine.TILE;
		map_height = getMap().getProperties().get("height", Integer.class) * FrameEngine.TILE;
		MapObjects map_objects = map.getLayers().get("COLLISION").getObjects();
		for (int ii = 0; ii < map_objects.getCount(); ++ii){
			RectangleMapObject rectangle_map_object = (RectangleMapObject) map_objects.get(ii);
			areaCollision.add(new Rectangle(rectangle_map_object.getRectangle()));
		}
		MapObjects map_slopes = map.getLayers().get("SLOPES").getObjects();
		for (int ii = 0; ii < map_slopes.getCount(); ++ii){
			RectangleMapObject map_slope = (RectangleMapObject) map_slopes.get(ii);
			slopes.add(new Rectangle(map_slope.getRectangle()));
		}
	}
	
	/**
	 * Adds an entity's hitbox to the map collision.
	 */
	public void addToCollision(Entity en) {
		Rectangle hitbox = en.getHitbox();
		hitboxCollision.put(en, hitbox);
		areaCollision.add(hitbox);
	}
	
	/**
	 * Removes an entity's hitbox from the map collision.
	 */
	public void removeFromCollision(Entity en){
		if (hitboxCollision.containsKey(en)){
			Rectangle hitbox = hitboxCollision.get(en);
			areaCollision.remove(hitbox);
			hitboxCollision.remove(en);
		}
	}

	public TiledMap getMap() {
		return map;
	}

	public HashSet<Rectangle> getCollision(){
		return areaCollision;
	}

	public HashSet<Rectangle>  getSlopes() {
		return slopes;
	}
	
	/**
	 * Generates an enemy encounter based on the map data.
	 */
	public ArrayList<Monster> generate_encounter(){
		ArrayList<Monster> encounter = new ArrayList<Monster>();
//		String[] data = new CSVReader().load_map_data(id);
//		String[] encounters_data = data[2].split(CSVReader.long_split);
//		String[] range = data[3].split(CSVReader.short_split);
//		int num_enemies = FrameEngine.get_rand_num_in_range(
//				Integer.parseInt(range[0]),
//				Integer.parseInt(range[1]));
//		HashMap<Integer, Vector2> monster_odds = make_monster_odds(encounters_data);
//		for (int ii = 0; ii < num_enemies; ++ii){
//			int chosen_monster_pos = get_monster_from_odds(encounters_data, monster_odds);
//			String[] monster_data = encounters_data[chosen_monster_pos].split(CSVReader.short_split);
//			String species = monster_data[0];
//			int level = FrameEngine.get_rand_num_in_range(Integer.parseInt(monster_data[1]), 
//					Integer.parseInt(monster_data[2]));
//			encounter.add(new Monster(species, level));
//		}
		return encounter;
	}

	/**
	 * Creates a hashmap detailing the odds of finding each particular species in an area.
	 */
//	private HashMap<Integer, Vector2> make_monster_odds(String[] encounters_data){
//		float odds_total = 0;
//		HashMap<Integer, Vector2> monster_pos_odds = new HashMap<Integer, Vector2>();
//		for (int ii = 0; ii < encounters_data.length; ++ii){
//			int odds = Integer.parseInt(encounters_data[ii].split(CSVReader.short_split)[3]);
//			Vector2 chance_range = new Vector2(odds_total, odds_total + odds);
//			odds_total += odds;
//			monster_pos_odds.put(ii, chance_range);
//		}
//		for (int ii = 0; ii < encounters_data.length; ++ii){
//			monster_pos_odds.get(ii).scl(1.0f/odds_total); // All odds are now out of 1
//		}
//		return monster_pos_odds;
//	}

	/**
	 * Picks out a position representing a species at random from a 
	 * hashmap of vector2s representing probability ranges.
	 */
//	private int get_monster_from_odds(String[] encounters_data, HashMap<Integer, Vector2> monster_odds){
//		float rand_val = (float) Math.random();
//		int chosen_monster_pos = -1;
//		for (int jj = 0; jj < encounters_data.length; ++jj){
//			if (rand_val >= monster_odds.get(jj).x && rand_val < monster_odds.get(jj).y){
//				chosen_monster_pos = jj;
//			}
//		}
//		if (chosen_monster_pos == -1){
//			FrameEngine.logger.log(Level.SEVERE, "Monster odds table couldn't find a result!");
//		}
//		return chosen_monster_pos;
//	}

}
