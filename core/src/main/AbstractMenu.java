package main;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract class representing any menu with a list of options to select from.
 */
public abstract class AbstractMenu {

	protected int cursor = 0;
	protected Timer cursorHalt = new Timer(10);
	protected Vector2 inputBuffer = new Vector2();
	
	public abstract List<? extends Object> getList();
	protected abstract void selectItem();
	private static final Sound moveCursor = Gdx.audio.newSound(Gdx.files.internal("sfx/speech/blip.wav"));

	public void update(){
		cursorHalt.countUp();
		handleInputs();
	}

	protected void handleInputs(){
		if (cursorHalt.timeUp()){
			if (!inputBuffer.isZero()){
				moveCursor((int)inputBuffer.x, (int)inputBuffer.y);
				finishMoved();
			}
			else if ((int)FrameEngine.getInputHandler().getXInput() != 0
					|| (int)FrameEngine.getInputHandler().getYInput() != 0){
				moveCursor(
						(int)FrameEngine.getInputHandler().getXInput(),
						(int)FrameEngine.getInputHandler().getYInput()
						);
				finishMoved();
			}
		}
		else{
			inputBuffer.set(
					FrameEngine.getInputHandler().getXInput(), 
					FrameEngine.getInputHandler().getYInput()
					);
		}
		
		if (FrameEngine.getInputHandler().getActionJustPressed()){
			selectItem();
		}
	}
	
	/**
	 * Logic after the cursor was successfully moved.
	 */
	private void finishMoved(){
		inputBuffer.setZero();
		cursorHalt.reset();
	}

	private void moveCursor(int x, int y){
		moveCursorVertical(y);
		moveCursorHorizontal(x);
	}

	protected void moveCursorVertical(int i){
		AudioHandler.playSound(moveCursor);
		cursor = MathUtils.clamp(cursor - i, 0, getList().size() - 1);
	}

	protected void moveCursorHorizontal(int i){
		AudioHandler.playSound(moveCursor);
	}

	public Object getSelectedItem(){
		return getList().get(cursor);
	}

}
