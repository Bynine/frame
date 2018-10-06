package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
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
import text.MenuOption;
import text.DialogueTree;
import text.Textbox;
import timer.Timer;

public class FrameEngine extends ApplicationAdapter {
	public static boolean DEBUG	= true;

	@SuppressWarnings("unused")
	public static boolean 
	DRAW 	= false	&& DEBUG,
	MUTE	= true	&& DEBUG,
	LOG		= false	&& DEBUG,
	NOMAIN	= true	&& DEBUG,
	INVIS	= false	&& DEBUG,
	MAPS	= false && DEBUG,
	SAVE	= false	|| !DEBUG;

	public static String startAreaName = DEBUG ? "FOREST" : "FOREST";
	private static Player player;
	private static FPSLogger fpsLogger;
	private static GameState gameState = GameState.OVERWORLD;
	private static InputHandler inputHandler;
	private static Area currArea = null;
	private static InteractableEntity currInteractable;
	private static DialogueTree dialogueTree = null;
	private static boolean inventoryRequest = false;
	private static SaveFile saveFile;
	private static ProgressionHandler progressionHandler;
	private static Inventory inventory;
	private static MainMenu mainMenu;
	private static ShopMenu shopMenu;
	private static PauseMenu pauseMenu;
	private static AnswersMenu answersMenu;
	private static ItemDescription currentThing = null;
	private static String givenItemID = "";
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
		logger.setLevel(Level.ALL);

		inventory = new Inventory();
		saveFile = new SaveFile(LOG);
		mainMenu = new MainMenu();
		shopMenu = new ShopMenu();
		pauseMenu = new PauseMenu();
		fpsLogger = new FPSLogger();
		debugMenu = new DebugMenu();
		AudioHandler.initialize();

