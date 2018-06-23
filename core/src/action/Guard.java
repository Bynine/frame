package action;

import encounter.Monster;
import encounter.Tech;
import main.FrameEngine;

public class Guard extends Action{

	public Guard(Tech tech, Monster target){
		FrameEngine.getCurrentBattle().add_guarded_target(tech.user, target);
		FrameEngine.getCurrentBattle().add_textbox(
				tech.user.getNickname() + " guards " + target.getNickname() + "!");
	}
	
}
