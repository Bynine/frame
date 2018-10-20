package main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import text.MenuOption;

public class MainMenu extends AbstractMenu {

	private ArrayList<MenuOption> options = new ArrayList<>();
	private final boolean exists;

	MainMenu(boolean exists){
		cursor = exists ? 1 : 0;
		options.add(new MenuOption(8, 2, "New Adventure", Option.NEW));
		options.add(new MenuOption(8, 2, "Wake Up", Option.CONTINUE));
		this.exists = exists;
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
			if (exists){
				FrameEngine.startSaveConfirmationMenu();
			}
			else{
				FrameEngine.newGame();
			}
		} break;
		case CONTINUE:{
			FrameEngine.continueGame();
		} break;
		}
	}

	public Vector2 getButtonPosition(int pos) {
		Vector2 position = super.getButtonPosition(pos);
		position.sub(0, FrameEngine.TILE * 5.5f);
		return position;
	}

	public static enum Option{
		NEW, CONTINUE
	}

	public void open(){
		cursor = 1;
	}

}
