package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;

import battle.Battle;
import battle.Battle_Final;
import battle.Monster;
import battle.Species;
import overworld.Player;
import overworld.Area;

public class FrameEngine extends ApplicationAdapter {
	private static Player player;
	private static FPSLogger fps_logger;
	private static GameState game_state = GameState.OVERWORLD;
	private static boolean do_log = false;
	private static boolean debug = true;
	private static Battle curr_battle;
	private static InputHandler input_handler;
	private static Area curr_area, new_area = null;
	private static ArrayList<Monster> party = new ArrayList<Monster>();
	public static final int TILE = 32;
	public static final Logger logger = Logger.getLogger("ERROR_LOG");
	public static GraphicsHandler graphics_handler;
	public static BattleGraphicsHandler battle_graphics_handler;

	public static final Vector2 resolution = new Vector2(1280, 640);
	private static final Vector2 start_position = new Vector2(TILE * 6, TILE * 12);
	public static float elapsed_time = 0;

	@Override
	public void create() {
		player = new Player(start_position.x, start_position.y);
		party.add(new Monster(Species.random, 2));
		party.get(0).refresh();
		curr_area = new Area("START");
		fps_logger = new FPSLogger();

		graphics_handler = new GraphicsHandler();
		battle_graphics_handler = new BattleGraphicsHandler();
		input_handler = new KeyboardMouseInputHandler();
		input_handler.initialize();
		EntityHandler.init_area_entities(curr_area);
	}

	@Override
	public void render() {
		float frame_speed = 60.0f;
		if (debug && input_handler.debug_speed_up_held()) frame_speed *= 4.0f; // speed-mode for debugging
		elapsed_time = frame_speed * (Gdx.graphics.getDeltaTime());
		
		if (input_handler.getPauseJustPressed()){
			if (game_state == GameState.PAUSED){
				game_state = GameState.OVERWORLD;
			}
			else if (game_state == GameState.OVERWORLD){
				game_state = GameState.PAUSED;
			}
		}

		switch(game_state){
		case OVERWORLD:{
			update_overworld();
		} break;
		case BATTLE:{
			if (curr_battle != null){
				update_battle();
			}
		} break;
		case VICTORY:{
			update_victory();
		} break;
		case PAUSED:{
			update_pause();
		} break;
		}

		input_handler.update();
		if (do_log) fps_logger.log();
	}

	/**
	 * Loop for OVERWORLD state.
	 */
	private void update_overworld(){
		if (new_area != null) change_area();
		EntityHandler.update();
		graphics_handler.draw_overworld();
	}

	/**
	 * Loop for BATTLE state.
	 */
	private void update_battle(){
		curr_battle.update();
		battle_graphics_handler.draw_battle(curr_battle);
	}
	
	/**
	 * Loop for VICTORY state.
	 */
	private void update_victory(){
		graphics_handler.draw_victory();
	}
	
	/**
	 * Loop for PAUSED state.
	 */
	private void update_pause(){
		graphics_handler.draw_overworld();
		graphics_handler.draw_menu();
	}

	/**
	 * Called when transitioning from OVERWORLD to BATTLE.
	 */
	public static boolean attempt_start_battle() {
		if (game_state == GameState.BATTLE){
			return false;
		}
		else{
			game_state = GameState.BATTLE;
			curr_battle = new Battle(curr_area.generate_encounter());
			return true;
		}
	}

	// TODO: remove later
	public static boolean attempt_start_boss_battle() {
		if (game_state == GameState.BATTLE){
			return false;
		}
		else{
			game_state = GameState.BATTLE;
			final int level = 5;
			curr_battle = new Battle_Final(
					new ArrayList<Monster>(Arrays.asList(
							new Monster("OFFENSE", level),
							new Monster("DEFENSE", level),
							new Monster("SUPPORT", level)
							)));
			return true;
		}
	}

	/**
	 * Called when transitioning from BATTLE to OVERWORLD.
	 */
	public static void end_battle() {
		if (game_state == GameState.BATTLE) game_state = GameState.OVERWORLD;
	}

	/**
	 * Next frame, the area will change.
	 */
	public static void initiate_area_change(String area_id) {
		new_area = new Area(area_id);
	}

	/**
	 * Cleans out the old area and initializes the new one.
	 */
	private static void change_area(){
		curr_area = new_area;
		new_area = null;
		EntityHandler.dispose();
		EntityHandler.init_area_entities(curr_area);
		graphics_handler.start_area();
	}
	
	public static void failure() {
		player.getPosition().set(start_position);
		FrameEngine.initiate_area_change("START");
		
	}

	/**
	 * Called when player wins Battle_Final.
	 */
	public static void victory() {
		game_state = GameState.VICTORY;
	}

	/**
	 * Adds a new member to the party.
	 */
	public static void add_party_member(Monster mon) {
		getParty().add(mon);
	}

	/**
	 * Removes a party member from the party.
	 */
	public static void release_party_member(int pos){
		getParty().remove(pos);
	}

	// UTILITY

	/**
	 * Gets a number between or at a and b.
	 */
	public static int get_rand_num_in_range(int a, int b){
		return a + (int)(Math.random() * (b + 1 - a));
	}

	// GET

	public static InputHandler getInputHandler(){
		return input_handler;
	}

	public static Area getCurrentArea(){
		return curr_area;
	}

	public static Battle getCurrentBattle() {
		return curr_battle;
	}

	public static Player getPlayer() {
		return player;
	}

	public static ArrayList<Monster> getParty(){
		return party;
	}

	private enum GameState{
		OVERWORLD, BATTLE, VICTORY, PAUSED
	}

}
