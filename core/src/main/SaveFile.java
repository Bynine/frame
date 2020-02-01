package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

/**
 * Keeps track of all recorded game variables.
 */
public class SaveFile {

	private final HashMap<String, Boolean> flags = new HashMap<>();
	private final HashMap<String, Integer> counters = new HashMap<>();
	private final HashMap<String, String> map = new HashMap<>();

	private String moneyKey = "smiles";
	private String inventoryKey = "kindnesses";
	private String areaKey = "travels";
	private String mapPrefix = "MAP_";
	private String positionX = "turnips";
	private String positionY = "snails";
	private boolean exists = false;

	public String startArea = "";
	public final Vector2 startPosition = new Vector2();

	private int money = 0;
	private static final String defaultSaveFile = "mrbsv.xml";
	public static final String CREDITS = "CREDITS";
	private boolean verbose;
	
	SaveFile(String prefs){
		Preferences preferences = Gdx.app.getPreferences(prefs);
		ArrayList<String> flags = new ArrayList<String>();
		String items = "";
		switch(prefs) {
		case "FROST":{
			flags.addAll(Arrays.asList(""));
		} break;
		}
		for(String flag: flags) {
			preferences.putBoolean(flag, true);
		}
		preferences.putInteger(moneyKey, 4);
		preferences.putString(inventoryKey, items);
		loadFromFile(preferences);
	}

	SaveFile(){
		if (FrameEngine.SHRINE){
			setFlag("ENTERED_SHRINE");
		}
		if (FrameEngine.FGOAL) {
			setFlag("FOUND_GOAL");
		}
		if (FrameEngine.FLAME) {
			setFlag("FOUND_FLAME");
		}
		if (!FrameEngine.SAVE) return;
		Preferences preferences = Gdx.app.getPreferences(defaultSaveFile);
		loadFromFile(preferences);
	}
	
	private void loadFromFile(Preferences preferences) {
		this.verbose = FrameEngine.LOG;

		exists = !preferences.get().isEmpty();
		money = preferences.getInteger(moneyKey);

		for (String key: preferences.get().keySet()){
			String value = preferences.get().get(key).toString();
			if (value.equals("true")){
				if (verbose) System.out.println("Bool:" + key + " " + value);
				flags.put(key, true);
			}
			else{
				try{
					int val = Integer.parseInt(value);
					if (verbose) System.out.println("Int: " + key + " " + val);
					counters.put(key, val);
				}
				catch(Exception e){
					// kludgey 
				}
			}
		}

		if (preferences.get().containsKey(inventoryKey)){
			String[] items = preferences.getString(inventoryKey).split(",");
			for (String item: items){
				if (!item.isEmpty()){
					if (verbose) {
						if (FrameEngine.getInventory().addItemConditional(item)) {
							System.out.println("Added item: " + item);
						}
						else {
							System.out.println("Wouldn't add duplicate item: " + item);
						}
					}
					
				}
			}
		}

		for (String key: preferences.get().keySet()){
			if (key.startsWith(mapPrefix)){
				map.put(key.substring(4), preferences.get().get(key).toString());
			}
		}
		if (verbose) System.out.println("Map: " + map.toString());
		boolean savedInTrials = false;

		if (preferences.get().containsKey(areaKey)){
			String area = preferences.getString(areaKey);
			// If player saves during trials, then loads, they're booted back to start
			if (area.startsWith("DEPTHSQ")){
				if (verbose) System.out.println("Naughty player saved in " + area);
				startArea = "UNDERSHRINE";
				startPosition.set(18, 22);
				savedInTrials = true;
			}
			else{
				if (verbose) System.out.println("Now arriving at: " + area);
				startArea = area;
			}
		}

		if (!savedInTrials){
			if (preferences.get().containsKey(positionX) && preferences.get().containsKey(positionY)){
				startPosition.set(
						preferences.getFloat(positionX),
						preferences.getFloat(positionY)
						);
				if (verbose) System.out.println(startPosition);
			}
		}
	}