		graphicsHandler = new GraphicsHandler();
		inputHandler = new KeyboardInputHandler();
		progressionHandler = new ProgressionHandler();
		inputHandler.initialize();
		if (!NOMAIN) {
			startMainMenu();
		}
		else {
			continueGame();
		}
		//gameState = GameState.CREDITS;
	}

	@Override
	public void render() {
		elapsedTime = 60.0f * getGameSpeed() * (Gdx.graphics.getDeltaTime());
		// Don't compensate for less than 6 FPS
		final float maxTime = 10;
		if (elapsedTime/getGameSpeed() > maxTime) {
			elapsedTime = maxTime;
		}
		if (Gdx.graphics.getDeltaTime() > 1) {
			printDebugOnLag();
		}
		for (Timer timer: timers){
			timer.countUp();
		}

		AudioHandler.update();
		graphicsHandler.update();
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
		case SHOP:{
			updateShop();
		} break;
		case INVENTORY: {
			updateInventory();
		} break;
		case CREDITS:{
			updateCredits();
		} break;
		}
		
		if (currArea != null) graphicsHandler.drawOverlay();

		inputHandler.update();
		if (LOG) fpsLogger.log();
		if (MAPS && getTime() > 6000){
			time.reset();
			if (newArea == null) newArea = new Area("BEACH");
			initiateAreaChange(newArea.getID().equals("FOREST") ? "BEACH" : "FOREST", new Vector2(8, 8));
			changeArea();
		}
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
		if (null != answersMenu){
			graphicsHandler.drawMenu(answersMenu);
			answersMenu.update();
		}
		if (null != dialogueTree){
			if (dialogueTree.terminated() || null == dialogueTree.getTextbox()){
				dialogueTree = null;
			}
			else{
				dialogueTree.getTextbox().update();
			}
		}
		if (inventoryRequest){
			graphicsHandler.drawItems(inventory, false);
			inventory.update();
		}
	}

	private void updatePause(){
		graphicsHandler.drawOverworld();
		graphicsHandler.drawMenu(pauseMenu);
		pauseMenu.update();
	}

	private void updateDebug(){
		graphicsHandler.drawDebug();
		graphicsHandler.drawMenu(debugMenu);
		debugMenu.update();
	}

	private void updateMain(){
		graphicsHandler.drawMainMenu();
		graphicsHandler.drawMenu(mainMenu);
		mainMenu.update();
	}
	
	private void updateShop(){
		graphicsHandler.drawOverworld();
		graphicsHandler.drawItems(shopMenu, true);
		shopMenu.update();
	}
	
	private void updateInventory(){
		graphicsHandler.drawOverworld();
		graphicsHandler.drawItems(inventory, false);
		inventory.update();
	}
	
	private void updateCredits(){
		graphicsHandler.drawCredits();
		if (time.getCounter() > 2600){
			startMainMenu();
		}
	}

	/**
	 * Checks inputs from input handler and performs necessary actions.
	 */
	private void assessInputs(){
		if (inputHandler.getPauseJustPressed()){
			if (gameState == GameState.PAUSED){
				gameState = GameState.OVERWORLD;
				AudioHandler.playSound(AbstractMenu.stopCursor);
			}
			else if (gameState == GameState.INVENTORY){
				gameState = GameState.PAUSED;
				AudioHandler.playSound(AbstractMenu.stopCursor);
			}
			else if (gameState == GameState.SHOP){
				endShop();
				AudioHandler.playSound(AbstractMenu.stopCursor);
			}
			else if (gameState == GameState.OVERWORLD && canControlPlayer()){
				handlePause();
				AudioHandler.playSound(AbstractMenu.moveCursor);
			}
		}
		if (inputHandler.getActionJustPressed()) {
			handleActionPressed();
		}
		if (inputHandler.getDebugJustPressed()){ // TODO: Disable for final release
			gameState = GameState.DEBUG;
		}
	}

	private void handlePause(){
		pauseMenu.open();
		gameState = GameState.PAUSED;
	}

	/**
	 * Performs actions when player hits Action.
	 */
	private void handleActionPressed(){
		if (null != dialogueTree){
			Textbox textbox = dialogueTree.getTextbox();
			if (textbox.isFinished()){
				if (null != answersMenu){
					dialogueTree.handleAnswer(answersMenu.getActiveButton().getOutput().toString());
					answersMenu = null;
				}
				else if (inventoryRequest){
					dialogueTree.handleItemChoice((ItemDescription)inventory.getActiveButton().getOutput());
					inventoryRequest = false;
				}
				else if (dialogueTree.finished()){
					Player.setImageState(Player.ImageState.NORMAL);
					dialogueTree.messageSpeaker();
					dialogueTree = null;
				}
				else{
					Player.setImageState(Player.ImageState.NORMAL);
					dialogueTree.advanceBranch();
				}
			}
			else{
				//textbox.complete();
			}
		}
		else if (canControlPlayer() && null != currInteractable){
			currInteractable.interact();
		}
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
		time.reset();
	}

	/**
	 * Begin a new game.
	 */
	static void newGame(){
		saveFile.wipeSave();
		inventory.addItem("KEEPSAKE");
		player.walkRight(18);
		contGame();
	}

	/**
	 * Continues from established save file.
	 */
	static void continueGame(){
		contGame();
	}
	
	private static void contGame(){
		newArea = new Area(startAreaName);
		newPosition.set(newArea.getStartLocation());
		changeArea();
		gameState = GameState.OVERWORLD;
		transition.reset();
	}
	
	public static void startMainMenu() {
		AudioHandler.startNewAudio("music/forest.ogg");
		gameState = GameState.MAIN;
	}
	
	public static void startInventory(){
		inventory.open();
		gameState = GameState.INVENTORY;
	}

	public static void startShop() {
		endDialogueTree();
		shopMenu.open();
		gameState = GameState.SHOP;
	}
	
	public static void endShop() {
		gameState = GameState.OVERWORLD;
	}

	public static void startCredits(){
		time.reset();
		saveFile.save();
		gameState = GameState.CREDITS;
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
		answersMenu = new AnswersMenu(options);
	}

	/**
	 * Pulls up inventory screen to request an item.
	 */
	public static void setInventoryRequest(String string) {
		inventory.open();
		if (inventory.getList().isEmpty()){
			dialogueTree = new DialogueTree("But you don't have anything to give!");
		}
		else{
			ArrayMap<String, String> options = new ArrayMap<>();
			for (MenuOption button: inventory.getList()){
				ItemDescription desc = (ItemDescription)button.getOutput();
				options.put(desc.name, desc.id);
			}
			inventoryRequest = true;
		}
	}

	/**
	 * Redirects dialogue tree to a different branch.
	 */
	public static void setRedirect(String string) {
		dialogueTree.handleAnswer(string);
	}
	
	public static void endDialogueTree() {
		dialogueTree = null;
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
	
	public static InteractableEntity getCurrentInteractable(){
		return currInteractable;
	}

	public static boolean canInteract(){
		return null != currInteractable;
	}

	public static boolean canUpdateEntities(){
		return transition.timeUp();
	}
	
	public static boolean canControlPlayer(){
		return null == getCurrentTextbox() && canUpdateEntities();
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

	public static ShopMenu getShopMenu() {
		return shopMenu;
	}

	private enum GameState{
		OVERWORLD, PAUSED, DEBUG, MAIN, SHOP, INVENTORY, CREDITS
	}
	
	public static void setGivenItemID(String newItemID){
		givenItemID = newItemID;
	}

	public static String getGivenItemID() {
		return givenItemID;
	}
	
	public static void setCurrentThing(String thing){
		if (null != currentThing) currentThing.dispose();
		currentThing = new ItemDescription(thing);
		
	}
	
	public static ItemDescription getCurrentThing(){
		return currentThing;
	}

}
