package main;

import java.util.ArrayList;
import java.util.List;

import text.Button;

public class MainMenu extends AbstractMenu {
	
	private ArrayList<Button> options = new ArrayList<>();
	
	MainMenu(){
		cursor = 1;
		options.add(new Button(8, 2, "New Adventure", Option.NEW));
		options.add(new Button(8, 2, "Wake Up", Option.CONTINUE));
	}
	
	@Override
	public List<Button> getList() {
		return options;
	}

	@Override
	protected void selectItem() {
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

}
