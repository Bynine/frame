package action;

import encounter.Monster;
import encounter.Tech;
import main.FrameEngine;

public class BoostStat extends Action {

	public BoostStat(Tech tech, Monster target, int stat_pos){
		int change = (int) (target.getRealStats()[stat_pos] * 0.4);	
		int mod = (int) (tech.pow * tech.user.getStatus().getCurrStats()[Monster.EMP] / (3.6 * tech.user.getLevel()));
		change += mod;
		target.getStatus().change_stat(change, stat_pos);
		FrameEngine.getCurrentBattle().add_textbox(tech.user.getNickname() + " boosts " + target.getNickname() + 
				" with " + tech.name + " by " + change);
	}

}
