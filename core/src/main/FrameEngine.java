package main;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;

import battle.Battle;
import battle.Monster;
import overworld.Player;
import overworld.Area;

public class FrameEngine extends ApplicationAdapter {
	private static Player player;
	private static FPSLogger fps_logger;
	private static GameState game_state = GameState.OVERWORLD;
	private static boolean do_log = false;
	private static boolean debug = true;
	private static Battle active_battle;
	private static InputHandler input_handler;
	private static Area curr_area;
	private static ArrayList<Monster> party = new ArrayList<Monster>();
	public static final int TILE = 32;
	public static final Logger logger = Logger.getLogger("logger");

	public static final Vector2 resolution = new Vector2(1280, 640);
	public static float elapsed_time = 0;

	@Override
	public void create() {
		player = new Player(TILE * 6, TILE * 12);
		party.add(new Monster("DUMPLING", 2));
		curr_area = new Area("START");
		fps_logger = new FPSLogger();

		GraphicsHandler.initialize();
		BattleGraphicsHandler.initialize();
		input_handler = new KeyboardMouseInputHandler();
		input_handler.initialize();
		EntityHandler.init_area_entities(curr_area);
	}

	@Override
	public void render() {
		float frame_speed = 60.0f;
		if (debug && input_handler.debug_speed_up_held()) frame_speed *= 4.0f; // speed-mode for debugging
		elapsed_time = frame_speed * (Gdx.graphics.getDeltaTime());

		switch(game_state){
		case OVERWORLD:{
			update_overworld();
		} break;
		case BATTLE:{
			if (active_battle != null){
				update_battle();
			}
		} break;
		}

		input_handler.update();
		if (do_log) fps_logger.log();
	}

	/**
	 * Loop for OVERWORLD state.
	 */
	private void update_overworld(){
		EntityHandler.update();
		GraphicsHandler.draw_overworld();
	}

	/**
	 * Loop for BATTLE state.
	 */
	private void update_battle(){
		active_battle.update();
		BattleGraphicsHandler.draw_battle(active_battle);
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
			active_battle = new Battle(curr_area.generate_encounter());
			return true;
		}
	}

	/**
	 * Called when transitioning from BATTLE to OVERWORLD.
	 */
	public static void end_battle() {
		game_state = GameState.OVERWORLD;
	}

	public static InputHandler getInputHandler(){
		return input_handler;
	}

	public static Area getCurrentArea(){
		return curr_area;
	}

	public static Player getPlayer() {
		return player;
	}

	public static ArrayList<Monster> getParty(){
		return party;
	}

	private enum GameState{
		OVERWORLD, BATTLE
	}

}
