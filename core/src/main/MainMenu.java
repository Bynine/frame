package main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import text.MenuOption;

public class MainMenu extends AbstractMenu {

	private ArrayList<MenuOption> options = new ArrayList<>();
	private final boolean exists;

	MainMenu(boolean exists){
		cursor = exists ? 1 : 0;
		options.add(new MenuOption(6, 2, "New Journey", Option.NEW));
		options.add(new MenuOption(6, 2, "Continue", Option.CONTINUE));
		options.add(new MenuOption(6, 2, "Mailbag", Option.MAIL));
		this.exists = exists;
	}

	@Override
	public List<MenuOption> getList() {
		return options;
	}

	@Override
	protected void selectItem() {
		AudioHandler.playSoundVariedPitch(select);
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
			if (exists){
				FrameEngine.continueGame();
			}
			else{
				AudioHandler.playSoundVariedPitch(error);
			}
		} break;
		case MAIL:{
			if (exists){
				FrameEngine.checkMail();
			}
			else{
				AudioHandler.playSoundVariedPitch(error);
			}
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
		cursor = MathUtils.clamp(cursor + i, 0, options.size()-1);
	}

	public Vector2 getButtonPosition(int pos) {
		int x = FrameEngine.TILE * 6 * pos;
		int y = FrameEngine.TILE;
		return new Vector2(x, y);
	}

	public static enum Option{
		NEW, CONTINUE, MAIL
	}

	public void open(){
		cursor = 1;
	}

}
