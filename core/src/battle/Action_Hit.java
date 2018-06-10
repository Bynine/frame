package battle;

import main.FrameEngine;

public class Action_Hit extends Action {

	public Action_Hit(Tech tech, Monster target){
		int damage = (int)
				((tech.pow * tech.user.getStatus().curr_stats[Monster.POW])
						/ (2.0 + (target.getStatus().curr_stats[Monster.DEF] * 1.6))
						* ((tech.user.level + 19.0) / 25.0)) + 1
				;
		target.getStatus().take_damage(damage);
		FrameEngine.getCurrentBattle().add_textbox
		(tech.user.nickname + " does " + damage + " damage to " + target.nickname);
	}
}
