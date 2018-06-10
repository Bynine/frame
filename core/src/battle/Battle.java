package battle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;

public class Battle {

	private final Team party_team;
	private final Team enemy_team;
	private final ArrayList<Monster> all = new ArrayList<Monster>();
	private final ArrayList<Tech> techs = new ArrayList<Tech>();
	private final ArrayDeque<Textbox> textboxes = new ArrayDeque<Textbox>();
	private final HashMap<Tech, Monster> specific_targets = new HashMap<Tech, Monster>();
	private final HashMap<Monster, Monster> guarded_targets = new HashMap<Monster, Monster>();
	private final BattleGUI gui;

	private boolean battle_ended = false;
	private boolean wrapped_up = false;
	private boolean commands_issued = false;
	private boolean turn_started = false;
	private boolean victory = false;
	private boolean tamed = false;

	private Tech tech_waiting_for_target = null;
	private Monster curr_mon = null;
	/**
	 * Saves the zone of the monster the user intends to tame while they choose a monster to release.
	 */
	private Rectangle tame_zone = null;
	protected State state = State.DECIDE;

	public static final int MAX_PARTY_SIZE = 3;

	/**
	 * Start a new battle, given some monsters to function as enemies.
	 */
	public Battle(ArrayList<Monster> enemy){
		party_team = new Team(FrameEngine.getParty());
		enemy_team = new Team(enemy);
		all.addAll(FrameEngine.getParty());
		all.addAll(enemy);
		for (Monster mon: all){
			mon.refresh();
		}
		gui = new BattleGUI(party_team, enemy_team);
	}

	/**
	 * The core loop.
	 */
	public void update(){
		if (!textboxes.isEmpty()){
			textbox_loop();
		}
		else if (battle_ended){
			if (!wrapped_up){
				wrap_up();
			}
			if (textboxes.isEmpty()){
				FrameEngine.end_battle();
			}
		}
		else if (!commands_issued){
			if (!turn_started){
				turn_start();
			}
			determine_state();
			command_loop();
		}
		else{
			battle_loop();
		}
	}
	
