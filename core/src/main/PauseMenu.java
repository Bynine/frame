package main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import text.MenuOption;

public class PauseMenu extends AbstractMenu{
	
	private ArrayList<MenuOption> options = new ArrayList<>();
	
	PauseMenu(){
		options.add(new MenuOption(6, 2, "Inventory", Option.INVENTORY));
		options.add(new MenuOption(6, 2, "Save & Rest", Option.EXIT));
	}

	@Override
	public List<MenuOption> getList() {
		return options;
	}

	@Override
	protected void selectItem() {
		AudioHandler.playSound(select);
		switch ((Option)getList().get(cursor).getOutput()){
		case INVENTORY:{
			FrameEngine.startInventory();
		} break;
		case EXIT:{
			FrameEngine.getSaveFile().save();
			FrameEngine.startMainMenu();
		} break;
		}
	}
	
	@Override
	protected void moveCursorVertical(int i){
		//
	}

	@Override
	protected void moveCursorHorizontal(int i){
		playCursorSound(i);
		cursor = MathUtils.clamp(cursor + i, 0, 1);
	}

	public static enum Option{
		INVENTORY, EXIT
	}
	
	public Vector2 getButtonPosition(int pos) {
		int x = pos == 0 ? FrameEngine.TILE * 3 : FrameEngine.TILE * 9;
		int y = Gdx.graphics.getHeight()/2 - 2 * FrameEngine.TILE;
		return new Vector2(x, y);
	}
	
}
