package main;

import java.util.HashSet;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class ControllerInputHandler implements InputHandler, ControllerListener{

	private Controller controller;
	private final HashSet<Integer> heldButtons = new HashSet<>();

	@Override
	public void initialize() {
		for (Controller control: Controllers.getControllers()) {
			if (isValidController(control)){
				controller = control;
			}
		}
		Controllers.addListener(this);
	}

	/**
	 * Only allows XBox controllers for now.
	 */
	private boolean isValidController(Controller control){
		return (control.getName().toLowerCase().contains("xbox") 
				&& control.getName().contains("360"));
	}

	@Override
	public void update() {

	}

	@Override
	public float getXInput() {
		return controller.getAxis(0);
	}

	@Override
	public float getYInput() {
		return controller.getAxis(1);
	}

	/**
	 * Checks the requested button to see if it's already been pressed without being released.
	 */
	private boolean getButtonJustPressed(int code){
		if (controller.getButton(code) && !heldButtons.contains(code)){
			heldButtons.add(code);
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public boolean getDebugJustPressed() {
		return getButtonJustPressed(6);
	}

	@Override
	public boolean getPauseJustPressed() {
		return getButtonJustPressed(5);
	}

	@Override
	public boolean getActionJustPressed() {
		return getButtonJustPressed(0);
	}

	@Override
	public boolean getLeftJustPressed() {
		return false;
	}

	@Override
	public boolean getRightJustPressed() {
		return false;
	}

	@Override
	public boolean getUpJustPressed() {
		return false;
	}

	@Override
	public boolean getDownJustPressed() {
		return false;
	}

	@Override
	public boolean getDebugSpeedUpHeld() {
		return controller.getButton(7);
	}
	
	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		heldButtons.remove(buttonCode);
		return false;
	}

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

	@Override
	public boolean getSaveJustPressed() {
		return false;
	}

}
