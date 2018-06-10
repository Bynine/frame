package battle;

import main.FrameEngine;

public class Action_LowerStat extends Action {

	public Action_LowerStat(Tech tech, Monster target, int stat_pos){
		int change = (int) -(target.getRealStats()[stat_pos] * 0.35);
		target.getStatus().change_stat(change, stat_pos);
		FrameEngine.getCurrentBattle().add_textbox(tech.user.nickname + " drains " + target.nickname + 
				" with " + tech.name + " by " + change);
	}
	
}
