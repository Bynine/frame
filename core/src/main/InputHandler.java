package main;

public interface InputHandler {
	
	abstract void initialize();
	
	abstract void update();

	abstract float getXInput();

	abstract float getYInput();
	
	abstract boolean getDebugJustPressed();
	
	abstract boolean getPauseJustPressed();
	
	abstract boolean getActionJustPressed();
	
	abstract boolean getLeftJustPressed();

	abstract boolean getRightJustPressed();

	abstract boolean getUpJustPressed();

	abstract boolean getDownJustPressed();
	
	abstract Pointer getPointer();
	
	/**
	 * If debug mode is on, increases game's update rate.
	 */
	abstract boolean debug_speed_up_held();

}
