package main;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;

import encounter.Battle;
import encounter.Monster;
import encounter.Species;
import overworld.Player;
import overworld.Area;
import overworld.InteractableEntity;

public class FrameEngine extends ApplicationAdapter {
	public static final boolean debug	= true;
	public static final boolean mute	= false;
	public static final boolean do_log	= true;

	private static Player player;
	private static FPSLogger fps_logger;
	private static GameState game_state = GameState.OVERWORLD;
	private static InputHandler input_handler;
	private static Area curr_area = null;
	private static Battle curr_battle;
	private static InteractableEntity curr_interactable;
	private static Textbox curr_textbox;
	private static ArrayList<Monster> party = new ArrayList<Monster>();
	private static Timer time = new Timer(0);
	private static Area new_area = null;
	private static Vector2 new_position = new Vector2();

	public static final int TILE = 32;
	public static final Logger logger = Logger.getLogger("ERROR_LOG");
	public static GraphicsHandler graphics_handler;
	public static BattleGraphicsHandler battle_graphics_handler;
	private static final Vector2 start_position = new Vector2(TILE * 30, TILE * 12);

	public static final Vector2 resolution = new Vector2(TILE * 40, TILE * 20);
	public static float elapsed_time = 0;

	@Override
	public void create() {
		player = new Player(start_position.x, start_position.y);
		party.add(new Monster(Species.random, 2));
		party.get(0).refresh();
		AudioHandler.initialize();
		curr_area = new Area("FOREST");
		fps_logger = new FPSLogger();

		graphics_handler = new GraphicsHandler();
		battle_graphics_handler = new BattleGraphicsHandler();
		input_handler = new KeyboardMouseInputHandler();
		input_handler.initialize();
		EntityHandler.init_area_entities(curr_area);
	}

	@Override
	public void render() {
		elapsed_time = getGameSpeed() * (Gdx.graphics.getDeltaTime());
		time.countUp();

		AudioHandler.update();
		assess_inputs();
		curr_interactable = null;

		switch(game_state){
		case OVERWORLD:{
			update_overworld();
		} break;
		case BATTLE:{
			if (curr_battle != null){
				update_battle();
			}
		} break;
		case PAUSED:{
			update_pause();
		} break;
		}

		input_handler.update();
		if (do_log) fps_logger.log();
	}

	/**
	 * Checks inputs from input handler and performs necessary actions.
	 * TODO: Refactor this into seperate class
	 */
	private void assess_inputs(){
		if (input_handler.getPauseJustPressed()){
			if (game_state == GameState.PAUSED){
				game_state = GameState.OVERWORLD;
			}
			else if (game_state == GameState.OVERWORLD){
				game_state = GameState.PAUSED;
			}
		}
		if (input_handler.getActionJustPressed()) handle_action_pressed();
	}

	/**
	 * Performs actions when player hits Action.
	 */
	private void handle_action_pressed(){
		if (null != curr_textbox){
			if (curr_textbox.isFinished()){
				curr_textbox.dispose();
				curr_textbox = null;
			}
			else{
				curr_textbox.complete();
			}
		}
		else if (null != curr_interactable){
			curr_interactable.interact();
		}
	}

	/**
	 * Loop for OVERWORLD state.
	 */
	private void update_overworld(){
		if (new_area != null) {
			change_area();
		}
		if (canUpdateEntities()){
			EntityHandler.update();
		}
		graphics_handler.draw_overworld();
		if (null != curr_textbox){
			curr_textbox.update();
		}
	}

	/**
	 * Loop for BATTLE state.
	 */
	private void update_battle(){
		curr_battle.update();
		battle_graphics_handler.draw_battle(curr_battle);
	}

	/**
	 * Loop for PAUSED state.
	 */
	private void update_pause(){
		graphics_handler.draw_overworld();
		graphics_handler.draw_pause();
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

	/**
	 * Called when transitioning from BATTLE to OVERWORLD.
	 */
	public static void end_battle() {
		if (game_state == GameState.BATTLE) game_state = GameState.OVERWORLD;
	}

	/**
	 * Next frame, the area will change.
	 */
	public static void initiate_area_change(String area_id, Vector2 position) {
		new_position.set(position);
		new_area = new Area(area_id);
	}

	/**
	 * Cleans out the old area and initializes the new one.
	 */
	private static void change_area(){
		curr_area = new_area;
		new_area = null;
		player.getPosition().set(new_position.x, (curr_area.map_height) - (new_position.y));
		player.getVelocity().setZero();
		AudioHandler.clearAudioSources();
		EntityHandler.dispose();
		EntityHandler.init_area_entities(curr_area);
		graphics_handler.start_area();
	}

	/**
	 * Called when the player faints.
	 */
	public static void failure() {
		player.getPosition().set(start_position);
		FrameEngine.initiate_area_change("START", new Vector2(0, 0));
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

	/**
	 * Updates which entity the player will interact with when they hit Action.
	 */
	public static void setInteractableEntity(InteractableEntity interactableEntity) {
		curr_interactable = interactableEntity;
	}

	/**
	 * Starts a textbox.
	 */
	public static void setTextbox(String text){
		curr_textbox = new Textbox(text);
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

	public static float getGameSpeed(){
		float game_speed = 60.0f;
		if (debug && input_handler.debug_speed_up_held()) {
			game_speed *= 4.0f;
		}
		return game_speed;
	}

	public static boolean canInteract(){
		return null != curr_interactable;
	}

	public static boolean canUpdateEntities(){
		return null == curr_textbox;
	}

	public static Textbox getCurrentTextbox(){
		return curr_textbox;
	}

	private enum GameState{
		OVERWORLD, BATTLE, PAUSED
	}

	public static float getTime() {
		return time.getCounter();
	}

}
