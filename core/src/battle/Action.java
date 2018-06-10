package battle;

import java.util.logging.Level;

import main.FrameEngine;

public abstract class Action {
	
	/**
	 * Based on the string, loads the correct action, which executes itself given the target and tech.
	 */
	static Action load_action(String action, Monster target, Tech tech){
		switch(action){
		case "HIT":
			return new Action_Hit(tech, target);
		case "HEAL":
			return new Action_Heal(tech, target);
		case "DIE":
			return new Action_Die(tech);
		case "RECOIL":
			return new Action_Recoil(tech);
		case "GUARD":
			return new Action_Guard(tech, target);
		case "LOWERPOW":
			return new Action_LowerStat(tech, target, Monster.POW);
		case "LOWERDEF":
			return new Action_LowerStat(tech, target, Monster.DEF);
		case "RAISEPOW":
			return new Action_BoostStat(tech, target, Monster.POW);
		case "RAISEDEF":
			return new Action_BoostStat(tech, target, Monster.DEF);
		}
		FrameEngine.logger.log(Level.WARNING, "Unsure what action " + action + " is.");
		return null;
	}
	
}
