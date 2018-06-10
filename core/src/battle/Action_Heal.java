package battle;

import main.FrameEngine;

public class Action_Heal extends Action {
	
	public Action_Heal(Tech tech, Monster target){
		int heal = (int)(
				(tech.user.getCurrStats()[Monster.EMP] / 10.0) +
				((target.getRealStats()[Monster.VIT] * (tech.pow/12.0)))
				);
		target.getStatus().heal(heal);
		FrameEngine.getCurrentBattle().add_textbox
		(tech.user.nickname + " restores " + heal + " health of " + target.nickname);
	}
}
