package battle;

import java.util.ArrayList;
import java.util.HashMap;
import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;

public class Battle {

	private final Team party_team;
	private final Team enemy_team;
	private final ArrayList<Monster> all = new ArrayList<Monster>();
	private final ArrayList<Tech> techs = new ArrayList<Tech>();
	private final HashMap<Tech, Monster> specific_targets = new HashMap<Tech, Monster>();
	private final BattleGUI gui;

	private boolean battle_ended = false;
	private boolean commands_issued = false;
	private boolean turn_started = false;
	private boolean victory = false;

	private Tech tech_waiting_for_target = null;
	private Monster curr_mon = null;
	private State state = State.DECIDE;


	/**
	 * Start a new battle, given some monsters to function as enemies.
	 */
	public Battle(ArrayList<Monster> enemy){
		party_team = new Team(FrameEngine.getParty());
		enemy_team = new Team(enemy);
		all.addAll(FrameEngine.getParty());
		all.addAll(enemy);
		for (Monster mon: all){
			mon.initialize_for_battle();
		}
		gui = new BattleGUI(party_team, enemy_team);
	}

	/**
	 * The core loop.
	 */
	public void update(){
		if (battle_ended){
			end_execute();
		}
		else if (!commands_issued){
			if (!turn_started){
				turn_start();
			}
			determine_state();
			command_loop();
		}
		else{
			battle_execute();
		}
	}
	
