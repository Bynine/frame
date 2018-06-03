package overworld;

import main.FrameEngine;

public class Player extends Entity{

	@Override
	public void update(){
		super.update();
		velocity.set(FrameEngine.get_input_handler().getXInput(), FrameEngine.get_input_handler().getYInput());
	}
}
