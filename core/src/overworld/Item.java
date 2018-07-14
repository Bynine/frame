package overworld;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.TSVReader;
import main.FrameEngine;
import main.Textbox;

public class Item extends InteractableEntity {
	
	private static final String vowels = "aeiou";
	
	private final TextureRegion texture;
	private final String name;

	public Item(float x, float y, String id) {
		super(x, y, id);
		hitbox.setSize(24, 16);
		String[] data = new TSVReader().loadDataByID(id, TSVReader.ITEM_URL);
		texture = new TextureRegion(new Texture("sprites/overworld/items/" + data[0].toLowerCase() + ".png"));
		name = data[1];
	}
	
	@Override
	public void interact() {
		setDelete();
		String indefinite = "a";
		if (vowels.indexOf(Character.toLowerCase(text.charAt(0))) != -1){
			indefinite = "an";
		}
		FrameEngine.setTextbox(new Textbox("Wow, you picked up " + indefinite + " " + name + "!"));
	}

	@Override
	public TextureRegion getImage(){
		return texture;
	}

	@Override
	public void dispose(){
		texture.getTexture().dispose();
	}

}
