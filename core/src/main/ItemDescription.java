package main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Information about item stored in inventory.
 */
public class ItemDescription {
	
	public final String id;
	public final String name;
	public final String description;
	public final String[] attributes;
	public final int price;
	public final TextureRegion icon;

	public ItemDescription(String id){
		String[] data = new TSVReader().loadDataByID(id, TSVReader.ITEM_URL);
		this.id = id;
		icon = new TextureRegion(new Texture("sprites/items/" + data[0].toLowerCase() + ".png"));
		name = data[1];
		attributes = data[2].split(",");
		if (data[3].equals("GHOST")){
			description = setGhostDescription();
		}
		else{
			description = data[3];
		}
		price = Integer.parseInt(data[4]);
	}
	
	private String setGhostDescription(){
		if (FrameEngine.getSaveFile().getFlag(ProgressionHandler.ghostLeslie)){
			return "\"I remembered everybody!\"";
		}
		else if (FrameEngine.getSaveFile().getFlag(ProgressionHandler.ghostLibrarian)){
			return "\"I remember someone who liked... red coffee?...\"";
		}
		else if (FrameEngine.getSaveFile().getFlag(ProgressionHandler.ghostShopkeeper)){
			return "\"I remember someone who loved old books...\"";
		}
		else{
			return "\"I remember someone who wanted to open a shop...\"";
		}
	}
	
	public boolean tooExpensive(){
		return price > FrameEngine.getSaveFile().getMoney();
	}
	
	public boolean hasAttribute(String attr){
		for (String attribute: attributes){
			if (attr.equals(attribute)) return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return String.format("%s: %s", name, description);
	}
	
	public void dispose(){
		icon.getTexture().dispose();
	}
	
}
