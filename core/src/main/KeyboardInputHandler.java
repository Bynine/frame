package main;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import com.badlogic.gdx.InputProcessor;

public class KeyboardInputHandler implements InputHandler, InputProcessor {
	
	private static final int
	KEY_LEFT = Keys.A,
	KEY_RIGHT = Keys.D,
	KEY_UP = Keys.W,
	KEY_DOWN = Keys.S;
	private static final int
	KEY_LEFT2 = Keys.LEFT,
	KEY_RIGHT2 = Keys.RIGHT,
	KEY_UP2 = Keys.UP,
	KEY_DOWN2 = Keys.DOWN;
	
	private ArrayDeque<String> saveText = new ArrayDeque<String>();

	@Override
	public void initialize() {
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void update(){
		if (Gdx.input.isKeyJustPressed(Keys.F)) saveText.add("F");
		if (Gdx.input.isKeyJustPressed(Keys.R)) saveText.add("R");
		if (Gdx.input.isKeyJustPressed(Keys.O)) saveText.add("O");
		if (Gdx.input.isKeyJustPressed(Keys.S)) saveText.add("S");
		if (Gdx.input.isKeyJustPressed(Keys.T)) saveText.add("T");
		if (saveText.size() > 10) {
			saveText.pop();
		}
	}
	
	@Override
	public float getXInput() {
		return getInput(
				Gdx.input.isKeyPressed(KEY_RIGHT), 
				Gdx.input.isKeyPressed(KEY_RIGHT2), 
				Gdx.input.isKeyPressed(KEY_LEFT), 
				Gdx.input.isKeyPressed(KEY_LEFT2)
				);
	}

	@Override
	public float getYInput() {
		return getInput(
				Gdx.input.isKeyPressed(KEY_UP), 
				Gdx.input.isKeyPressed(KEY_UP2), 
				Gdx.input.isKeyPressed(KEY_DOWN), 
				Gdx.input.isKeyPressed(KEY_DOWN2)
				);
	}
	
	/**
	 * Helper for WASD input.
	 */
	private float getInput(boolean input_A, boolean input_A2, boolean input_B, boolean input_B2){
		if (input_A || input_A2) return 1;
		else if (input_B || input_B2) return -1;
		else return 0;
	}
	
	@Override
	public boolean getPauseJustPressed(){
		return Gdx.input.isKeyJustPressed(Keys.ENTER);
	}
	
	@Override
	public boolean getActionJustPressed(){
		return Gdx.input.isKeyJustPressed(Keys.SPACE);
	}
	
	@Override
	public boolean getLeftJustPressed() {
		return Gdx.input.isKeyJustPressed(KEY_LEFT) || Gdx.input.isKeyJustPressed(KEY_LEFT2);
	}

	@Override
	public boolean getRightJustPressed() {
		return Gdx.input.isKeyJustPressed(KEY_RIGHT) || Gdx.input.isKeyJustPressed(KEY_RIGHT2);
	}

	@Override
	public boolean getUpJustPressed() {
		return Gdx.input.isKeyJustPressed(KEY_UP) || Gdx.input.isKeyJustPressed(KEY_UP2);
	}

	@Override
	public boolean getDownJustPressed() {
		return Gdx.input.isKeyJustPressed(KEY_DOWN) || Gdx.input.isKeyJustPressed(KEY_DOWN2);
	}
	
	@Override
	public boolean getDebugJustPressed(){
		return Gdx.input.isKeyJustPressed(Keys.Q);
	}
	
	@Override
	public boolean getSaveJustPressed() {
		return Gdx.input.isKeyJustPressed(Keys.E);
	}
	
	@Override
	public boolean getSuperDebugSpeedUpHeld() {
		return Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
	}

	@Override
	public boolean getDebugSpeedUpHeld() {
		return Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);
	}

	// USELESS
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean getPlusPressed() {
		return Gdx.input.isKeyPressed(Keys.P);
	}

	@Override
	public boolean getMinusPressed() {
		return Gdx.input.isKeyPressed(Keys.I);
	}

	@Override
	public String getSaveText() {
		StringBuilder sb = new StringBuilder();
		for (String c: saveText) {
			sb.append(c);
		}
		return sb.toString();
	}

	@Override
	public void wipeSaveText() {
		saveText.clear();
	}

}
