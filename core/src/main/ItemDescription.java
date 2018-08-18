package main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Information about item stored in inventory.
 */
public class ItemDescription {
	
	final String id, name, description;
	final TextureRegion icon;

	ItemDescription(String id){
		String[] data = new TSVReader().loadDataByID(id, TSVReader.ITEM_URL);
		this.id = id;
		name = data[1];
		icon = new TextureRegion(new Texture("sprites/items/" + data[0].toLowerCase() + ".png"));
		description = data[3];
	}
	
	@Override
	public String toString(){
		return String.format("%s: %s", name, description);
	}
	
	public void dispose(){
		icon.getTexture().dispose();
	}
	
}
