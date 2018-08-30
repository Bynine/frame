package entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.FrameEngine;
import text.Textbox;

public class Currency extends InteractableEntity{
	
	private final String flag;
	protected final int amount;

	public Currency(float x, float y,  int amount, String flag) {
		super(x, y, "");
		this.flag = flag;
		this.amount = amount;
		this.image = new TextureRegion(new Texture("sprites/items/acorn.png"));
	}
	
	@Override
	public void interact() {
		setDelete();
		String currency = "acorn";
		if (amount > 1) currency = currency.concat("s");
		FrameEngine.putTextbox(new Textbox(getString(currency)));
		FrameEngine.getSaveFile().addMoney(amount);
		FrameEngine.getSaveFile().setFlag(flag, true);
	}
	
	protected String getString(String currency){
		return "You obtained " + amount + " " + currency + "!";
	}

	@Override
	public void dispose() {
		image.getTexture().dispose();
	}

}
