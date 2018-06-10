package battle;

import main.FrameEngine;

public class Action_BoostStat extends Action {

	public Action_BoostStat(Tech tech, Monster target, int stat_pos){
		int change = (int) (target.getRealStats()[stat_pos] * 0.4);	
		int mod = (int) (tech.pow * tech.user.getStatus().curr_stats[Monster.EMP] / (3.6 * tech.user.level));
		change += mod;
		target.getStatus().change_stat(change, stat_pos);
		FrameEngine.getCurrentBattle().add_textbox(tech.user.nickname + " boosts " + target.nickname + 
				" with " + tech.name + " by " + change);
	}

}
