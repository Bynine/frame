package overworld;

import java.util.ArrayList;
import java.util.logging.Level;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;

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
		int num_enemies = get_rand_num_in_range(Integer.parseInt(range[0]),Integer.parseInt(range[1]));
		for (int ii = 0; ii < num_enemies; ++ii){
			String[] monster_data = 
					encounters_data[get_rand_num_in_range(0, encounters_data.length-1)]
							.split(CSVReader.short_split);
			String species = monster_data[0];
			int level = get_rand_num_in_range(Integer.parseInt(monster_data[1]), 
					Integer.parseInt(monster_data[2]));
			encounter.add(new Monster(species, level));
		}
		return encounter;
	}

	/**
	 * Gets a number between or at a and b.
	 */
	private int get_rand_num_in_range(int a, int b){
		return a + (int)(Math.random() * (b + 1 - a));
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

	public ArrayList<Entity> getEntities(){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		MapObjects map_entities = map.getLayers().get("ENTITIES").getObjects();
		for (int ii = 0; ii < map_entities.getCount(); ++ii){
			MapObject entity = map_entities.get(ii);
			MapProperties properties = entity.getProperties();
			int x = Math.round(properties.get("x", Float.class));
			int y = Math.round(properties.get("y", Float.class));
			switch(entity.getName().toLowerCase()){
			case "enemy": entities.add(new Chaser(x, y)); break;
			default: {
				FrameEngine.logger.log(Level.WARNING, "Unsure what " + entity.getName() + " is.");
			}break;
			}
		}
		return entities;
	}

}
