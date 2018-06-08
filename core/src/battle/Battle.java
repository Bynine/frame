package battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;
import main.MousePress;

public class Battle {

	private final ArrayList<Monster> party = new ArrayList<Monster>();
	private final ArrayList<Monster> enemies = new ArrayList<Monster>();
	private final ArrayList<Monster> all = new ArrayList<Monster>();
	private final ArrayList<Tech> techs = new ArrayList<Tech>();
	private final HashMap<Tech, Monster> specific_targets = new HashMap<Tech, Monster>();
	private final HashMap<Rectangle, Monster> zone_to_monster = new HashMap<Rectangle, Monster>();

	private boolean battle_ended = false;
	private boolean commands_issued = false;
	private boolean turn_started = false;
	private Tech tech_waiting_for_target = null;
	private Monster curr_mon = null;
	private State state = State.CHOOSE_TECH;

	private static final int hero_width = 240;
	private static final int hero_height = 120;
	private static final int hero_y = 120;
	public static final ArrayList<Rectangle> HERO_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(1*Gdx.graphics.getWidth()/5, hero_y, hero_width, hero_height),
			new Rectangle(2*Gdx.graphics.getWidth()/5, hero_y, hero_width, hero_height),
			new Rectangle(3*Gdx.graphics.getWidth()/5, hero_y, hero_width, hero_height)
			));
	private static final int enemy_dim = 240;
	private static final int enemy_y = 240;
	public static final ArrayList<Rectangle> ENEMY_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(1*Gdx.graphics.getWidth()/5, enemy_y, enemy_dim, enemy_dim),
			new Rectangle(2*Gdx.graphics.getWidth()/5, enemy_y, enemy_dim, enemy_dim),
			new Rectangle(3*Gdx.graphics.getWidth()/5, enemy_y, enemy_dim, enemy_dim)
			));
	private static final int move_w = 240;
	private static final int move_h = 120;
	private static final int move_y = 0;
	public static final ArrayList<Rectangle> TECH_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(0*Gdx.graphics.getWidth()/4, move_y, move_w, move_h),
			new Rectangle(1*Gdx.graphics.getWidth()/4, move_y, move_w, move_h),
			new Rectangle(2*Gdx.graphics.getWidth()/4, move_y, move_w, move_h),
			new Rectangle(3*Gdx.graphics.getWidth()/4, move_y, move_w, move_h)
			));


	/**
	 * Start a new battle, given some monsters to function as enemies.
	 */
	public Battle(ArrayList<Monster> enemy){
		this.party.addAll(FrameEngine.getParty());
		this.enemies.addAll(enemy);
		all.addAll(party);
		all.addAll(enemies);
		for (Monster mon: all){
			mon.initialize_for_battle();
		}
		for (int ii = 0; ii < party.size(); ++ii){
			zone_to_monster.put(HERO_ZONES.get(ii), party.get(ii));
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
			end_execute();
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
		state = State.BATTLE;
		techs.sort(new Tech.TechComparator());
		for (Tech tech: techs){
			if (!battle_ended && tech.user.can_act()){
				print_battle_state();
				execute_tech(tech);
				battle_ended = check_end();
			}
		}
		end_turn();
	}

	/**
	 * Cleans up references at the end of a turn.
	 */
	private void end_turn(){
		state = State.CHOOSE_TECH;
		commands_issued = false;
		turn_started = false;
		specific_targets.clear();
		for (Monster mon: party){
			mon.commanded = false;
		}
		curr_mon = null;
	}

	/**
	 * Performs all the actions contained in the tech.
	 */
	private void execute_tech(Tech tech){
		ArrayList<Monster> targets = get_targets(tech);
		for (String action: tech.actions){
			for (Monster target: targets){
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
		if (party.contains(tech.user)){
			opposing_team.addAll(enemies);
		}
		else if (enemies.contains(tech.user)){
			opposing_team.addAll(party);
		}
		switch(tech.target){
		case NONE:{
			// don't add any targets
		}
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
		if(tech_waiting_for_target != null){
			state = State.CHOOSE_TARGET;
		}
		else{
			state = State.CHOOSE_TECH;
		}
		commands_issued = true;
		for (Monster mon: party){
			if (!mon.commanded && mon.can_act()) commands_issued = false;
		}
		if (!turn_started){
			turn_start();
		}
		for(Monster mon: party){ // choose the first active party member who hasn't been commanded yet
			// TODO: organize by speed instead of team order
			if (curr_mon == null && mon.can_act() && !mon.commanded) {
				curr_mon = mon;
			}
		}
		Rectangle selected_zone = evaluate_zone();
		if (selected_zone != null) {
			if(tech_waiting_for_target != null){
				System.out.println(curr_mon.nickname + " chose target for tech.");
				hero_target_choice(curr_mon, selected_zone);
			}
			else{
				System.out.println(curr_mon.nickname + " chose tech.");
				hero_tech_choice(curr_mon, selected_zone);
			}
		}
	}

	/**
	 * Checks if mouse was clicked and if the position it was clicked in corresponds to something valid.
	 */
	private Rectangle evaluate_zone(){
		MousePress mouse_press = FrameEngine.getInputHandler().getMousePress();
		if (!mouse_press.this_frame) {
			return null;
		}
		if (tech_waiting_for_target != null){
			for (Rectangle zone: HERO_ZONES){
				if (mouse_press.in_zone(zone) && valid_monster_target(zone)) {
					return zone;
				}
			}
			for (Rectangle zone: ENEMY_ZONES){
				if (mouse_press.in_zone(zone) && valid_monster_target(zone)) {
					return zone;
				}
			}
			System.out.println("Invalid target.");
		}
		else{ // choosing tech
			for (Rectangle zone: TECH_ZONES){
				if (mouse_press.in_zone(zone) && valid_move_target(zone)) {
					return zone;
				}
			}
		}
		return null;
	}

	/**
	 * Checks to see if targeted monster in zone is valid.
	 */
	private boolean valid_monster_target(Rectangle zone){
		Monster mon = zone_to_monster.get(zone);
		//if (mon == curr_mon) return false;
		if (null == mon) return false;
		if (!mon.can_target()) return false;
		return true;
	}

	/**
	 * 
	 */
	private boolean valid_move_target(Rectangle zone){
		if (TECH_ZONES.indexOf(zone) 
				>= curr_mon.techs.size()) return false; // can't choose moves that don't exist
		return true;
	}

	/**
	 * Called once at the start of each turn.
	 */
	private void turn_start(){
		System.out.println("\n-- NEW TURN -- \n");
		print_battle_state();
		techs.clear();
		for (Monster enemy: enemies){
			if (enemy.can_act()){
				Tech tech = new Tech(enemy, choose_command(enemy));
				techs.add(tech);
				if (tech.target == Tech.Target.SINGLE){
					for (int ii = 0; ii < party.size(); ++ii){ // TODO: choose hero to hit
						if (party.get(ii).can_target()) specific_targets.put(tech, party.get(ii));
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
		for (int ii = 0; ii < enemies.size(); ++ii){
			if (enemies.get(ii).can_target()) {
				specific_targets.put(tech_waiting_for_target, zone_to_monster.get(selected_zone));
			}
		}
		commanded_hero(hero);
		tech_waiting_for_target = null;
	}

	/**
	 * Decides what tech a monster should use.
	 */
	private void hero_tech_choice(Monster hero, Rectangle zone){
		Tech tech = new Tech(hero, hero.techs.get(TECH_ZONES.indexOf(zone)));
		techs.add(tech);
		if (tech.target == Tech.Target.SINGLE){
			specific_targets.put(tech, null);
			tech_waiting_for_target = tech;
		}
		else{
			commanded_hero(hero);
		}
	}

	/**
	 * This monster is now done being commanded.
	 */
	private void commanded_hero(Monster hero){
		hero.commanded = true;
		curr_mon = null;
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
		case "DIE":
			return new Action_Die(tech);
		}
		FrameEngine.logger.log(Level.WARNING, "Unsure what action " + action + " is.");
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
		if (has_lost(party)){
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
	 * Called once the battle is finished.
	 */
	private void end_execute(){
		// TODO: Check if it was a victory or failure
		for (Monster mon: party){
			for(Monster enemy: enemies){
				mon.add_experience(enemy.level);
			}
		}
		FrameEngine.end_battle();
	}

	public ArrayList<String> getTechs() {
		if (null == curr_mon){
			return new ArrayList<String>();
		}
		return curr_mon.techs;
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

	public ArrayList<Monster> getEnemies(){
		return enemies;
	}
	public State getState(){
		return state;
	}

	public enum State{
		CHOOSE_TECH, CHOOSE_TARGET, BATTLE
	}

}
