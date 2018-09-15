package main;

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
	private String inventoryKey = "kindnesses";
	private String mapKey = "travels";
	
	private int money = 0;
	private static final String saveFile = "mrbsv.xml";
	
	private boolean verbose = false;

	SaveFile(boolean verbose){
		this.verbose = verbose;
		// TODO: remove
		flags.put("ENTERED_SHRINE",  true);
		if (!FrameEngine.SAVE) return;
		Preferences preferences = Gdx.app.getPreferences(saveFile);
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
		
		if (preferences.get().containsKey(mapKey)){
			if (verbose) System.out.println("Now arriving at: " + mapKey);
			FrameEngine.startAreaName = preferences.getString(mapKey);
		}
	}
	
	/**
	 * Puts game state into save file.
	 */
	void save(){
		if (!FrameEngine.SAVE) return;
		Preferences preferences = Gdx.app.getPreferences(saveFile);
		preferences.put(flags);
		preferences.put(counters);
		preferences.putInteger(moneyKey, money);
		preferences.putString(mapKey, FrameEngine.getArea().getID());
		
		StringBuilder builder = new StringBuilder();
		for (Object obj: FrameEngine.getInventory().getList()){
			ItemDescription desc = (ItemDescription) obj;
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
	 * Checks if this flag has been set to true.
	 */
	public boolean getFlag(String flag){
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
	}
	
}
