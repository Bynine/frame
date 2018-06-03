package encounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;
import main.MousePress;

public class Battle {

	private final ArrayList<Monster> heroes = new ArrayList<Monster>(Arrays.asList(
			new Monster("DUMPLING", 20)
			));
	private final ArrayList<Monster> enemies = new ArrayList<Monster>(Arrays.asList(
			new Monster("DUMPLING", 10),
			new Monster("OFF", 10)
			));
	private final ArrayList<Monster> all = new ArrayList<Monster>();
	private final ArrayList<Tech> techs = new ArrayList<Tech>();
	private final HashMap<Tech, Monster> specific_targets = new HashMap<Tech, Monster>();
	private final HashMap<Rectangle, Monster> zone_to_monster = new HashMap<Rectangle, Monster>();

	private boolean battle_ended = false;
	private boolean commands_issued = false;
	private boolean turn_started = false;
	private Tech tech_waiting_for_target = null;

	private static final int hero_dim = 240;
	private static final int hero_height = 0;
	public static final ArrayList<Rectangle> HERO_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(Gdx.graphics.getWidth()/5, hero_height, hero_dim, hero_dim),
			new Rectangle(2*Gdx.graphics.getWidth()/5, hero_height, hero_dim, hero_dim),
			new Rectangle(3*Gdx.graphics.getWidth()/5, hero_height, hero_dim, hero_dim)
			));
	private static final int enemy_dim = 240;
	private static final int enemy_height = Gdx.graphics.getHeight()/2;
	public static final ArrayList<Rectangle> ENEMY_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(Gdx.graphics.getWidth()/5, enemy_height, enemy_dim, enemy_dim),
			new Rectangle(2*Gdx.graphics.getWidth()/5, enemy_height, enemy_dim, enemy_dim),
			new Rectangle(3*Gdx.graphics.getWidth()/5, enemy_height, enemy_dim, enemy_dim)
			));


	/**
	 * Start a new battle, given some monsters to function as enemies.
	 */
	public Battle(Monster... enemy){
		heroes.get(0).nickname = "Hero!"; // LATER: remove
		for (Monster mon: enemy){
			this.enemies.add(mon);
		}
		all.addAll(heroes);
		all.addAll(enemies);
		for (Monster mon: all){
			mon.initialize_for_battle();
		}
		for (int ii = 0; ii < heroes.size(); ++ii){
			zone_to_monster.put(HERO_ZONES.get(ii), heroes.get(ii));
		}
		for (int ii = 0; ii < enemies.size(); ++ii){
			zone_to_monster.put(ENEMY_ZONES.get(ii), enemies.get(ii));
		}
	}

	/**
	 * The core loop.
	 */
	public void update(){
		if (battle_ended){
			// TODO: Battle end loop
		}
		else if (!commands_issued){
			command_loop();
		}
		else{
			battle_execute();
		}
	}

	/**
	 * Handles the battle once all commands have been issued.
	 */
	private void battle_execute(){	
		techs.sort(new Tech.TechComparator());
		for (Tech tech: techs){
			if (!battle_ended && tech.user.alive()){
				print_battle_state();
				execute_tech(tech);
				battle_ended = check_end();
			}
		}
		commands_issued = false;
		turn_started = false;
		specific_targets.clear();
		for (Monster mon: heroes){
			mon.commanded = false;
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
		case SINGLE: {
			targets.add(specific_targets.get(tech));
		} break;
		case ALL: {
			targets.addAll(all);
			targets.remove(tech.user);
		} break;
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
		case PARTNERS: {
			targets.addAll(all);
			targets.removeAll(opposing_team);
			targets.remove(tech.user);
		} break;
		}
		return targets;
	}

	/**
	 * Loop that waits on the player to issue commands.
	 */
	private void command_loop(){
		commands_issued = true;
		for (Monster mon: heroes){
			if (!mon.commanded) commands_issued = false;
		}
		if (!turn_started){
			turn_start();
		}
		Rectangle selected_zone = evaluate_zone();
		if (selected_zone != null) {
			Monster curr_mon = null;
			for(Monster mon: heroes){ // choose the first active party member who hasn't been commanded yet
				if (curr_mon == null && mon.alive() && !mon.commanded) {
					curr_mon = mon;
				}
			}
			if(tech_waiting_for_target != null){
				System.out.println("Chose target for tech.");
				hero_target_choice(curr_mon, selected_zone);
			}
			else{
				System.out.println("Chose tech.");
				hero_tech_choice(curr_mon); // TODO: Allow tech selection. Defaults to first right now.
			}
		}
	}
	
	/**
	 * Checks if mouse was clicked and if the position it was clicked in corresponds to something valid
	 */
	private Rectangle evaluate_zone(){
		MousePress mouse_press = FrameEngine.get_input_handler().getMousePress();
		if (!mouse_press.active) {
			return null;
		}
		for (Rectangle zone: HERO_ZONES){
			if (mouse_press.in_zone(zone) && valid_target(zone)) {
				return zone;
			}
		}
		for (Rectangle zone: ENEMY_ZONES){
			if (mouse_press.in_zone(zone) && valid_target(zone)) {
				return zone;
			}
		}
		System.out.println("Invalid target.");
		return null;
	}
	
	/**
	 * Checks to see if target in zone is valid.
	 */
	private boolean valid_target(Rectangle zone){
		Monster mon = zone_to_monster.get(zone);
		if (null == mon) return false;
		if (!mon.alive()) return false;
		// TODO: other checks, like if it's the user
		return true;
	}

	/**
	 * Called once at the start of each turn.
	 */
	private void turn_start(){
		System.out.println("\n-- NEW TURN -- \n");
		techs.clear();
		for (Monster enemy: enemies){
			if (enemy.alive()){
				Tech tech = new Tech(enemy, choose_command(enemy));
				techs.add(tech);
				if (tech.target == Tech.Target.SINGLE){
					for (int ii = 0; ii < heroes.size(); ++ii){ // TODO: choose hero to hit
						if (heroes.get(ii).alive()) specific_targets.put(tech, heroes.get(ii));
					}
				}
			}
		}
		turn_started = true;
	}

	/**
	 * Decides target for a single-target tech.
	 */
	private void hero_target_choice(Monster hero, Rectangle selected_zone){
		for (int ii = 0; ii < enemies.size(); ++ii){ // TODO: choose enemy to hit
			if (enemies.get(ii).alive()) specific_targets.put(tech_waiting_for_target, zone_to_monster.get(selected_zone));
		}
		hero.commanded = true;
		tech_waiting_for_target = null;
	}

	/**
	 * Decides what tech a monster should use.
	 */
	private void hero_tech_choice(Monster hero){
		Tech tech = new Tech(hero, hero.techs.get(0));
		techs.add(tech);
		if (tech.target == Tech.Target.SINGLE){
			specific_targets.put(tech, null);
			tech_waiting_for_target = tech;
		}
		else{
			hero.commanded = true;
		}
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
		System.out.println("ERROR: Unsure of what action " + action + " is.");
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
			System.out.println("Defeat...");
			return true;
		}
		else if (has_lost(enemies)){
			System.out.println("Victory!");
			return true;
		}
		return false;
	}

	/**
	 * Checks if all members of team have lost.
	 */
	private boolean has_lost(ArrayList<Monster> team){
		for(Monster mon: team){
			if (mon.alive()) return false;
		}
		return true;
	}

	/**
	 * For debugging purposes.
	 */
	private void print_battle_state(){
		System.out.println("\n");
		for(Monster mon: all){
			System.out.println(mon.toString());
		}
		System.out.println(" --- \n");
	}

	public ArrayList<Monster> get_heroes(){
		return heroes;
	}
	public ArrayList<Monster> get_enemies(){
		return enemies;
	}

}
