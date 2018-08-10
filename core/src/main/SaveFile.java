package main;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Keeps track of all recorded game variables.
 */
public class SaveFile {

	private final HashMap<String, Boolean> flags = new HashMap<>();
	private final HashMap<String, Integer> counters = new HashMap<>();
	
	private String moneyKey = "smiles";
	private int money = 0;
	
	private String inventoryKey = "kindnesses";
	private final ArrayList<String> inventory = new ArrayList<String>();
	
	private static final String saveFile = "mrbsv.xml";
	
	@SuppressWarnings("unchecked")
	SaveFile(){
		if (!FrameEngine.SAVE) return;
		Preferences preferences = Gdx.app.getPreferences(saveFile);
		for (String key: preferences.get().keySet()){
			flags.put(key, preferences.getBoolean(key));
		}
		money = preferences.getInteger(moneyKey);
		inventory.addAll((ArrayList<String>) preferences.get().get(inventoryKey));
	}
	
	/**
	 * Puts game state into save file.
	 */
	void save(){
		if (!FrameEngine.SAVE) return;
		Preferences preferences = Gdx.app.getPreferences(saveFile);
		preferences.put(flags);
		preferences.putInteger(moneyKey, money);
		preferences.put(new HashMap<String, ArrayList<String>>(){{
			put(inventoryKey, inventory);
		}});
		preferences.flush();
	}
	
	/**
	 * Sets the value of the given flag.
	 */
	public void setFlag(String flag, boolean bool){
		flags.put(flag, bool);
	}
	
	/**
	 * Checks if this flag has been set to true.
	 */
	public boolean getFlag(String flag){
		if (flags.containsKey(flag)){
			return flags.get(flag);
		}
		return false;
	}
	
	public void addItem(String item){
		inventory.add(item);
	}
	
	public boolean hasItem(String checkItem){
		return inventory.contains(checkItem);
	}
	
	/**
	 * If the counter does not exist, creates it and sets it to n.
	 * If it does, adds the given value.
	 */
	public void addToCounter(int n, String counter){
		if (counters.containsKey(counter)){
			counters.put(counter, counters.get(counter) + n);
		}
		else{
			counters.put(counter, n);
		}
	}
	
	public int getMoney(){
		return money;
	}
	
	public ArrayList<String> getInventory(){
		return inventory;
	}

	/**
	 * If counter exists, returns its value.
	 * If it doesn't, returns -1.
	 */
	public int getCounter(String string) {
		if (!counters.containsKey(string)) return -1;
		else return counters.get(string);
	}
	
}
