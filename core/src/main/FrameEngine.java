package main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;

import encounter.Battle;
import overworld.Player;

public class FrameEngine extends ApplicationAdapter {
	private static Player player;
	private static FPSLogger logger;
	private static GameState game_state = GameState.BATTLE;
	private static boolean do_log = false;
	private static boolean debug = true;
	private static Battle active_battle;
	private static InputHandler input_handler;

	public static final Vector2 resolution = new Vector2(1280, 640);
	public static float elapsed_time = 0;

	@Override
	public void create() {
		player = new Player();
		GraphicsHandler.initialize();
		setup_input_handler();
		logger = new FPSLogger();
		
		active_battle = new Battle();
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
			if (active_battle != null) update_battle();
		} break;
		}

		input_handler.update();
		if (do_log) logger.log();
	}
	
	private static void setup_input_handler(){
		input_handler = new KeyboardMouseInputHandler();
		input_handler.initialize();
	}

	private void update_overworld(){
		// TODO: update all overworld entities
		player.update();
		GraphicsHandler.draw_overworld(player);
	}
	
	private void update_battle(){
		active_battle.update();
		GraphicsHandler.draw_battle(active_battle);
	}
	
	public static InputHandler get_input_handler(){
		return input_handler;
	}

	private enum GameState{
		OVERWORLD, BATTLE
	}
	
	@Override
	public void dispose() {
		player.dispose();
	}
}
