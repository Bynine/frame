package battle;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;

public class Battle_Final extends Battle {

	public Battle_Final(ArrayList<Monster> enemies) {
		super(enemies);
	}
	
	@Override
	protected void victory(){
		FrameEngine.victory();
	}
	
	@Override
	protected void failure(){
		FrameEngine.failure();
	}
	
	@Override
	protected void tame(Rectangle zone){
		add_textbox("But they don't want to join you...");
		state = State.DECIDE;
	}

}
