package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.TSVReader;
import text.Textbox;
import main.FrameEngine;

public class Item extends InteractableEntity {
	
	protected final String name, id, flag;

	public Item(float x, float y, String id, String flag) {
		super(x, y, id);
		this.id = id;
		this.flag = flag;
		hitbox.setSize(24, 16);
		String[] data = new TSVReader().loadDataByID(id, TSVReader.ITEM_URL);
		image = new TextureRegion(new Texture("sprites/items/" + data[0].toLowerCase() + ".png"));
		name = data[1];
	}
	
	@Override
	public void interact() {
		setRemove();
		FrameEngine.putTextbox(new Textbox("You obtained " + getIndefinite(name) + " " + name + "!"));
		FrameEngine.getInventory().addItem(id);
		FrameEngine.getSaveFile().setFlag(flag, true);
	}
	
	@Override
	public void dispose(){
		image.getTexture().dispose();
	}

}
