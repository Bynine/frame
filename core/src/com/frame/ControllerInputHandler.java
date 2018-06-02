package com.frame;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class ControllerInputHandler implements InputHandler, ControllerListener {
	
	private static Controller controller;
	private static final float dead_zone = 0.15f;

	boolean setupController(){
		int numControllers = Controllers.getControllers().size;
		if (numControllers < 1) return false;
		Controllers.addListener(this);
		controller = Controllers.getControllers().first();
		return true;
	}
	
	@Override
	public float getXInput() {
		return getInput(1);
	}

	@Override
	public float getYInput() {
		return -getInput(0);
	}
	
	private float getInput(int axis){
		float input = controller.getAxis(axis);
		if (Math.abs(input) < dead_zone) return 0;
		else return input;
	}

	@Override
	public boolean debug_speed_up_held() {
		return controller.getButton(5);
	}
	
	// USELESS

	@Override
	public void connected(Controller controller) {
	
	}

	@Override
	public void disconnected(Controller controller) {
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}
	
	
	
}
