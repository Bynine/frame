package main;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

import text.MenuOption;

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
	
	public String startArea = "FOREST";
	public final Vector2 startPosition = new Vector2();

	private int money = 0;
	private static final String saveFile = "mrbsv.xml";

	private boolean verbose = false;

	SaveFile(boolean verbose){
		this.verbose = verbose;
		if (FrameEngine.DEBUG){
			//flags.put("ENTERED_SHRINE", true);
		}
		if (!FrameEngine.SAVE) return;
		Preferences preferences = Gdx.app.getPreferences(saveFile);
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
					if (verbose) System.out.println("Added item: " + item);
					FrameEngine.getInventory().addItem(item);
				}
			}
		}

		for (String key: preferences.get().keySet()){
			if (key.startsWith(mapPrefix)){
				map.put(key.substring(4), preferences.get().get(key).toString());
			}
		}
		if (verbose) System.out.println("Map: " + map.toString());

		if (preferences.get().containsKey(areaKey)){
			if (verbose) System.out.println("Now arriving at: " + preferences.getString(areaKey));
			startArea = preferences.getString(areaKey);
		}
		
		if (preferences.get().containsKey(positionX) && preferences.get().containsKey(positionY)){
			startPosition.set(
					preferences.getFloat(positionX),
					preferences.getFloat(positionY)
					);
			if (verbose) System.out.println(startPosition);
		}
		
	}

	/**
	 * Puts game state into save file.
	 */
	void save(boolean positionIsSet){
		if (!FrameEngine.SAVE) return;
		Preferences preferences = Gdx.app.getPreferences(saveFile);
		preferences.put(flags);
		preferences.put(counters);
		for (String key: map.keySet()){
			map.put(mapPrefix + key, map.get(key));
			map.remove(key);
		}
		preferences.put(map);
		preferences.putInteger(moneyKey, money);
		
		float x = (FrameEngine.getPlayer().getPosition().x)/FrameEngine.TILE;
		if (positionIsSet) x = 1;
		float y = ((FrameEngine.getArea().mapHeight - FrameEngine.getPlayer().getPosition().y)
				/FrameEngine.TILE);
		startArea = FrameEngine.getArea().getID();
		startPosition.set(x, y);
		if (verbose) System.out.println("Saved player to " + x + " " + y);
		preferences.putString(areaKey, FrameEngine.getArea().getID());
		preferences.putFloat(positionX, x);
		preferences.putFloat(positionY, y);

		StringBuilder builder = new StringBuilder();
		for (MenuOption menuOption: FrameEngine.getInventory().getList()){
			ItemDescription desc = (ItemDescription) menuOption.getOutput();
			builder.append(desc.id + ",");
		}
		preferences.putString(inventoryKey, builder.toString());

		preferences.flush();
		if (verbose) System.out.println("Saved!");
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
			counters.put(counter, counters.get(counter) + n);
		}
		else{
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
		Preferences preferences = Gdx.app.getPreferences(saveFile);
		preferences.clear();
		preferences.flush();
		flags.clear();
		counters.clear();
		map.clear();
		money = 0;
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
