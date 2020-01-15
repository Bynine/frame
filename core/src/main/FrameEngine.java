package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import area.Area;
import debug.DebugMenu;
import entity.Entity;
import entity.Footprint;
import entity.InteractableEntity;
import entity.NPC;
import entity.Player;
import entity.Player.ImageState;
import entity.Portal.Direction;
import text.MenuOption;
import text.DialogueTree;
import text.Textbox;
import timer.Timer;

public class FrameEngine extends ApplicationAdapter {
	private static boolean DEBUG = true;

	@SuppressWarnings("unused")
	public static boolean 
	DRAW 	= false	&& DEBUG,
	MUTE	= false	&& DEBUG,
	LOG		= true	&& DEBUG,
	FPS		= false && DEBUG,
	NOMAIN	= false	&& DEBUG,
	INVIS	= false	&& DEBUG,
	MAPS	= false && DEBUG,
	TREASURE= false	|| !DEBUG,
	GHOST	= false	&& DEBUG,
	SHRINE  = false && DEBUG,
	FGOAL	= false	&& DEBUG,
	FROST	= false	&& DEBUG,
	FLAME	= false	&& DEBUG,
	INV 	= false	&& DEBUG,
	ALLITEMS= false	&& DEBUG,
	CASH	= false && DEBUG,
	OMNI	= false	&& DEBUG, // Toggles whether everything appears
	SAVE	= true	|| !DEBUG,
	LETTERS = false	&& DEBUG,
	DUNGEON	= false	&& DEBUG,
	WINDOW	= true	&& DEBUG;

	private static Player player;
	private static FPSLogger fpsLogger;
	public static GameState gameState = GameState.OVERWORLD;
	private static InputHandler inputHandler;
	private static Area currArea = null;
	private static InteractableEntity currInteractable;
	private static ArrayList<DialogueTree> dialogueTrees = new ArrayList<DialogueTree>();
	static boolean inventoryRequest = false;
	private static SaveFile saveFile;
	private static ProgressionHandler progressionHandler;
	private static Inventory inventory;
	private static MainMenu mainMenu;
	private static SaveConfirmationMenu saveConfirmationMenu;
	private static ShopMenu shopMenu;
	private static PauseMenu pauseMenu;
	private static AnswersMenu answersMenu;
	private static Mailbox mailbox;
	private static ItemDescription currentThing = null;
	private static String givenItemID = "";
	private static final float TRANSITION_TIME = 45;
	private static final float TRANSITION_CHANGE_TIME = 15;
	private static final float TRANSITION_END_TIME = TRANSITION_TIME - TRANSITION_CHANGE_TIME;
	private static Timer 
	time = new Timer(0),
	cocoa = new Timer(1520),
	transition = new Timer((int)TRANSITION_TIME);
	private static ArrayList<Timer> timers = new ArrayList<Timer>(Arrays.asList(
			time, transition
			));
	private static Area newArea = null;

	public static final int TILE = 32;
	public static final Logger logger = Logger.getLogger("ERROR_LOG");
	public static DebugMenu debugMenu;
	public static GraphicsHandler graphicsHandler;
	public static boolean snailActive = true, mailToRead = false, cocoaTime = false;

	public static final Vector2 resolution = new Vector2(TILE * 18/GraphicsHandler.ZOOM, TILE * 12/GraphicsHandler.ZOOM);
	public static final Vector2 newPosition = new Vector2(TILE * 32, TILE * 32);
	public static Direction newDirection = Direction.ANY;
	public static float elapsedTime = 0;
	public static int testCounter = 0;

	@Override
	public void create() {
		player = new Player(0, 0);
		logger.setLevel(Level.ALL);

		inventory = new Inventory();
		saveFile = new SaveFile(LOG);
		newPosition.set(saveFile.startPosition);
		mainMenu = new MainMenu(saveFile.exists());
		saveConfirmationMenu = new SaveConfirmationMenu();
		shopMenu = new ShopMenu();
		pauseMenu = new PauseMenu();
		mailbox = new Mailbox();
		fpsLogger = new FPSLogger();
		debugMenu = new DebugMenu();
		AudioHandler.initialize();

		graphicsHandler = new GraphicsHandler();
		inputHandler = new KeyboardInputHandler();
		progressionHandler = new ProgressionHandler();
		inputHandler.initialize();
		if (!NOMAIN) {
			startMainMenu(saveFile.exists());
		}
		else {
			continueGame();
			saveFile.setRandomFlags();
		}
	}

