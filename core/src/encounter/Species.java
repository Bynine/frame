package encounter;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector3;
import com.frame.CSVReader;

public class Species {

	final String name;
	final String base_technique;
	final int[] base_stats = new int[Monster.NUM_STATS];
	final Vector3 palette_range = new Vector3();
	/** TODO: Change the way these work to allow for multiple moves at the same level.
	 */
	final HashMap<Integer, String> tech_levelup = new HashMap<Integer, String>();
	final HashMap<Integer, String> trait_levelup = new HashMap<Integer, String>();

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
				tech_levelup.put(Integer.parseInt(trait_data[1]), trait_data[0]);
			}
		}
		for (String tech: data[9].split(CSVReader.long_split)){ // go through each technique in turn.
			String[] tech_data = tech.split(CSVReader.short_split);
			tech_levelup.put(Integer.parseInt(tech_data[1]), tech_data[0]);
		}
		base_technique = tech_levelup.get(1);
		
		palette_range.x = ((float)(Integer.parseInt(data[10])))/255.0f; //R variance
		palette_range.y = ((float)(Integer.parseInt(data[11])))/255.0f; //G variance
		palette_range.z = ((float)(Integer.parseInt(data[12])))/255.0f; //B variance
	}
	
}