	private void textbox_loop(){
		if (FrameEngine.getInputHandler().getPointer().this_frame){
			textboxes.remove();
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
		else if (state == State.RELEASE){
			state = State.RELEASE;
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
	private void battle_loop(){	
		state = State.BATTLE;
		if (techs.isEmpty()){
			end_turn();
		}
		else{
			Tech tech = techs.remove(techs.size() - 1);
			if (!battle_ended && tech.user.getStatus().can_act()){
				print_battle_state();
				add_textbox(tech.user.nickname + " used " + tech.name + "!");
				execute_tech(tech);
				battle_ended = check_end();
			}
		}
		
	}

	/**
	 * Cleans up references at the end of a turn.
	 */
	private void end_turn(){
		state = State.DECIDE;
		commands_issued = false;
		turn_started = false;
		specific_targets.clear();
		guarded_targets.clear();
		for (Monster mon: party_team.getMembers()){
			mon.getStatus().commanded = false;
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
				target = check_guarded_target(tech.user, target);
				if (target.getStatus().alive()){
					Action.load_action(action, target, tech);
				}
			}
		}
	}
	
	/**
	 * Checks to see if the target is being guarded.
	 */
	private Monster check_guarded_target(Monster user, Monster target){
		if (guarded_targets.containsKey(target)){
			boolean same_team = 
					party_team.getMembers().containsAll(Arrays.asList(user, guarded_targets.get(target))) ||
					enemy_team.getMembers().containsAll(Arrays.asList(user, guarded_targets.get(target)));
			if (!same_team) {
				add_textbox
				(guarded_targets.get(target).getNickname() + " guarded " + target.getNickname());
				return guarded_targets.get(target);
			}
		}
		return target;
	}

	/**
	 * Checks to see if the tech can be used, give the team's gauge. If it can't, stops the tech.
	 */
	private boolean check_team_gauge(Team team, Tech tech){
		if (team.has_tech(tech)) {
			if (!team.enough_team_gauge(tech.tg)){
				add_textbox
				("Didn't have enough Team Power to use " + tech.name);
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
			if (!mon.getStatus().commanded && mon.getStatus().can_act()) commands_issued = false;
		}
		for(Monster mon: party_team.getMembers()){
			// TODO: organize by speed instead of team order
			if (curr_mon == null && mon.getStatus().can_act() && !mon.getStatus().commanded) {
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
				techs.sort(new Tech.TechComparator());
			}
		} break;
		case TAME:{
			tame(zone);
		} break;
		case RELEASE:{
			release(zone);
		} break;
		case DECIDE:{ // TODO: Refactor, ugly
			int pos = BattleGUI.DECISION_ZONES.indexOf(zone);
			switch(pos){
			case 0: { 
				state = State.CHOOSE_TECH; 
			} break;
			case 1: { 
				state = State.TAME; 
			} break;
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
		if (!mon.getStatus().can_target()) return false;
		return true;
	}

	/**
	 * Checks to see if there's a tech in the selected zone.
	 */
	private boolean valid_tech_target(Rectangle zone){
		if (BattleGUI.TECH_ZONES.indexOf(zone) 
				>= curr_mon.tech_ids.size()) return false; // can't choose moves that don't exist
		return true;
	}

	/**
	 * Tames monster in the selected zone.
	 */
	protected void tame(Rectangle zone){
		if (FrameEngine.getParty().size() >= MAX_PARTY_SIZE){
			state = State.RELEASE;
			tame_zone = zone;
		}
		else{
			Monster mon = gui.getMonsterFromZone(zone);
			FrameEngine.add_party_member(mon);
			battle_ended = true;
		}
		tamed = true;
	}

	/**
	 * Releases the selected monster in the zone.
	 */
	private void release(Rectangle zone){
		add_textbox("Released " + gui.getMonsterFromZone(zone).nickname + "...");
		FrameEngine.release_party_member(BattleGUI.PARTY_ZONES.indexOf(zone));
		tame(tame_zone);
	}

	/**
	 * Called once at the start of each turn.
	 */
	private void turn_start(){
		System.out.println("\n-- NEW TURN -- \n");
		print_battle_state();
		techs.clear();
		make_enemy_decisions();
		turn_started = true;
	}

	/**
	 * Decides what tech enemy uses and who they use it on.
	 */
	private void make_enemy_decisions(){
		for (Monster enemy: enemy_team.getMembers()){
			if (enemy.getStatus().can_act()){
				Tech tech = new Tech(enemy, choose_command(enemy));
				techs.add(tech);
				if (tech.target == Tech.Target.SINGLE){
					boolean friendly = // TODO: Refactor to be less fugly
							tech.actions.contains("HEAL") 
							|| tech.actions.contains("RAISEPOW")
							|| tech.actions.contains("RAISEDEF")
							|| tech.actions.contains("GUARD")
							;
					ArrayList<Monster> prospective_targets;
					if (friendly){
						prospective_targets = enemy_decision_helper(enemy_team);
					}
					else{
						prospective_targets = enemy_decision_helper(party_team);
					}
					specific_targets.put(tech, prospective_targets.get
							(FrameEngine.get_rand_num_in_range(0, prospective_targets.size() - 1)));
				}
			}
		}
	}

	/**
	 * Decides who is elible to be targetted by the enemy.
	 */
	private ArrayList<Monster> enemy_decision_helper(Team team){
		ArrayList<Monster> prospective_targets = new ArrayList<Monster>();
		for (int ii = 0; ii < team.getMembers().size(); ++ii){
			if (team.getMembers().get(ii).getStatus().can_target()) {
				prospective_targets.add(team.getMembers().get(ii));
			}
		}
		return prospective_targets;
	}

	/**
	 * Decides target for a single-target tech.
	 */
	private void hero_target_choice(Monster hero, Rectangle selected_zone){
		for (int ii = 0; ii < enemy_team.getMembers().size(); ++ii){
			if (enemy_team.getMembers().get(ii).getStatus().can_target()) {
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
		Tech tech = new Tech(hero, hero.tech_ids.get(BattleGUI.TECH_ZONES.indexOf(zone)));
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
		hero.getStatus().commanded = true;
		curr_mon = null;
	}

	/**
	 * Choose enemy commands based on battle state.
	 */
	private String choose_command(Monster enemy){
		// TODO: implement ai choosing move
		// TODO: implement ai choosing target
		return enemy.tech_ids.get(FrameEngine.get_rand_num_in_range(0, enemy.tech_ids.size()-1));
	}

	/**
	 * Checks whether the battle has ended.
	 */
	private boolean check_end(){
		if (has_lost(party_team)){
			return true;
		}
		else if (has_lost(enemy_team)){
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
			if (mon.getStatus().alive()) return false;
		}
		return true;
	}

	/**
	 * Called once the battle is finished.
	 */
	protected void wrap_up(){
		if (victory){
			victory();
		}
		else if (tamed){
			add_textbox("Success! Your new friend is happy to be with you.");
		}
		else{
			failure();
		}
		wrapped_up = true;
	}

	/**
	 * Called during end loop. Aggregates experience from enemies, then adds them to party monsters.
	 */
	protected void victory(){
		int exp = 0;
		float exp_bonus = 1.0f;
		for(Monster enemy: enemy_team.getMembers()){
			exp += enemy.determine_exp_gain();
			exp_bonus *= 1.2f; // Fighting more monsters leads to more exp
		}
		exp *= exp_bonus;
		add_textbox("Victory! Each party member gained " + exp + " experience.");
		for (Monster mon: party_team.getMembers()){
			mon.refresh();
			mon.add_experience(exp);
		}
	}

	/**
	 * Called if the player's party all faint.
	 */
	protected void failure(){
		add_textbox("Defeat...");
	}

	/**
	 * Returns a list of the current monster's techs, to display on screen.
	 */
	public ArrayList<String> getTechs() {
		// TODO: Get actual techs, not just their id strings.
		if (null == curr_mon){
			return new ArrayList<String>();
		}
		return curr_mon.tech_ids;
	}

	/**
	 * Adds a target that's guarded by "guarder" to guarded_targets. Called by Action_Guard.
	 */
	void add_guarded_target(Monster guarder, Monster target){
		guarded_targets.put(target, guarder);
	}
	
	/**
	 * Adds a new textbox to the stack.
	 */
	void add_textbox(String text){
		textboxes.add(new Textbox(text));
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
	
	/**
	 * Gets the textbox that should be displayed.
	 */
	public Textbox getCurrentTextbox(){
		if (textboxes.isEmpty()) {
			return null;
		}
		return textboxes.peek();
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
		DECIDE, CHOOSE_TECH, CHOOSE_TARGET, BATTLE, TAME, RELEASE
	}
	public Monster getCurrentMonster() {
		return curr_mon;
	}
	public BattleGUI getGUI(){
		return gui;
	}

}
