package encounter;

import java.util.ArrayList;
import java.util.Arrays;

public class Battle {

	ArrayList<Monster> heroes = new ArrayList<Monster>(
			Arrays.asList(new Monster("DUMPLING", 10))
			);
	ArrayList<Monster> enemies = new ArrayList<Monster>(
			Arrays.asList(new Monster("DUMPLING", 10))
			);
	ArrayList<Monster> all = new ArrayList<Monster>();
	private boolean ended = false;

	/**
	 * Start a new battle, given some monsters to function as enemies.
	 */
	public Battle(Monster... enemy){
		for (Monster mon: enemy){
			this.enemies.add(mon);
		}
		all.addAll(heroes);
		all.addAll(enemies);
		for (Monster mon: all){
			mon.initialize_for_battle();
		}
	}

	/**
	 * The core loop.
	 */
	public void update(){
		if (ended){
			// TODO: Battle end loop
		}
		else{
			battle_loop();
		}
	}

	/**
	 * Handles the battle while it's ongoing.
	 */
	private void battle_loop(){
		ArrayList<Tech> techs = new ArrayList<Tech>();
		for (Monster enemy: enemies){
			techs.add(new Tech(enemy, choose_command(enemy)));
		}
		for (Monster hero: heroes){ // TODO: wait on user commands
			techs.add(new Tech(hero, choose_command(hero)));
		}
		techs.sort(new Tech.TechComparator());
		for (Tech tech: techs){
			if (!ended){
				print_battle_state();
				execute_tech(tech);
				ended = check_end();
			}
		}
	}

	/**
	 * Performs all the actions contained in the tech.
	 */
	private void execute_tech(Tech tech){
		ArrayList<Monster> targets = get_targets(tech);
		for (Monster target: targets){
			for (String action: tech.actions){
				load_action(action, target, tech);
			}
		}
	}

	/**
	 * Based on tech's Target parameter, get the list of targets the tech affects.
	 */
	private ArrayList<Monster> get_targets(Tech tech){
		ArrayList<Monster> targets = new ArrayList<Monster>();
		ArrayList<Monster> opposing_team = new ArrayList<Monster>();
		if (heroes.contains(tech.user)){
			opposing_team.addAll(enemies);
		}
		else if (enemies.contains(tech.user)){
			opposing_team.addAll(heroes);
		}
		switch(tech.target){
		case SINGLE: { // TODO: specific target picking
			targets.add(opposing_team.get(0));
		} break;
		case ALL: {
			targets.addAll(all);
			targets.remove(tech.user);
		}
		case ENEMY: {
			targets.addAll(opposing_team);
		} break;
		case TEAM: {
			targets.addAll(all);
			targets.removeAll(opposing_team);
		} break;
		case SELF: {
			targets.add(tech.user);
		} break;
		case PARTNER: { // TODO: specific target picking
			targets.addAll(all);
			targets.removeAll(opposing_team);
			targets.remove(tech.user);
		} break;
		default: break; // NONE
		}
		return targets;
	}

	/**
	 * Based on the string, loads the correct action, which executes itself given the target and tech.
	 */
	private Action load_action(String action, Monster target, Tech tech){
		switch(action){
		case "HIT":
			return new Action_Hit(tech, target);
		case "HEAL":
			return new Action_Heal(tech, target);
		case "EXPLODE":
			return new Action_Explode(tech);
		}
		return null;
	}

	/**
	 * Choose enemy commands based on battle state.
	 */
	private String choose_command(Monster enemy){
		// TODO: implement ai choosing
		return enemy.techs.get(0);
	}

	/**
	 * Checks whether the battle has ended.
	 */
	private boolean check_end(){
		if (has_lost(heroes)){
			System.out.println("Heroes have died");
			return true;
		}
		else if (has_lost(enemies)){
			System.out.println("Enemies have died");
			return true;
		}
		return false;
	}

	/**
	 * Checks if all members of team have lost.
	 */
	private boolean has_lost(ArrayList<Monster> team){
		for(Monster mon: team){
			if (mon.curr_stats[Monster.VIT] > 0) return false;
		}
		return true;
	}

	/**
	 * For debugging purposes.
	 */
	private void print_battle_state(){
		for(Monster mon: all){
			System.out.println(mon.toString());
		}
	}

}
