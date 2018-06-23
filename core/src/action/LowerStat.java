package action;

import encounter.Monster;
import encounter.Tech;
import main.FrameEngine;

public class LowerStat extends Action {

	public LowerStat(Tech tech, Monster target, int stat_pos){
		int change = (int) -(target.getRealStats()[stat_pos] * 0.35);
		target.getStatus().change_stat(change, stat_pos);
		FrameEngine.getCurrentBattle().add_textbox(
				tech.user.getNickname() + " drains " + target.getNickname() + 
				" with " + tech.name + " by " + change);
	}
	
}
