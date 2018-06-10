package battle;

import main.FrameEngine;

public class Action_Guard extends Action{

	public Action_Guard(Tech tech, Monster target){
		FrameEngine.getCurrentBattle().add_guarded_target(tech.user, target);
		FrameEngine.getCurrentBattle().add_textbox(tech.user.nickname + " guards " + target.nickname + "!");
	}
	
}
