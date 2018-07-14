package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;

import debug.DebugMenu;
import encounter.Encounter;
import overworld.Player;
import overworld.Area;
import overworld.InteractableEntity;

public class FrameEngine extends ApplicationAdapter {
	public static boolean DEBUG	= false;

	public static boolean DRAW 	= true && DEBUG;
	public static boolean MUTE	= true && DEBUG;
	public static boolean LOG	= true && DEBUG;

	private static Player player;
	private static FPSLogger fpsLogger;
	private static GameState gameState = GameState.OVERWORLD;
	private static InputHandler inputHandler;
	private static Area currArea = null;
	private static Encounter currEncounter;
	private static InteractableEntity currInteractable;
	private static Textbox currTextbox;
	private static Timer 
	time = new Timer(0),
	transition = new Timer(30);
	private static ArrayList<Timer> timers = new ArrayList<Timer>(Arrays.asList(
			time, transition
			));
	private static Area newArea = null;
	private static Vector2 newPosition = new Vector2();

	public static final int TILE = 32;
	public static final Logger logger = Logger.getLogger("ERROR_LOG");
	public static DebugMenu debugMenu;
	public static GraphicsHandler graphicsHandler;
	public static EncounterGraphicsHandler encounterGraphicsHandler;

	public static final Vector2 resolution = new Vector2(TILE * 40, TILE * 20);
	public static float elapsedTime = 0;
	
	public static int playerHealth = 1;

	@Override
	public void create() {
		player = new Player(0, 0);
		AudioHandler.initialize();
		debugMenu = new DebugMenu();
		newArea = new Area("FOREST");
		currEncounter = new Encounter("HUNGRY");
		newPosition.set(newArea.getStartLocation());
		currArea = newArea;
		fpsLogger = new FPSLogger();

		graphicsHandler = new GraphicsHandler();
		encounterGraphicsHandler = new EncounterGraphicsHandler();
		inputHandler = new KeyboardMouseInputHandler();
		inputHandler.initialize();
		changeArea();
	}

	@Override
	public void render() {
		elapsedTime = 60.0f * getGameSpeed() * (Gdx.graphics.getDeltaTime());
		if (Gdx.graphics.getDeltaTime() > 1) {
			printDebugOnLag();
		}
		for (Timer timer: timers){
			timer.countUp();
		}

		AudioHandler.update();
		assessInputs();
		currInteractable = null;

		switch(gameState){
		case OVERWORLD:{
			updateOverworld();
		} break;
		case ENCOUNTER:{
			if (currEncounter != null){
				updateEncounter();
			}
		} break;
		case PAUSED:{
			updatePause();
		} break;
		case DEBUG:{
			updateDebug();
		} break;
		}

		inputHandler.update();
		if (LOG) fpsLogger.log();
	}

	/**
	 * Checks inputs from input handler and performs necessary actions.
	 */
	private void assessInputs(){
		if (inputHandler.getPauseJustPressed()){
			if (gameState == GameState.PAUSED){
				gameState = GameState.OVERWORLD;
			}
			else if (gameState == GameState.OVERWORLD){
				gameState = GameState.PAUSED;
			}
		}
		if (inputHandler.getActionJustPressed()) handleActionPressed();
		if (inputHandler.getDebugJustPressed()){
			gameState = GameState.DEBUG;
		}
	}

	/**
	 * Performs actions when player hits Action.
	 */
	private void handleActionPressed(){
		if (null != currTextbox){
			if (currTextbox.isFinished()){
				currTextbox.dispose();
				currTextbox = null;
			}
			else{
				currTextbox.complete();
			}
		}
		else if (null != currInteractable){
			currInteractable.interact();
		}
	}

	/**
	 * Loop for OVERWORLD state.
	 */
	private void updateOverworld(){
		if (newArea != null) {
			changeArea();
		}
		if (canUpdateEntities()){
			EntityHandler.update();
		}
		if (inTransition()){
			graphicsHandler.drawTransition();
		}
		else{
			graphicsHandler.drawOverworld();
		}
		if (null != currTextbox){
			currTextbox.update();
		}
	}

