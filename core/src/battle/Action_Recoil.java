package battle;

import main.FrameEngine;

public class Action_Recoil extends Action {

	public Action_Recoil(Tech tech){
		int damage = (int)(0.05 * tech.pow * tech.user.getRealStats()[Monster.VIT]);
		tech.user.getStatus().take_damage(damage); 
		FrameEngine.getCurrentBattle().add_textbox
		(tech.user.nickname + " takes " + damage + " from its reckless attack!");
	}
	
}