	/**
	 * Puts game state into save file.
	 */
	void save(boolean credits){
		if (!FrameEngine.SAVE) return;
		Preferences preferences = Gdx.app.getPreferences(defaultSaveFile);
		if (credits){
			preferences.putString(CREDITS, "true");
		}
		
		preferences.put(flags);
		preferences.put(counters);
		
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()){
			String key = iter.next();
			preferences.putString(mapPrefix + key, map.get(key));
			if (verbose) System.out.println("Mapped " + (mapPrefix + key) + " to " + map.get(key));
		}
		preferences.putInteger(moneyKey, money);

		float x = (FrameEngine.getPlayer().getPosition().x)/FrameEngine.TILE;
		if (credits) x = 0.5f;
		float y = ((FrameEngine.getArea().mapHeight - FrameEngine.getPlayer().getPosition().y)
				/FrameEngine.TILE);
		startArea = FrameEngine.getArea().getID();
		startPosition.set(x, y);
		if (verbose) System.out.println("Saved player to " + x + " " + y);
		
		preferences.putString(areaKey, FrameEngine.getArea().getID());
		preferences.putFloat(positionX, x);
		preferences.putFloat(positionY, y);

		StringBuilder builder = new StringBuilder();
		for (String itemId: FrameEngine.getInventory().getItems()){
			if (verbose) System.out.println(itemId);
			builder.append(itemId + ",");
		}
		preferences.putString(inventoryKey, builder.toString());

		preferences.flush();
		if (verbose) System.out.println("Saved!");
	}
	
	/**
	 * Sets flag to true.
	 */
	public void setFlag(String flag) {
		this.setFlag(flag, true);
	}

	/**
	 * Sets the value of the given flag.
	 */
	public void setFlag(String flag, boolean bool){
		boolean invert = flag.startsWith("!");
		if (invert){
			flag = flag.substring(1);
		}
		flags.put(flag, invert ^ bool);
		if (verbose) System.out.println(flag + " set to " + bool);
	}

	/**
	 * Checks if this series of flags has been set to true.
	 */
	public boolean getFlag(String flag){
		if (FrameEngine.ALLTRUE) {
			return true;
		}
		String[] flags = flag.split(",");
		boolean isFlag = true;
		for (String subFlag: flags){
			if (!getFlagHelper(subFlag)) isFlag = false;
		}
		return isFlag;
	}

	/**
	 * Checks if this flag has been set to true.
	 */
	private boolean getFlagHelper(String flag){
		boolean value = false;
		boolean invert = flag.startsWith("!");
		if (invert){
			flag = flag.substring(1);
		}
		if (flags.containsKey(flag)){
			return invert ^ flags.get(flag);
		}
		return invert ^ value;
	}

	/**
	 * If the counter does not exist, creates it and sets it to n.
	 * If it does, adds the given value.
	 */
	public void addToCounter(int n, String counter){
		if (counters.containsKey(counter)){
			if (verbose) System.out.println("Added " + n + " to counter " + counter);
			counters.put(counter, counters.get(counter) + n);
		}
		else{
			if (verbose) System.out.println("Added new counter " + counter + " with value " + n);
			counters.put(counter, n);
		}
	}

	public void addMoney(int n){
		money += n;
	}

	public int getMoney(){
		return money;
	}

	/**
	 * If counter exists, returns its value.
	 * If it doesn't, returns -1.
	 */
	public int getCounter(String string) {
		if (!counters.containsKey(string)) return -1;
		else return counters.get(string);
	}

	public void wipeSave() {
		Preferences preferences = Gdx.app.getPreferences(defaultSaveFile);
		preferences.clear();
		preferences.flush();
		flags.clear();
		counters.clear();
		map.clear();
		money = 0;
		setRandomFlags();
		exists = false;
	}

	public void setRandomFlags(){
		setRandomFlag("RAND1");
		setRandomFlag("RAND2");
		setRandomFlag("RAND3");
		setRandomFlag("RAND4");
	}

	private void setRandomFlag(String flag){
		if (coinFlip()){
			setFlag(flag, true);
		}
	}

	private boolean coinFlip(){
		return Math.random() > 0.5f;
	}

	public void setMapping(String key, String value) {
		map.put(key, value);
	}

	public String getMapping(String key) {
		if (!map.containsKey(key)) return "";
		else return map.get(key);
	}

	public boolean exists() {
		return exists;
	}

}
