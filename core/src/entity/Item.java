package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.TSVReader;
import text.Textbox;
import main.FrameEngine;

public class Item extends InteractableEntity {
	
	protected final String name;
	protected final String id;

	public Item(float x, float y, String id) {
		super(x, y, id);
		this.id = id;
		hitbox.setSize(24, 16);
		String[] data = new TSVReader().loadDataByID(id, TSVReader.ITEM_URL);
		image = new TextureRegion(new Texture("sprites/items/" + data[0].toLowerCase() + ".png"));
		name = data[1];
	}
	
	@Override
	public void interact() {
		setDelete();
		FrameEngine.putTextbox(new Textbox("You picked up " + getIndefinite(name) + " " + name + "!"));
		FrameEngine.getInventory().addItem(id);
	}
	
	@Override
	public void dispose(){
		image.getTexture().dispose();
	}

}
