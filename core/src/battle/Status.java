package battle;

import java.util.ArrayList;

import main.FrameEngine;

/**
 * Represents a monster's status in battle.
 */
public class Status {
	
	private final Monster mon;
	ArrayList<Tech> techs = new ArrayList<Tech>();
	int[] curr_stats = new int[Monster.NUM_STATS]; // Temporary stats that can change in a battle.
	boolean commanded = false;

	Status(Monster mon){
		this.mon = mon;
		for (int ii = 0; ii < Monster.NUM_STATS; ++ii){
			curr_stats[ii] = mon.getRealStats()[ii];
		}
		for (String id: mon.getTechIDs()){
			techs.add(new Tech(mon, id));
		}
	}

	/**
	 * Whether this monster can currently act.
	 */
	public boolean can_act(){
		return alive();
	}

	/**
	 * Whether this monster can currently be targeted.
	 */
	public boolean can_target(){
		return alive();
	}

	/**
	 * Whether or not the monster is still in the battle.
	 */
	public boolean alive(){
		return curr_stats[Monster.VIT] > 0;
	}

	public void take_damage(int damage) {
		if (alive() && curr_stats[Monster.VIT] - damage <= 0){
			FrameEngine.getCurrentBattle().add_textbox(mon.nickname + " has been knocked out!");
		}
		curr_stats[Monster.VIT] -= damage;
		if (curr_stats[Monster.VIT] <= 0){
			curr_stats[Monster.VIT] = 0;
		}
	}

	public void heal(int heal) {
		curr_stats[Monster.VIT] += heal;
		if (curr_stats[Monster.VIT] > mon.getRealStats()[Monster.VIT]){ // no overheal
			curr_stats[Monster.VIT] = mon.getRealStats()[Monster.VIT];
		}
	}
	
	public void change_stat(int change, int stat_pos) {
		curr_stats[stat_pos] += change;
		if (curr_stats[stat_pos] < 1) curr_stats[stat_pos] = 1;
	}

	public int[] getCurrStats() {
		return curr_stats;
	}

	public ArrayList<Tech> getTechs() {
		return techs;
	}
	
}
