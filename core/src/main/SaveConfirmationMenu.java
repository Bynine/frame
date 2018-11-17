package main;

import java.util.ArrayList;
import java.util.List;

import text.MenuOption;

public class SaveConfirmationMenu extends AbstractMenu {
	
	private ArrayList<MenuOption> options = new ArrayList<>();

	SaveConfirmationMenu(){
		options.add(new MenuOption(8, 2, "Go Back", Option.RETURN));
		options.add(new MenuOption(8, 2, "Delete Save File", Option.WIPE));
	}
	
	@Override
	public List<MenuOption> getList() {
		return options;
	}

	@Override
	protected void selectItem() {
		AudioHandler.playSoundVariedPitch(select);
		switch ((Option)getList().get(cursor).getOutput()){
		case RETURN:{
			FrameEngine.startMainMenu(true);
		} break;
		case WIPE:{
			FrameEngine.newGame();
		} break;
		}
	}
	
	public static enum Option{
		RETURN, WIPE
	}

}
