package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class CSVReader {
	
	public static final String long_split = " ";
	public static final String short_split = "&";
	
	String data = null;

	/**
	 * Finds the species id from the csv list and returns the relevant data.
	 */
	public String[] load_species_data(String id){
		String[] results = get_by_id(id, "data/species - Sheet1.csv");
		return results;
	}
	
	/**
	 * Finds the tech id from the csv list and returns the relevant data.
	 */
	public String[] load_tech_data(String id){
		String[] results = get_by_id(id, "data/techniques - Sheet1.csv");
		return results;
	}
	
	/**
	 * Gets a member of the list.
	 */
	private String[] get_by_id(String id, String url){
		final int id_pos = 1;
		String species_line = null;
		get_data(url);
		for (String line: data.split("\n")){ // Check each member...
			if (id.equals(line.split(",")[id_pos])){ // We want the ID column to match the requested ID.
				species_line = line;
			}
		}
		String[] results = species_line.split(",");
		int last_index = results.length - 1;
			// Remove return character from end of last value in line
		results[last_index] = results[last_index].substring(0, results[last_index].length()-1); 
		return results;
	}
	
	/**
	 * Loads data from the requested url.
	 */
	private void get_data(String url){
		FileHandle handle = Gdx.files.internal(url);
		data = handle.readString();
	}
	
}
