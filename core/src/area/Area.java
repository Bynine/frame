package area;

import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import entity.Entity;
import main.TSVReader;
import main.FrameEngine;
import main.AudioHandler;

/**
 * Contains all info about a particular overworld area.
 */
public class Area {
	private final TmxMapLoader tmx_map_loader = new TmxMapLoader();
	private final HashSet<Rectangle> areaCollision = new HashSet<>();
	private final HashSet<Rectangle> slopes = new HashSet<>();
	private final HashMap<Rectangle, Terrain> terrains = new HashMap<>();
	private final HashMap<Entity, Rectangle> hitboxCollision = new HashMap<>();
	private final Vector2 startLocation = new Vector2();
	public final TiledMap map;
	public final int map_width;
	public final int map_height;
	public final boolean cameraFixed;
	public final String overlayString;

	public Area(String id){
		String[] data = new TSVReader().loadDataByID(id, TSVReader.MAP_URL);
		AudioHandler.startNewAudio("music/" + data[2] + ".ogg");
		cameraFixed = Boolean.parseBoolean(data[3].toLowerCase());
		overlayString = data[4].toLowerCase();
		String[] locationData = data[5].split("&");
		startLocation.set(
				Integer.parseInt(locationData[0]) * FrameEngine.TILE,
				Integer.parseInt(locationData[1]) * FrameEngine.TILE
				);
		map = tmx_map_loader.load("maps/" + id + ".tmx");
		map_width  = getMap().getProperties().get("width",  Integer.class) * FrameEngine.TILE;
		map_height = getMap().getProperties().get("height", Integer.class) * FrameEngine.TILE;
		
		setCollision();
		setMapSlopes();
		setTerrainObjects();
	}
	
	/**
	 * Loads collision from the map and adds the rectangles to areaCollision.
	 */
	private void setCollision(){
		MapObjects mapObjects = map.getLayers().get("COLLISION").getObjects();
		for (int ii = 0; ii < mapObjects.getCount(); ++ii){
			RectangleMapObject rectangle_map_object = (RectangleMapObject) mapObjects.get(ii);
			areaCollision.add(new Rectangle(rectangle_map_object.getRectangle()));
		}
	}
	
	/**
	 * Loads slopes from the map and adds them to slopes.
	 */
	private void setMapSlopes(){
		MapObjects mapSlopes = map.getLayers().get("SLOPES").getObjects();
		for (int ii = 0; ii < mapSlopes.getCount(); ++ii){
			RectangleMapObject mapSlope = (RectangleMapObject) mapSlopes.get(ii);
			slopes.add(new Rectangle(mapSlope.getRectangle()));
		}
	}
	
	/**
	 * Loads trrrain objects from the map, interprets their type, and adds them to terrain.
	 */
	private void setTerrainObjects(){
		MapObjects terrainObjects = map.getLayers().get("TERRAIN").getObjects();
		for (int ii = 0; ii < terrainObjects.getCount(); ++ii){
			RectangleMapObject terrainObj = (RectangleMapObject) terrainObjects.get(ii);
			Terrain terrain = Terrain.valueOf(terrainObj.getName().toUpperCase());
			Rectangle rect = new Rectangle(terrainObj.getRectangle());
			terrains.put(rect, terrain);
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
	
	public Terrain getTerrain(Entity en){
		Rectangle hitbox = new Rectangle(en.getHitbox());
		final int reduction = 6;
		hitbox.width -= reduction;
		hitbox.height -= reduction;
		hitbox.x += reduction/2;
		hitbox.y += reduction/2;
		for (Rectangle rect: terrains.keySet()){
			if (hitbox.overlaps(rect)) {
				return terrains.get(rect);
			}
		}
		return Terrain.NORMAL;
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
	
	public Vector2 getStartLocation(){
		return startLocation;
	}
	
	public enum Terrain{
		NORMAL, WOOD, WATER
	}

	public void dispose() {
		map.dispose();
	}

}