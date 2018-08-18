package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import area.Area;
import debug.DebugMenu;
import entity.InteractableEntity;
import entity.Player;
import text.ButtonContainer;
import text.DialogueTree;
import text.Textbox;

public class FrameEngine extends ApplicationAdapter {
	public static boolean DEBUG	= true;

	@SuppressWarnings("unused")
	public static boolean 
	DRAW 	= false	&& DEBUG,
	MUTE	= true	&& DEBUG,
	LOG		= false	&& DEBUG,
	SAVE	= true	&& DEBUG;

	public static String startAreaName = "BEACH";
	private static Player player;
	private static FPSLogger fpsLogger;
	private static GameState gameState = GameState.MAIN;
	private static InputHandler inputHandler;
	private static Area currArea = null;
	private static InteractableEntity currInteractable;
	private static DialogueTree dialogueTree = null;
	private static ButtonContainer buttonContainer = null;
	private static SaveFile saveFile;
	private static ProgressionHandler progressionHandler;
	private static Inventory inventory;
	private static MainMenu mainMenu;
	private static Timer 
	time = new Timer(0),
	transition = new Timer(20);
	private static ArrayList<Timer> timers = new ArrayList<Timer>(Arrays.asList(
			time, transition
			));
	private static Area newArea = null;
	private static Vector2 newPosition = new Vector2();

	public static final int TILE = 32;
	public static final Logger logger = Logger.getLogger("ERROR_LOG");
	public static DebugMenu debugMenu;
	public static GraphicsHandler graphicsHandler;

	public static final Vector2 resolution = new Vector2(TILE * 36, TILE * 24);
	public static float elapsedTime = 0;

	@Override
	public void create() {
		player = new Player(0, 0);
		
		inventory = new Inventory();
		saveFile = new SaveFile();
		mainMenu = new MainMenu();
		fpsLogger = new FPSLogger();
		debugMenu = new DebugMenu();
		AudioHandler.initialize();
		
		newArea = new Area(startAreaName);
		newPosition.set(newArea.getStartLocation());

		graphicsHandler = new GraphicsHandler();
		inputHandler = new KeyboardInputHandler();
		progressionHandler = new ProgressionHandler();
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
		case PAUSED:{
			updatePause();
		} break;
		case DEBUG:{
			updateDebug();
		} break;
		case MAIN:{
			updateMain();
		} break;
		}

