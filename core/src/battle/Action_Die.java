package battle;

import main.FrameEngine;

public class Action_Die extends Action {

	public Action_Die(Tech tech){
		if (!tech.user.getStatus().alive()) return;
		tech.user.getStatus().take_damage(Integer.MAX_VALUE);
		FrameEngine.getCurrentBattle().add_textbox(tech.user.nickname + " exploded!!");
	}
}
