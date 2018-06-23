package action;

import encounter.Tech;
import main.FrameEngine;

public class Die extends Action {

	public Die(Tech tech){
		if (!tech.user.getStatus().alive()) return;
		tech.user.getStatus().take_damage(Integer.MAX_VALUE);
		FrameEngine.getCurrentBattle().add_textbox(tech.user.getNickname() + " exploded!!");
	}
}
