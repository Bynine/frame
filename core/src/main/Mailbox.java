package main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import text.MenuOption;

public class Mailbox extends AbstractMenu {
	
	protected final ArrayList<String> letters = new ArrayList<String>();
	protected final ArrayList<MenuOption> descs = new ArrayList<MenuOption>();
	private String currLetter;
	public static final String close = "CLOSE";
	
	Mailbox(){
		letters.add("Letter1");
		letters.add("Letter2");
		descs.add(new MenuOption(6, 2, "Close Mailbag", close));
		for (String letter: letters) {
			descs.add(new MenuOption(6, 2, letter, letter));
		}
	}

	@Override
	protected void selectItem() {
		String output = getActiveButton().getOutput().toString();
		if (output.equals(close)) {
			currLetter = null;
			FrameEngine.startMainMenu(true);
		}
	}
	
	public Vector2 getButtonPosition(int pos) {
		Vector2 position = super.getButtonPosition(pos);
		position.x -= FrameEngine.TILE * 6;
		return position;
	}
	
	@Override
	public List<MenuOption> getList() {
		return descs;
	}
	
	@Override
	public void open(){
		super.open();
	}
	
	public String getLetter() {
		return currLetter;
	}

}
