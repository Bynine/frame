package main;

public interface InputHandler {
	
	abstract void initialize();
	
	abstract void update();

	abstract float getXInput();

	abstract float getYInput();
	
	abstract boolean getPauseJustPressed();
	
	abstract Pointer getPointer();
	
	/**
	 * If debug mode is on, increases game's update rate.
	 */
	abstract boolean debug_speed_up_held();

}
