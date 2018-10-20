package main;

import java.util.logging.Level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Gives back String arrays from csv files.
 */
public class TSVReader {

	public static final String split = "\t";
	public static final String long_split = " ";
	public static final String short_split = "&";

	public static final String 
	MAP_URL = "data/maps.tsv",
	NPC_URL = "data/npcs.tsv",
	ITEM_URL = "data/items.tsv",
	QUESTION_URL = "data/questions.tsv"
	;

	private String data = null;

	/**
	 * Finds the id from the csv list and returns the relevant data.
	 */
	public String[] loadDataByID(String id, String url){
		String[] results = getByID(id, url);
		return results;
	}
	
	public String[] loadAllData(String url){
		getData(url);
		return data.split("\n");
	}

	/**
	 * Gets a member of the list.
	 */
	private String[] getByID(String id, String url){
		final int idPosition = 0;
		String speciesLine = null;
		getData(url);
		for (String line: data.split("\n")){ // Check each member...
			if (id.equals(line.split(split)[idPosition])){ // We want the ID column to match the requested ID.
				speciesLine = line;
			}
		}
		if (null == speciesLine){
			FrameEngine.logger.log(Level.SEVERE, "Couldn't load data with id " + id);
		}
		return parseLine(speciesLine);
	}

	/**
	 * Loads data from the requested url.
	 */
	private void getData(String url){
		FileHandle handle = Gdx.files.internal(url);
		data = handle.readString();
	}

	/**
	 * Takes the line and turns it into an array of data.
	 */
	private String[] parseLine(String line){
		String[] results = line.split(split);
		int last_index = results.length - 1;
		char last_char = 
				results[last_index].substring
				(results[last_index].length()-1, results[last_index].length()).toCharArray()[0];
		if (last_char == 13 || last_char == 10){ // Remove return character from end of last value in line
			results[last_index] = results[last_index].substring(0, results[last_index].length()-1); 
		}
		return results;
	}

}
