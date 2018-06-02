package com.frame;

public interface InputHandler {

	abstract float getXInput();
	/*{ 
		float xInput = getInput(1, Gdx.input.isKeyPressed(Keys.D), Gdx.input.isKeyPressed(Keys.A));
		if (Math.abs(xInput) < deadZone) return 0;
		else return xInput; 
	}*/

	abstract float getYInput();
	/*{ 
		float yInput = -getInput(0, Gdx.input.isKeyPressed(Keys.S), Gdx.input.isKeyPressed(Keys.W)); 
		if (Math.abs(yInput) < deadZone) return 0;
		else return yInput; 
	}*/

	//abstract float getInput(int axis, boolean inputA, boolean inputB);
	/*{
		if (ctlType == CtlType.CONTROLLER) return controller.getAxis(axis);
		if (inputA) return 1;
		else if (inputB) return -1;
		else return 0;
	}*/
	
	/**
	 * If debug mode is on, increases game's update rate.
	 */
	abstract boolean debug_speed_up_held();
	/*{
		if (ctlType == CtlType.CONTROLLER) return controller.getButton(5);
		else return Gdx.input.isKeyPressed(Keys.SPACE);
	}*/

}