		inputHandler.update();
		if (LOG) fpsLogger.log();
	}
	
	private void updateOverworld(){
		if (newArea != null) {
			changeArea();
		}
		if (canUpdateEntities()){
			EntityHandler.update();
			progressionHandler.update();
		}
		else{
			EntityHandler.updateImages();
		}
		if (inTransition()){
			graphicsHandler.drawTransition();
		}
		else{
			graphicsHandler.drawOverworld();
		}
		if (null != buttonContainer){
			buttonContainer.update();
		}
		if (null != dialogueTree){
			dialogueTree.getTextbox().update();
		}
	}

	private void updatePause(){
		graphicsHandler.drawOverworld();
		graphicsHandler.drawPause();
		inventory.update();
	}

	private void updateDebug(){
		graphicsHandler.drawDebug();
		debugMenu.update();
	}
	
	private void updateMain(){
		graphicsHandler.drawMainMenu();
		mainMenu.update();
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
				handlePause();
			}
		}
		if (inputHandler.getActionJustPressed()) {
			handleActionPressed();
		}
		if (inputHandler.getSaveJustPressed()){
			handleSavePressed();
		}
		if (DEBUG && inputHandler.getDebugJustPressed()){
			gameState = GameState.DEBUG;
		}
	}
	
	private void handlePause(){
		gameState = GameState.PAUSED;
		inventory.open();
	}

	/**
	 * Performs actions when player hits Action.
	 */
	private void handleActionPressed(){
		if (null != dialogueTree){
			Textbox textbox = dialogueTree.getTextbox();
			if (textbox.isFinished()){
				if (null != buttonContainer){
					dialogueTree.handleAnswer(buttonContainer.getChoice());
					buttonContainer = null;
				}
				else if (dialogueTree.finished()){
					dialogueTree = null;
				}
				else{
					dialogueTree.advanceBranch();
				}
			}
			else{
				textbox.complete();
			}
		}
		else if (null != currInteractable){
			currInteractable.interact();
		}
	}
	
	private void handleSavePressed(){
		saveFile.save();
	}

	/**
	 * Prints the state of the game when the game lags significantly.
	 */
	private void printDebugOnLag(){
		System.out.println("Last frame lasted for more than a second: " + Gdx.graphics.getDeltaTime());
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
		if (null != currArea) currArea.dispose();
		currArea = newArea;
		newArea = null;
		player.getPosition().set(newPosition.x, (currArea.mapHeight) - (newPosition.y));
		player.getVelocity().setZero();
		EntityHandler.dispose();
		AudioHandler.clearAudioSources();
		EntityHandler.initializeAreaEntities(currArea);
		graphicsHandler.startArea();
		transition.reset();
	}
	
	/**
	 * Begin a new game.
	 */
	static void newGame(){
		saveFile.wipeSave();
		gameState = GameState.OVERWORLD;
	}
	
	/**
	 * Continues from established save file.
	 */
	static void continueGame(){
		gameState = GameState.OVERWORLD;
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
	public static void putTextbox(Textbox textbox){
		dialogueTree = new DialogueTree((textbox));
	}

	/**
	 * Runs through Dialogue until finished.
	 */
	public static void startDialogueTree(DialogueTree dialogue) {
		dialogueTree = dialogue;
	}

	/**
	 * Establishes a button container with answers to a question.
	 */
	public static void setQuestion(String[] strings) {
		ArrayMap<String, String> options = new ArrayMap<>();
		for (String string: strings){
			options.put(string, string);
		}
		buttonContainer = new ButtonContainer(new Vector2(5, 2), options);
	}

	/**
	 * Pulls up inventory screen to request an item.
	 */
	public static void setInventoryRequest(String string) {
		inventory.open();
		ArrayMap<String, String> options = new ArrayMap<>();
		for (ItemDescription desc: inventory.getDescriptions()){
			options.put(desc.name, desc.id);
		}
		buttonContainer = new ButtonContainer(new Vector2(5, 2), options);
	}
	
	/**
	 * Redirects dialogue tree to a different branch.
	 */
	public static void setRedirect(String string) {
		dialogueTree.handleAnswer(string);
	}

	// GET

	public static InputHandler getInputHandler(){
		return inputHandler;
	}

	public static Area getArea(){
		return currArea;
	}

	public static Player getPlayer() {
		return player;
	}

	public static float getGameSpeed(){
		if (DEBUG && inputHandler.getDebugSpeedUpHeld()) {
			return 8.0f;
		}
		else{
			return 1.0f;
		}
	}

	public static boolean canInteract(){
		return null != currInteractable;
	}

	public static boolean canUpdateEntities(){
		return null == getCurrentTextbox() && transition.timeUp();
	}

	public static boolean inTransition(){
		return !transition.timeUp();
	}

	public static Textbox getCurrentTextbox(){
		if (null == dialogueTree) return null;
		return dialogueTree.getTextbox();
	}

	public static float getTime() {
		return time.getCounter();
	}

	public static ButtonContainer getButtonContainer() {
		return buttonContainer;
	}

	public static SaveFile getSaveFile() {
		return saveFile;
	}

	public static ProgressionHandler getProgressionHandler() {
		return progressionHandler;
	}

	public static Inventory getInventory() {
		return inventory;
	}
	
	public static MainMenu getMainMenu(){
		return mainMenu;
	}
	
	private enum GameState{
		OVERWORLD, PAUSED, DEBUG, MAIN
	}

}
