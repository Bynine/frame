package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents player's items.
 */
public class Inventory extends AbstractMenu{
	
	private final ArrayList<String> items = new ArrayList<String>();
	private final ArrayList<ItemDescription> descs = new ArrayList<ItemDescription>();
	private final HashMap<String, ItemDescription> descMap = new HashMap<>();
	
	Inventory(){
		items.add("KEEPSAKE");
		items.add("APPLE");
	}
	
	public boolean hasItem(String item){
		return items.contains(item);
	}
	
	public void addItem(String item){
		items.add(item);
	}
	
	public void removeItem(String item){
		items.remove(item);
		descs.remove(item);
		descMap.get(item).dispose();
		descMap.remove(item);
	}

	/**
	 * descs will have a description for each item in items.
	 */
	public void open(){
		cursor = 0;
		updateDescriptions();
	}
	
	private void updateDescriptions(){
		descs.clear();
		for (String item: items){
			if (!(descMap.containsKey(item))){
				descMap.put(item, new ItemDescription(item));
			}
			descs.add(descMap.get(item));
		}
	}
	
	public List<ItemDescription> getDescriptions(){
		return descs;
	}

	@Override
	public List<? extends Object> getList() {
		return getDescriptions();
	}

	@Override
	protected void selectItem() {
		// ???
	}
	
}
