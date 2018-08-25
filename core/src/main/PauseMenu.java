package main;

import java.util.ArrayList;
import java.util.List;

import text.Button;

public class PauseMenu extends AbstractMenu{
	
	private ArrayList<Button> options = new ArrayList<>();
	
	PauseMenu(){
		options.add(new Button(2, 2, "Inventory", Option.INVENTORY));
		options.add(new Button(2, 2, "Exit", Option.EXIT));
	}

	@Override
	public List<Button> getList() {
		return options;
	}

	@Override
	protected void selectItem() {
		switch ((Option)getList().get(cursor).getOutput()){
		case INVENTORY:{
			FrameEngine.newGame();
		} break;
		case EXIT:{
			FrameEngine.continueGame();
		} break;
		}
	}

	public static enum Option{
		INVENTORY, EXIT
	}
	
}