	@Override
	public void render() {
		elapsedTime = 60.0f * getGameSpeed() * (Gdx.graphics.getDeltaTime());
		// Don't compensate for less than 6 FPS. At that point, well...
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
		if (CASH && getTime() % 60 == 0){
			saveFile.addMoney(1);
		}

		AudioHandler.update();
		graphicsHandler.update();
		assessInputs();
		currInteractable = null;

		try{
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
			case SAVECONFIRM:{
				updateSaveConfirm();
			} break;
			case MAP:{
				updateMap();
			} break;
			case MAILBOX:{
				updateMailbox();
			}
			}
		}
		catch (Exception e){
			logger.warning(e.getClass().toString());
			logger.warning(e.getMessage());
			logger.warning(Arrays.toString(e.getStackTrace()));
		}

		if (currArea != null) graphicsHandler.drawOverlay();

		inputHandler.update();
		if (FPS) fpsLogger.log();
	}

	private void updateOverworld(){
		if (newArea != null && !transitionStart()) {
			changeArea();
		}
		if (canUpdateEntities()){
			progressionHandler.update();
			EntityHandler.update();
			if (canControlPlayer()) {
				cocoa.countUp();
			}
			if (cocoa.timeUp() && cocoaTime) {
				endCocoaTime();
			}
		}
		else{
			EntityHandler.updateImages();
		}
		if (!transitionStart() && !transitionEnd() && inTransition()){
			graphicsHandler.drawTransition();
		}
		else{
			graphicsHandler.drawOverworld();
		}
		if (null != answersMenu){
			graphicsHandler.drawMenu(answersMenu);
			answersMenu.update();
		}
		if (null != getDialogueTree()){
			if (getDialogueTree().terminated() || null == getDialogueTree().getTextbox()){
				endDialogueTree();
			}
			else{
				getDialogueTree().getTextbox().update();
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
		graphicsHandler.drawTitle();
		if (currArea.id.equals("PATH")) {
			playerWalk(Walk.RIGHT);
		}
		graphicsHandler.drawMenu(mainMenu);
		if (mailToRead) {
			graphicsHandler.drawGotMail();
		}
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

	private static final int CREDITS_END_TIME = 7400;
	private static final int CREDITS_END_TIME_EARLY = CREDITS_END_TIME - 700;
	
	public static int getCreditsTime(){
		return saveFile.getFlag("FOUND_GOAL") ? CREDITS_END_TIME : CREDITS_END_TIME_EARLY;
	}

	private void updateCredits(){
		graphicsHandler.drawCredits();
		playerWalk(Walk.LEFT);
		if (time.getCounter() > getCreditsTime()){
			startMainMenu(true);
		}
	}

	private void updateSaveConfirm(){
		graphicsHandler.drawTitle();
		playerWalk(Walk.NONE);
		graphicsHandler.drawMenu(saveConfirmationMenu);
		saveConfirmationMenu.update();
	}
	
	private void updateMap() {
		graphicsHandler.drawOverworld();
		graphicsHandler.drawMap();
	}
	
	private void updateMailbox() {
		graphicsHandler.drawTitle();
		playerWalk(Walk.NONE);
		graphicsHandler.drawMailbox(mailbox);
		mailbox.update();
	}

	static float positionX = 0;
	static final float positionY = TILE * 4.5f;

	private void playerWalk(Walk walk){
		EntityHandler.update();
		switch(walk){
		case LEFT: {
			positionX = (3*currArea.mapWidth/4) - ((2 * time.getCounter()) % currArea.mapWidth/2);
		} break;
		case RIGHT: {
			positionX = currArea.mapWidth/4 + ((2 * time.getCounter()) % currArea.mapWidth/2);
		} break;
		case NONE: {
			time.countDown();
		}
		}

		final Vector2 prev = player.getPosition();

		player.getPosition().set(positionX, positionY);
		for (Entity en: EntityHandler.getEntities()) {
			if (en instanceof Footprint) {
				en.getPosition().set(
						positionX + (en.getPosition().x - prev.x), 
						positionY + (en.getPosition().y - prev.y));
				if (en.getPosition().dst(player.getPosition()) > 1200) {
					System.out.println(en.getPosition().sub(player.getPosition()));
				}
			}
		}
	}

	enum Walk{
		LEFT, RIGHT, NONE
	}

	/**
	 * Checks inputs from input handler and performs necessary actions.
	 */
	private void assessInputs(){
		if (inputHandler.getPauseJustPressed()){
			if (gameState == GameState.PAUSED){
				gameState = GameState.OVERWORLD;
				AudioHandler.playSoundVariedPitch(AbstractMenu.stopCursor);
			}
			else if (inventoryRequest){
				inventoryRequest = false;
				endDialogueTree();
				gameState = GameState.OVERWORLD;
			}
			else if (gameState == GameState.INVENTORY){
				gameState = GameState.PAUSED;
				AudioHandler.playSoundVariedPitch(AbstractMenu.stopCursor);
			}
			else if (gameState == GameState.SHOP){
				endShop();
				AudioHandler.playSoundVariedPitch(AbstractMenu.stopCursor);
			}
			else if (gameState == GameState.OVERWORLD && canControlPlayer()){
				if (!player.canPause()) {
					AudioHandler.playSoundVariedPitch(AbstractMenu.error);
				}
				else {
					handlePause();
					AudioHandler.playSoundVariedPitch(AbstractMenu.moveCursor);
				}
			}
			else if (gameState == GameState.MAP) {
				gameState = GameState.INVENTORY;
				AudioHandler.playSoundVariedPitch(AbstractMenu.closeMap);
			}
			else if (gameState == GameState.MAILBOX) {
				Player.setImageState(ImageState.NORMAL);
				gameState = GameState.MAIN;
				AudioHandler.playSoundVariedPitch(AbstractMenu.stopCursor);
			}
		}
		if (inputHandler.getActionJustPressed()) {
			handleActionPressed();
		}
		if (DEBUG && inputHandler.getDebugJustPressed()){
			gameState = GameState.DEBUG;
		}
		if (inputHandler.getPlusPressed()) {
			testCounter++;
			System.out.println(testCounter);
		}
		if (inputHandler.getMinusPressed()) {
			testCounter--;
			System.out.println(testCounter);
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
		if (null != getDialogueTree()){
			Textbox textbox = getDialogueTree().getTextbox();
			if (textbox.isFinished()){
				if (null != answersMenu){
					getDialogueTree().handleAnswer(answersMenu.getActiveButton().getOutput().toString());
					answersMenu = null;
				}
				else if (inventoryRequest){
					getDialogueTree().handleItemChoice((ItemDescription)inventory.getActiveButton().getOutput());
					inventoryRequest = false;
				}
				else if (getDialogueTree().finished()){
					Player.setImageState(Player.ImageState.NORMAL);
					getDialogueTree().messageSpeaker();
					endDialogueTree();
				}
				else{
					Player.setImageState(Player.ImageState.NORMAL);
					getDialogueTree().advanceBranch();
				}
			}
			else{
//				if (DEBUG && inputHandler.getDebugSpeedUpHeld()){
//					textbox.complete();
//				}
			}
		}
		else if (canControlPlayer() && null != currInteractable){
			player.getVelocity().setZero();
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
	public static void initiateAreaChange(String area_id, Vector2 position, Direction direction) {
		initiateAreaChangeHelper(area_id);
		newDirection = direction;
		newPosition.set(position);
	}

	/**
	 * Spawns player in map's default start location.
	 */
	public static void debugAreaChange(String area_id){
		initiateAreaChangeHelper(area_id);
		newPosition.set(newArea.getDefaultLocation());
	}

	/**
	 * Next frame, the area will change.
	 */
	private static void initiateAreaChangeHelper(String area_id){
		newArea = new Area(area_id);
		gameState = GameState.OVERWORLD;
		transition.reset();
	}

	/**
	 * Cleans out the old area and initializes the new one.
	 */
	private static void changeArea(){
		player.getVelocity().setZero();
		if (null != currArea) currArea.dispose();
		currArea = newArea;
		newArea = null;
		QuestionHandler.changeArea(currArea);
		AudioHandler.startNewAudio(currArea.music);
		setNewPlayerPosition();
		EntityHandler.dispose();
		AudioHandler.clearAudioSources();
		EntityHandler.initializeAreaEntities(currArea);
		graphicsHandler.startArea();
		time.reset();
		player.setDirection(newDirection);
		player.reset();
		newDirection = Direction.ANY;
		if (inventory.hasItem("SNAIL") && snailActive){
			activateSnail();
		}
	}

	@SuppressWarnings("serial")
	private static void activateSnail(){
		int numSecrets = EntityHandler.getNumSecrets();
		if (numSecrets > 0){
			startDialogueTree(
					new DialogueTree(new NPC("SNAIL", ""),
							"snail", new HashMap<String, String>(){{
								put("ARE_IS", numSecrets != 1 ? "are" : "is");
								put("NUM_SECRETS", Integer.toString(numSecrets));
								put("DO_THE_MARIO", numSecrets != 1 ? "s" : "");
							}})
					);
		}
	}

	private static void setNewPlayerPosition(){
		player.getPosition().set(newPosition.x, (currArea.mapHeight) - (newPosition.y));
		player.update(); // To avoid triggering portals twice
	}

	/**
	 * Begin a new game.
	 */
	static void newGame(){
		inventory.wipe();
		saveFile.wipeSave();
		mainMenuToGame();
		inventory.addItem("KEEPSAKE");
	}

	/**
	 * Continues from established save file.
	 */
	static void continueGame(){
		mainMenuToGame();
	}

	private static void mainMenuToGame(){
		saveFile = new SaveFile(LOG);
		final int walkDist = 18;
		if (saveFile.getFlag(SaveFile.CREDITS)){
			saveFile.setFlag("CREDITS", false);
			player.walkRight(walkDist);
			if (saveFile.getFlag("FOUND_GOAL") && !saveFile.getFlag("START_FROST")) {
				saveFile.setFlag("START_FROST");
				saveFile.startPosition.set(0.5f, 26f);
				saveFile.startArea = "FROST_FOREST";
				player.walkRight(walkDist);
				if (!inventory.hasItem("MAP")) {
					inventory.addItem("MAP");
					saveFile.setFlag("SHOP_MAP");
				}
			}
		}
		if (!saveFile.exists()){
			saveFile.startPosition.set(0.5f, 26f);
			saveFile.startArea = FROST ? "FROST_FOREST" : "FOREST";
			player.walkRight(walkDist);
		}
		gameState = GameState.OVERWORLD;
		newArea = new Area(saveFile.startArea);
		newPosition.set(saveFile.startPosition.scl(TILE));
		if (LOG) System.out.println("Start position is: " + newPosition);
		changeArea();
		if (player.isColliding()){
			logger.warning("Player was spawned inside collision at: " + newPosition.toString());
			newPosition.set(currArea.getDefaultLocation());
			setNewPlayerPosition();
		}
		if (!DEBUG) {
			transition.reset();
		}
	}

	public static void startMainMenu(boolean saveFileExists) {
		if (saveFileExists) {
			mainMenu = new MainMenu(true);
			mainMenu.open();
		}
		if (!noWalkingMainMenu()){
			gameState = GameState.MAIN;
			String newAreaId = "PATH"; //saveFile.exists() ? "VOID" : "PATH";
			newArea = new Area(newAreaId);
			if (!SAVE){
				newPosition.set(61 * TILE, 41 * TILE);
			}
			else{
				newPosition.set(0, 25.5f * TILE);
			}
			changeArea();
		}
		else{
			gameState = GameState.MAIN;
		}
		//cocoaCalamity();
	}
	
	public static void cocoaCalamity() {
		if (!getInventory().removeItem("COCOAHOT")) {
			logger.log(Level.SEVERE, "COCOA COULDN'T BE FOUND TO REMOVE!?");
		}
		getInventory().addItem("COCOACOLD");
		cocoa.end();
	}

	public static void startSaveConfirmationMenu() {
		gameState = GameState.SAVECONFIRM;
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
		dialogueTrees.clear();
		time.reset();
		saveFile.save(true);
		newArea = new Area("PATH");
		changeArea();
		if (saveFile.getFlag("GOT_ALL_TREASURES")) {
			saveFile.setFlag("FROST_ACTIVE");
		}
		gameState = GameState.CREDITS;
		AudioHandler.startNewAudio("music/johnnoodle.ogg", false);
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
		startDialogueTree(new DialogueTree((textbox)));
	}

	/**
	 * Runs through Dialogue until finished.
	 */
	public static void startDialogueTree(DialogueTree dialogue) {
		if (LOG) logger.info("Added dialogue tree " + dialogue.getTextbox().getAllText());
		dialogueTrees.add(dialogue);
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
			dialogueTrees.clear();
			dialogueTrees.add(0, new DialogueTree("But you don't have anything on you!"));
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
		getDialogueTree().handleAnswer(string);
	}

	public static void endDialogueTree() {
		dialogueTrees.remove(0);
	}
	
	public static void showMap() {
		gameState = GameState.MAP;
	}

	public static void checkMail() {
		mailbox.open();
		Player.setImageState(ImageState.READ);
		gameState = GameState.MAILBOX;
	}

	// GET

	public static Vector2 playerInput() {
		if (gameState == GameState.CREDITS){
			return new Vector2(-1, 0);
		}
		else if (gameState == GameState.MAIN){
			return new Vector2(1, 0);
		}
		else if (noWalkingMainMenu()){
			return Vector2.Zero;
		}
		return null;
	}
	
	private static boolean noWalkingMainMenu() {
		return gameState == GameState.SAVECONFIRM || gameState == GameState.MAILBOX;
	}

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
			return 8.5f;
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
		return null == getCurrentTextbox() && canUpdateEntities() && player.canControl();
	}

	public static boolean inTransition(){
		return !transition.timeUp();
	}

	public static Textbox getCurrentTextbox(){
		if (null == getDialogueTree()) return null;
		return getDialogueTree().getTextbox();
	}

	public static DialogueTree getDialogueTree(){
		if (dialogueTrees.isEmpty()) return null;
		else return dialogueTrees.get(0);
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

	public enum GameState{
		OVERWORLD, PAUSED, DEBUG, MAIN, SHOP, INVENTORY, CREDITS, SAVECONFIRM, MAP, MAILBOX
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

	public static boolean transitionStart(){
		return !transition.timeUp() && 
				(transition.getCounter() < TRANSITION_CHANGE_TIME);
	}

	public static boolean transitionEnd(){
		return !transition.timeUp() && 
				((transition.getEndTime() - transition.getCounter()) < TRANSITION_CHANGE_TIME);
	}

	public static int transitionTime(){
		return transition.getCounter();
	}

	public static float getTransitionMod(){
		if (transitionStart()){
			return 1f - transitionTime() / TRANSITION_CHANGE_TIME;
		}
		else if (transitionEnd()){
			return (transitionTime() - TRANSITION_END_TIME)/TRANSITION_CHANGE_TIME;
		}
		return 0;
	}

	public static boolean canSelectMenuItem() {
		return gameState == GameState.INVENTORY;
	}
	
	public static void startCocoaTimer() {
		cocoaTime = true;
		cocoa.reset();
	}
	
	public static void endCocoaTime() {
		cocoaTime = false;
		if (inventory.hasItem("COCOAHOT")) {
			startDialogueTree(new DialogueTree(null, "cocoa_cold"));
		}
	}

	public static void coffeeBoost() {
		gameState = GameState.OVERWORLD;
		player.coffeeBoost();
	}

	public static boolean isCocoaTime() {
		return cocoaTime && gameState == GameState.OVERWORLD;
	}

}
