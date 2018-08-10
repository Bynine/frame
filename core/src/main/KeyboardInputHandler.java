package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import text.Pointer;

import com.badlogic.gdx.InputProcessor;

public class KeyboardInputHandler implements InputHandler, InputProcessor {
	
	Pointer active_pointer = new Pointer(false, -1, -1);
	private static final int
	KEY_LEFT = Keys.A,
	KEY_RIGHT = Keys.D,
	KEY_UP = Keys.W,
	KEY_DOWN = Keys.S;

	@Override
	public void initialize() {
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void update(){
		active_pointer = new Pointer(Gdx.input.justTouched(), Gdx.input.getX(), Gdx.input.getY());
	}
	
	@Override
	public float getXInput() {
		return getInput(Gdx.input.isKeyPressed(KEY_RIGHT), Gdx.input.isKeyPressed(KEY_LEFT));
	}

	@Override
	public float getYInput() {
		return getInput(Gdx.input.isKeyPressed(KEY_UP), Gdx.input.isKeyPressed(KEY_DOWN));
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
		return Gdx.input.isKeyJustPressed(KEY_LEFT);
	}

	@Override
	public boolean getRightJustPressed() {
		return Gdx.input.isKeyJustPressed(KEY_RIGHT);
	}

	@Override
	public boolean getUpJustPressed() {
		return Gdx.input.isKeyJustPressed(KEY_UP);
	}

	@Override
	public boolean getDownJustPressed() {
		return Gdx.input.isKeyJustPressed(KEY_DOWN);
	}
	
	@Override
	public boolean getDebugJustPressed(){
		return Gdx.input.isKeyJustPressed(Keys.Q);
	}
	
	/**
	 * Helper for WASD input.
	 */
	private float getInput(boolean input_A, boolean input_B){
		if (input_A) return 1;
		else if (input_B) return -1;
		else return 0;
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

}
