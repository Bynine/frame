package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class KeyboardMouseInputHandler implements InputHandler, InputProcessor {
	
	MousePress active_mouse_press = new MousePress(false, -1, -1, -1, -1);
	

	@Override
	public void initialize() {
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void update(){
		 if(Gdx.input.justTouched()){
			 active_mouse_press = new MousePress(true, Gdx.input.getX(), Gdx.input.getY(), 0, 0);
		 }
		 else{
			 active_mouse_press = new MousePress(false, -1, -1, -1, -1);
		 }
	}
	
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

	@Override
	public MousePress getMousePress() {
		return active_mouse_press;
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