	/**
	 * Loop for ENCOUNTER state.
	 */
	private void updateEncounter(){
		encounterGraphicsHandler.draw(currEncounter);
		currEncounter.update();
	}

	/**
	 * Loop for PAUSED state.
	 */
	private void updatePause(){
		graphicsHandler.drawOverworld();
		graphicsHandler.drawPause();
	}
	
	/**
	 * Loop for DEBUG state.
	 */
	private void updateDebug(){
		graphicsHandler.drawDebug();
		debugMenu.update();
	}
	
	/**
	 * Prints the state of the game when the game lags significantly.
	 */
	private void printDebugOnLag(){
		System.out.println("Last frame lasted for more than a second: " + Gdx.graphics.getDeltaTime());
	}

	/**
	 * Called when transitioning from OVERWORLD to BATTLE.
	 */
	public static boolean attemptStartEncounter(String encounterID) {
		if (gameState == GameState.ENCOUNTER){
			return false;
		}
		else{
			gameState = GameState.ENCOUNTER;
			currEncounter = new Encounter(encounterID);
			encounterGraphicsHandler.begin();
			return true;
		}
	}

	/**
	 * Called when transitioning from BATTLE to OVERWORLD.
	 */
	public static void endEncounter() {
		if (gameState == GameState.ENCOUNTER) gameState = GameState.OVERWORLD;
	}

	/**
	 * Takes in a position to put the player in.
	 */
	public static void initiateAreaChange(String area_id, Vector2 position) {
		initiateAreaChangeHelper(area_id);
		newPosition.set(position);
	}
	
	/**
	 * Spawns player in map's default start location.
	 */
	public static void initiateAreaChange(String area_id){
		initiateAreaChangeHelper(area_id);
		newPosition.set(newArea.getStartLocation());
	}
	
	/**
	 * Next frame, the area will change.
	 */
	private static void initiateAreaChangeHelper(String area_id){
		newArea = new Area(area_id);
		gameState = GameState.OVERWORLD;
	}

	/**
	 * Cleans out the old area and initializes the new one.
	 */
	private static void changeArea(){
		currArea = newArea;
		newArea = null;
		player.getPosition().set(newPosition.x, (currArea.map_height) - (newPosition.y));
		player.getVelocity().setZero();
		AudioHandler.clearAudioSources();
		EntityHandler.dispose();
		EntityHandler.initializeAreaEntities(currArea);
		graphicsHandler.startArea();
		transition.reset();
	}

	/**
	 * Called when the player faints.
	 */
	public static void failure() {
		player.getPosition().set(newPosition);
		FrameEngine.initiateAreaChange("START", new Vector2(0, 0));
	}

	/**
	 * Updates which entity the player will interact with when they hit Action.
	 */
	public static void setInteractableEntity(InteractableEntity interactableEntity) {
		currInteractable = interactableEntity;
	}

	/**
	 * Starts a textbox.
	 */
	public static void setTextbox(Textbox textbox){
		currTextbox = textbox;
	}

	// GET

	public static InputHandler getInputHandler(){
		return inputHandler;
	}

	public static Area getCurrentArea(){
		return currArea;
	}

	public static Encounter getCurrentEncounter() {
		return currEncounter;
	}

	public static Player getPlayer() {
		return player;
	}

	public static float getGameSpeed(){
		if (DEBUG && inputHandler.debug_speed_up_held()) {
			return 5.0f;
		}
		else{
			return 1.0f;
		}
	}

	public static boolean canInteract(){
		return null != currInteractable;
	}

	public static boolean canUpdateEntities(){
		return transition.timeUp() && null == currTextbox;
	}

	public static boolean inTransition(){
		return !transition.timeUp();
	}

	public static Textbox getCurrentTextbox(){
		return currTextbox;
	}

	private enum GameState{
		OVERWORLD, ENCOUNTER, PAUSED, DEBUG
	}

	public static float getTime() {
		return time.getCounter();
	}

}
