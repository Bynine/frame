package com.frame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class KeyboardInputHandler implements InputHandler, InputProcessor {
	
	@Override
	public float getXInput() {
		return getInput(Gdx.input.isKeyPressed(Keys.S), Gdx.input.isKeyPressed(Keys.W));
	}

	@Override
	public float getYInput() {
		return getInput(Gdx.input.isKeyPressed(Keys.D), Gdx.input.isKeyPressed(Keys.A));
	}
	
	private float getInput(boolean input_A, boolean input_B){
		if (input_A) return 1;
		else if (input_B) return -1;
		else return 0;
	}

	@Override
	public boolean debug_speed_up_held() {
		return Gdx.input.isKeyPressed(Keys.SPACE);
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
