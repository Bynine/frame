package action;

import encounter.Monster;
import encounter.Tech;
import main.FrameEngine;

public class Heal extends Action {
	
	public Heal(Tech tech, Monster target){
		int heal = (int)(
				(tech.user.getCurrStats()[Monster.EMP] / 10.0) +
				((target.getRealStats()[Monster.VIT] * (tech.pow/12.0)))
				);
		target.getStatus().heal(heal);
		FrameEngine.getCurrentBattle().add_textbox
		(tech.user.getNickname() + " restores " + heal + " of " + target.getNickname() + "'s health.");
	}
}