	/**
	 * Changes battle.state based on the game state.
	 */
	private void determine_state(){
		if(tech_waiting_for_target != null){
			state = State.CHOOSE_TARGET;
		}
		else if (state == State.TAME){
			state = State.TAME;
		}
		else if (state != State.DECIDE){
			state = State.CHOOSE_TECH;
		}
		else{
			state = State.DECIDE;
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
		state = State.DECIDE;
		commands_issued = false;
		turn_started = false;
		specific_targets.clear();
		for (Monster mon: party_team.getMembers()){
			mon.commanded = false;
		}
		curr_mon = null;
		party_team.boost_team_gauge();
		enemy_team.boost_team_gauge();
	}

	/**
	 * Performs all the actions contained in the tech.
	 */
	private void execute_tech(Tech tech){
		if (!check_team_gauge(party_team, tech)) return;
		if (!check_team_gauge(enemy_team, tech)) return;
		ArrayList<Monster> targets = get_targets(tech);
		for (String action: tech.actions){
			for (Monster target: targets){
				Action.load_action(action, target, tech);
			}
		}
	}

	/**
	 * Checks to see if the tech can be used, give the team's gauge. If it can't, stops the tech.
	 */
	private boolean check_team_gauge(Team team, Tech tech){
		if (team.has_tech(tech)) {
			if (!team.enough_team_gauge(tech.tg)){
				System.out.println("Didn't have enough Team Power to use " + tech.name);
				return false;
			}
			else{
				team.use_team_gauge(tech.tg);
				return true;
			}
		}
		return true;
	}

	/**
	 * Based on tech's Target parameter, get the list of targets the tech affects.
	 */
	private ArrayList<Monster> get_targets(Tech tech){
		ArrayList<Monster> targets = new ArrayList<Monster>();
		ArrayList<Monster> opposing_team = new ArrayList<Monster>();
		if (party_team.getMembers().contains(tech.user)){
			opposing_team.addAll(enemy_team.getMembers());
		}
		else if (enemy_team.getMembers().contains(tech.user)){
			opposing_team.addAll(party_team.getMembers());
		}
		switch(tech.target){
		case NONE:{ // don't add any targets
		} break;
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
		for (Monster mon: party_team.getMembers()){
			if (!mon.commanded && mon.can_act()) commands_issued = false;
		}
		for(Monster mon: party_team.getMembers()){
			// TODO: organize by speed instead of team order
			if (curr_mon == null && mon.can_act() && !mon.commanded) {
				curr_mon = mon;
			}
		}
		Rectangle selected_zone = gui.evaluate_zone(state);
		if (selected_zone != null) {
			select_zone_result(selected_zone);
		}
	}
	
	/**
	 * Executes the result of selecting a particular zone.
	 */
	private void select_zone_result(Rectangle zone){
		switch(state){
		case CHOOSE_TARGET:{
			if (valid_monster_target(zone)){
				System.out.println(curr_mon.nickname + " chose target for tech.");
				hero_target_choice(curr_mon, zone);
			}
		} break;
		case CHOOSE_TECH:{
			if (valid_tech_target(zone)){
				System.out.println(curr_mon.nickname + " chose tech.");
				hero_tech_choice(curr_mon, zone);
			}
		} break;
		case TAME:{
			tame(zone);
		} break;
		case DECIDE:{ // TODO: Refactor, ugly
			int pos = BattleGUI.DECISION_ZONES.indexOf(zone);
			switch(pos){
			case 0: { state = State.CHOOSE_TECH; } break;
			case 1: { state = State.TAME; } break;
			}
		} break;
		default:
			break;
		}
	}

	/**
	 * Checks to see if targeted monster in zone is valid.
	 */
	public boolean valid_monster_target(Rectangle zone){
		Monster mon = gui.getMonsterFromZone(zone);
		//if (mon == curr_mon) return false;
		if (null == mon) return false;
		if (!mon.can_target()) return false;
		return true;
	}

	/**
	 * Checks to see if there's a tech in the selected zone.
	 */
	private boolean valid_tech_target(Rectangle zone){
		if (BattleGUI.TECH_ZONES.indexOf(zone) 
				>= curr_mon.techs.size()) return false; // can't choose moves that don't exist
		return true;
	}

	/**
	 * Tames monster in the selected zone.
	 */
	private void tame(Rectangle zone){
		Monster mon = gui.getMonsterFromZone(zone);
		FrameEngine.add_party_member(mon);
		end_execute();
	}

	/**
	 * Called once at the start of each turn.
	 */
	private void turn_start(){
		System.out.println("\n-- NEW TURN -- \n");
		print_battle_state();
		techs.clear();
		for (Monster enemy: enemy_team.getMembers()){
			if (enemy.can_act()){
				Tech tech = new Tech(enemy, choose_command(enemy));
				techs.add(tech);
				if (tech.target == Tech.Target.SINGLE){
					for (int ii = 0; ii < party_team.getMembers().size(); ++ii){ // TODO: choose hero to hit
						if (party_team.getMembers().get(ii).can_target()) {
							specific_targets.put(tech, party_team.getMembers().get(ii));
						}
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
		for (int ii = 0; ii < enemy_team.getMembers().size(); ++ii){
			if (enemy_team.getMembers().get(ii).can_target()) {
				specific_targets.put(tech_waiting_for_target, gui.getMonsterFromZone(selected_zone));
			}
		}
		commanded_hero(hero);
		tech_waiting_for_target = null;
	}

	/**
	 * Decides what tech a monster should use.
	 */
	private void hero_tech_choice(Monster hero, Rectangle zone){
		Tech tech = new Tech(hero, hero.techs.get(BattleGUI.TECH_ZONES.indexOf(zone)));
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
	 * Choose enemy commands based on battle state.
	 */
	private String choose_command(Monster enemy){
		// TODO: implement ai choosing move
		// TODO: implement ai choosing target
		return enemy.techs.get(FrameEngine.get_rand_num_in_range(0, enemy.techs.size()-1));
	}

	/**
	 * Checks whether the battle has ended.
	 */
	private boolean check_end(){
		if (has_lost(party_team)){
			System.out.println("Defeat...");
			return true;
		}
		else if (has_lost(enemy_team)){
			System.out.println("Victory!");
			victory = true;
			return true;
		}
		return false;
	}

	/**
	 * Checks if all members of team have lost.
	 */
	private boolean has_lost(Team team){
		for(Monster mon: team.getMembers()){
			if (mon.alive()) return false;
		}
		return true;
	}

	/**
	 * Called once the battle is finished.
	 */
	private void end_execute(){
		if (victory){
			gain_experience();
		}
		else{
			// TODO: Loss function
		}
		FrameEngine.end_battle();
	}

	/**
	 * Called during end loop. Aggregates experience from enemies, then adds them to party monsters.
	 */
	private void gain_experience(){
		int exp = 0;
		float exp_bonus = 1.0f;
		for(Monster enemy: enemy_team.getMembers()){
			exp += enemy.determine_exp_gain();
			exp_bonus *= 1.2f; // Fighting more monsters leads to more exp
		}
		exp *= exp_bonus;
		for (Monster mon: party_team.getMembers()){
			mon.add_experience(exp);
		}
	}

	/**
	 * Returns a list of the current monster's techs, to display on screen.
	 */
	public ArrayList<String> getTechs() {
		// TODO: Get actual techs, not just their id strings.
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

	public Team getParty(){
		return party_team;
	}
	public Team getEnemies(){
		return enemy_team;
	}
	public State getState(){
		return state;
	}
	public enum State{
		DECIDE, CHOOSE_TECH, CHOOSE_TARGET, BATTLE, TAME
	}

}
