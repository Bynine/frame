package action;

import encounter.Monster;
import encounter.Tech;
import main.FrameEngine;

public class Recoil extends Action {

	public Recoil(Tech tech){
		int damage = (int)(0.05 * tech.pow * tech.user.getRealStats()[Monster.VIT]);
		tech.user.getStatus().take_damage(damage); 
		FrameEngine.getCurrentBattle().add_textbox
		(tech.user.getNickname() + " takes " + damage + " from its reckless attack!");
	}
	
}
