package action;

import java.util.logging.Level;

import encounter.Monster;
import encounter.Tech;
import main.FrameEngine;

public abstract class Action {
	
	/**
	 * Based on the string, loads the correct action, which executes itself given the target and tech.
	 */
	public static Action load_action(String action, Monster target, Tech tech){
		switch(action){
		case "HIT":
			return new Hit(tech, target);
		case "HEAL":
			return new Heal(tech, target);
		case "DIE":
			return new Die(tech);
		case "RECOIL":
			return new Recoil(tech);
		case "GUARD":
			return new Guard(tech, target);
		case "LOWERPOW":
			return new LowerStat(tech, target, Monster.POW);
		case "LOWERDEF":
			return new LowerStat(tech, target, Monster.DEF);
		case "RAISEPOW":
			return new BoostStat(tech, target, Monster.POW);
		case "RAISEDEF":
			return new BoostStat(tech, target, Monster.DEF);
		}
		FrameEngine.logger.log(Level.WARNING, "Unsure what action " + action + " is.");
		return null;
	}
	
}
