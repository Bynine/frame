package action;

import encounter.Monster;
import encounter.Tech;
import main.FrameEngine;

public class Hit extends Action {

	public Hit(Tech tech, Monster target){
		int damage = (int)
				((tech.pow * tech.user.getStatus().getCurrStats()[Monster.POW])
						/ (2.0 + (target.getStatus().getCurrStats()[Monster.DEF] * 1.6))
						* ((tech.user.getLevel() + 19.0) / 25.0)) + 1
				;
		target.getStatus().take_damage(damage);
		FrameEngine.getCurrentBattle().add_textbox
		(tech.user.getNickname() + " does " + damage + " damage to " + target.getNickname());
	}
}
