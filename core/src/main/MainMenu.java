package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainMenu extends AbstractMenu {
	
	MainMenu(){
		cursor = 1;
	}
	
	@Override
	public List<? extends Object> getList() {
		return new ArrayList<>(Arrays.asList(Option.values()));
	}

	@Override
	protected void selectItem() {
		switch ((Option)getList().get(cursor)){
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
