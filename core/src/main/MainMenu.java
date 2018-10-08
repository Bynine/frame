package main;

import java.util.ArrayList;
import java.util.List;

import text.MenuOption;

public class MainMenu extends AbstractMenu {
	
	private ArrayList<MenuOption> options = new ArrayList<>();
	
	MainMenu(boolean exists){
		cursor = exists ? 1 : 0;
		options.add(new MenuOption(8, 2, "New Adventure", Option.NEW));
		options.add(new MenuOption(8, 2, "Wake Up", Option.CONTINUE));
	}
	
	@Override
	public List<MenuOption> getList() {
		return options;
	}

	@Override
	protected void selectItem() {
		AudioHandler.playSound(select);
		switch ((Option)getList().get(cursor).getOutput()){
		case NEW:{
			FrameEngine.newGame();
		} break;
		case CONTINUE:{
			FrameEngine.continueGame();
		} break;
		}
	}

	public static enum Option{
		NEW, CONTINUE
	}
	
	public void open(){
		cursor = 1;
	}

}
