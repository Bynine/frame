package main;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import text.Button;
import timer.Timer;

/**
 * Abstract class representing any menu with a list of options to select from.
 */
public abstract class AbstractMenu {

	protected int cursor = 0;
	protected Timer cursorHalt = new Timer(10);
	protected Vector2 inputBuffer = new Vector2();
	
	public abstract List<Button> getList();
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
		playCursorSound();
		cursor = MathUtils.clamp(cursor - i, 0, getList().size() - 1);
	}

	protected void moveCursorHorizontal(int i){
		playCursorSound();
	}
	
	protected final void playCursorSound(){
		AudioHandler.playSound(moveCursor);
	}

	public Button getActiveButton(){
		if (getList().size() == 0) return null;
		return getList().get(cursor);
	}
	
	public Vector2 getButtonPosition(int pos) {
		int onScreen = 5;
		int posX = (int) pos/onScreen;
		int posY = 2 + (pos % onScreen);
		Button button = getList().get(pos);
		Vector2 position = new Vector2(
				(Gdx.graphics.getWidth()/(2/GraphicsHandler.ZOOM) + 
						FrameEngine.TILE * (button.getDimensions().x) * (posX - 0.5f)),
				(Gdx.graphics.getHeight()/2) - (button.getDimensions().y * FrameEngine.TILE * posY)
				);
		return position;
	}
	
	public final int getCursor(){
		return cursor;
	}
	
	public void open(){
		cursor = 0;
	}

}
