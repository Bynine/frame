package main;

import java.util.ArrayList;
import java.util.List;

import text.Button;

/**
 * Represents player's items.
 */
public class Inventory extends AbstractMenu{
	
	private final ArrayList<String> items = new ArrayList<String>();
	private final ArrayList<Button> descs = new ArrayList<Button>();
	
	Inventory(){
		//items.add("KEEPSAKE");
	}
	
	public boolean hasItem(String item){
		return items.contains(item);
	}
	
	public void addItem(String item){
		items.add(item);
	}
	
	public void removeItem(String item){
		items.remove(item);
		// TOOD: Look through buttons, dispose item description in matching button
		descs.remove(item);
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
			descs.add(new Button(2, 2, item, new ItemDescription(item)));
		}
	}

	@Override
	public List<Button> getList() {
		return descs;
	}

	@Override
	protected void selectItem() {
		//
	}
	
}
