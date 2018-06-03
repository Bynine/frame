package encounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import main.CSVReader;

public class Species {

	public final String name;
	public final String base_technique;
	public final int[] base_stats = new int[Monster.NUM_STATS];
	public final Vector3 palette_range = new Vector3();
	final HashMap<Integer, ArrayList<String>> tech_levelup = new HashMap<Integer, ArrayList<String>>();
	final HashMap<Integer, ArrayList<String>> trait_levelup = new HashMap<Integer, ArrayList<String>>();
	public final TextureRegion front, back;

	/**
	 * Contains fields for unchanging, species-specific characteristics.
	 */
	public Species(String id) {
		String[] data = new CSVReader().load_species_data(id);
		name = data[0];
		base_stats[Monster.VIT] = Integer.parseInt(data[2]);
		base_stats[Monster.POW] = Integer.parseInt(data[3]);
		base_stats[Monster.DEF] = Integer.parseInt(data[4]);
		base_stats[Monster.EMP] = Integer.parseInt(data[5]);
		base_stats[Monster.AGI] = Integer.parseInt(data[6]);
		for (String trait: data[8].split(CSVReader.long_split)){ // go through each trait in turn.
			String[] trait_data = trait.split(CSVReader.short_split);
			if (trait_data.length > 1){ // don't add any traits if the species has none
				add_to_levelup(trait_levelup, trait_data);
			}
		}
		for (String tech: data[9].split(CSVReader.long_split)){ // go through each technique in turn.
			String[] tech_data = tech.split(CSVReader.short_split);
			add_to_levelup(tech_levelup, tech_data);
		}
		base_technique = tech_levelup.get(1).get(0);
		
		String debug_image_id = "DUMMY"; // TODO: replace with id
		front = new TextureRegion(new Texture(Gdx.files.internal("sprites/battle/" + debug_image_id + "_FRONT.png")));
		back = new TextureRegion(new Texture(Gdx.files.internal("sprites/battle/" + debug_image_id + "_BACK.png")));
		palette_range.x = ((float)(Integer.parseInt(data[10])))/255.0f; //R variance
		palette_range.y = ((float)(Integer.parseInt(data[11])))/255.0f; //G variance
		palette_range.z = ((float)(Integer.parseInt(data[12])))/255.0f; //B variance
	}
	
	/**
	 * Used to set the levelup pools for techs and traits.
	 */
	private void add_to_levelup(HashMap<Integer, ArrayList<String>> levelup, String[] data){
		int level = Integer.parseInt(data[1]);
		if (levelup.containsKey(level)){
			levelup.get(level).add(data[0]);
		}
		else{
			levelup.put(level, new ArrayList<String>(Arrays.asList(data[0])));
		}
	}
	
}
