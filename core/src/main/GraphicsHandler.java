package main;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entity.Critter;
import entity.Emitter;
import entity.Entity;
import entity.InteractableEntity;
import entity.NPC;
import entity.Player;
import entity.Emitter.Graphic;
import text.Button;
import text.ButtonContainer;
import text.Textbox;

/**
 * Handles drawing overworld.
 */
public class GraphicsHandler {
	public static final Color DEFAULT_COLOR = new Color(1, 1, 1, 1);

	protected SpriteBatch batch;
	protected final OrthographicCamera ow_cam = new OrthographicCamera();
	protected final OrthographicCamera center_cam = new OrthographicCamera();
	protected OrthogonalTiledMapRenderer renderer;
	protected BitmapFont font, debugFont;
	protected ShapeRenderer shapeRenderer;
	public static final float ZOOM = 1.0f/2.0f;
	private final EntityDepthSorter sorter = new EntityDepthSorter();
	protected Color wipeColor = new Color(0.05f, 0.07f, 0.12f, 1);
	private static final TextureRegion 
	interactBubble = new TextureRegion(new Texture("sprites/player/interact.png")),
	talkingBubble = new TextureRegion(new Texture("sprites/player/talk.png")),
	textboxOverlay = new TextureRegion(new Texture("sprites/gui/textbox_overlay.png")),
	textboxCorner = new TextureRegion(new Texture("sprites/gui/textbox_corner.png")),
	textboxTop = new TextureRegion(new Texture("sprites/gui/textbox_top.png")),
	textboxSide = new TextureRegion(new Texture("sprites/gui/textbox_side.png")),
	textboxCenter = new TextureRegion(new Texture("sprites/gui/textbox_center.png"));

	private static TextureRegion overlay = new TextureRegion(new Texture("sprites/gui/watercolor_dim.png"));
	private static TextureRegion water = new TextureRegion(new Texture("sprites/graphics/water.png"));

	GraphicsHandler(){
		batch = new SpriteBatch();
		ow_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ow_cam.zoom = ZOOM;
		center_cam.zoom = ZOOM;
		center_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lato.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;
		parameter.spaceY = 2;
		parameter.color = new Color(
				26.0f/255.0f, 
				39.0f/255.0f, 
				39.0f/255.0f, 
				1);
		parameter.mono = true;
		font = generator.generateFont(parameter);
		parameter.color = new Color(1, 1, 1, 1);
		debugFont = generator.generateFont(parameter);
		generator.dispose();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
	}

	/**
	 * Sets the renderer to draw the current map.
	 */
	void startArea(){
		renderer = new OrthogonalTiledMapRenderer(FrameEngine.getArea().getMap());
		overlay = new TextureRegion(new Texture(
				"sprites/gui/watercolor_" + FrameEngine.getArea().overlayString + ".png"));
	}

	/**
	 * Draws all overworld graphics.
	 */
	void drawOverworld(){
		wipeScreen();
		ArrayList<Entity> normalEntities = new ArrayList<Entity>();
		ArrayList<Entity> frontEntities = new ArrayList<Entity>();
		for (Entity entity: EntityHandler.getEntities()){
			if (entity.getLayer() == Entity.Layer.NORMAL) normalEntities.add(entity);
			if (entity.getLayer() == Entity.Layer.FRONT) frontEntities.add(entity);
		}
		if (FrameEngine.getArea().cameraFixed){
			updateCameraFixed();
		}
		else{
			updateOverworldCam();
		}
		renderer.setView(ow_cam);
		batch.setProjectionMatrix(ow_cam.combined);

		batch.setColor(DEFAULT_COLOR);
		drawWater();
		renderer.render(new int[]{0, 1});	// Render background tiles.

		drawShadows(EntityHandler.getEntities());
		drawEntities(normalEntities);
		renderer.render(new int[]{2, 3}); 	// Render foreground tiles.
		drawEntities(frontEntities);
		handleEmitters();

		if (FrameEngine.canUpdateEntities() && FrameEngine.canInteract()) {
			drawInteractBubble();
		}
		else if(null != FrameEngine.getCurrentTextbox()
				&& FrameEngine.getCurrentTextbox().playerTalking()){
			drawTalkingBubble();
		}
		
		drawOverlay();
		
		if (null != FrameEngine.getCurrentTextbox()) {
			drawDefaultTextbox(FrameEngine.getCurrentTextbox());
		}
		if (null != FrameEngine.getButtonContainer()){
			drawButtons(FrameEngine.getButtonContainer());
		}

		if (FrameEngine.DRAW){
			shapeRenderer.setProjectionMatrix(ow_cam.combined);
			shapeRenderer.begin();
			Rectangle interactionBox = FrameEngine.getPlayer().getInteractionBox();
			shapeRenderer.rect(
					interactionBox.x, interactionBox.y, interactionBox.width, interactionBox.height
					);
			shapeRenderer.end();
		}
	}

	/**
	 * Draws the transition between maps.
	 */
	void drawTransition(){
		wipeScreen();
		drawOverlay();
	}

	private void drawWater(){
		batch.begin();
		drawByTiles(water, true);
		batch.draw(Player.ripple,
				FrameEngine.getPlayer().getPosition().x,
				FrameEngine.getPlayer().getPosition().y
				);
		batch.end();
	}

	/**
	 * Draws the bubble above the player's head that shows they can interact with something.
	 */
	private void drawInteractBubble(){
		batch.begin();
		batch.draw(interactBubble,
				FrameEngine.getPlayer().getPosition().x,
				FrameEngine.getPlayer().getPosition().y
				);
		batch.end();
	}
	
	/**
	 * Draws the bubble above the player's head that shows they can interact with something.
	 */
	private void drawTalkingBubble(){
		batch.begin();
		batch.draw(talkingBubble,
				FrameEngine.getPlayer().getPosition().x,
				FrameEngine.getPlayer().getPosition().y
				);
		batch.end();
	}

	/**
	 * Draws all given entities.
	 */
	private void drawEntities(ArrayList<Entity> entities){
		batch.begin();
		shapeRenderer.begin();
		entities.sort(sorter);
		for (Entity en: entities){
			drawEntity(en);
		}
		batch.end();
		shapeRenderer.end();
	}

	/**
	 * Draws a single entity.
	 */
	private void drawEntity(Entity en){
		if (null != en.getImage() && !en.shouldDelete()){
			batch.setColor(en.getColor());
			if (en.isFlipped() ^ en.getImage().isFlipX()) en.getImage().flip(true, false);
			batch.draw(
					en.getImage(), 
					en.getPosition().x, 
					en.getPosition().y + en.getZPosition()
					);
			batch.setColor(DEFAULT_COLOR);
		}
		if (FrameEngine.DRAW && en instanceof InteractableEntity){
			InteractableEntity iEn = (InteractableEntity)en;
			Rectangle interactionBox = iEn.getInteractHitbox();
			shapeRenderer.rect(
					interactionBox.x, interactionBox.y, interactionBox.width, interactionBox.height
					);
		}
	}

	/**
	 * Draws all entity shadows.
	 */
	private void drawShadows(ArrayList<Entity> entities){
		final float maxHeight = 320.0f;
		batch.begin();
		for(Entity en: entities){
			if (null != en.getImage() && null != en.getShadow() && !en.shouldDelete()){
				if (		en instanceof Player
						||	en instanceof Critter
						||	en instanceof NPC
						) {
					float shadowOpacity = en.getZPosition() < maxHeight ? (1 - en.getZPosition()/maxHeight) : 0;
					batch.setColor(1, 1, 1, shadowOpacity);
					batch.draw(
							en.getShadow(), 
							en.getPosition().x + (en.getImage().getRegionWidth() - en.getShadow().getRegionWidth())/2, 
							en.getPosition().y
							);
				}
			}
		}
		batch.end();
	}

	/**
	 * Handle all emitters.
	 */
	private void handleEmitters(){
		batch.begin();
		for (Entity en: EntityHandler.getEntities()){
			if (en instanceof Emitter){
				handleEmitter((Emitter)en);
			}
		}
		batch.setColor(DEFAULT_COLOR);
		batch.end();
	}

	/**
	 * Handles functions of an emitter.
	 */
	private void handleEmitter(Emitter emitter){
		for (Graphic graphic: emitter.getGraphics()){
			float transparency = MathUtils.clamp(
					graphic.getTimeLeft()/25.0f,
					0, 1
					);
			batch.setColor(1, 1, 1, transparency);
			batch.draw(emitter.getGraphicImage(), 
					graphic.getPosition().x, 
					graphic.getPosition().y
					);
		}
	}

	/**
	 * Draws watercolor overlay on top of map.
	 */
	protected void drawOverlay(){
		beginOverlay();
		batch.setProjectionMatrix(ow_cam.combined);
		drawByTiles(overlay, false);
		endOverlay();
	}

	/**
	 * Draws a texture over the entire map.
	 */
	private void drawByTiles(TextureRegion texture, boolean scrolls){
		int x_tiles = 3 + (int) (FrameEngine.getArea().map_width / texture.getRegionWidth());
		int y_tiles = 3 + (int) (FrameEngine.getArea().map_height / texture.getRegionHeight());
		for (int xx = 0; xx < x_tiles; ++xx){
			for (int yy = 0; yy < y_tiles; ++yy){
				final float speed = 0.5f;
				float x_disp = scrolls ? FrameEngine.getTime() * speed % texture.getRegionWidth() : 0;
				float y_disp = scrolls ? FrameEngine.getTime() * speed/2.0f % texture.getRegionHeight() : 0;
				batch.draw(texture, 
						xx * texture.getRegionWidth() - x_disp, 
						yy * texture.getRegionHeight() - y_disp
						);
			}
		}
	}

	/**
	 * Draws the pause screen.
	 */
	void drawPause() {
		batch.setProjectionMatrix(center_cam.combined);
		batch.begin();
		String pauseMessage = "Let's take a break.";
		GlyphLayout glyph = new GlyphLayout(font, pauseMessage);
		font.draw(batch, pauseMessage, 
				Gdx.graphics.getWidth()/(2/ZOOM) - glyph.width/2, 
				Gdx.graphics.getHeight()*((3.0f/4.0f)*ZOOM));
//		int ii = 0;
//		for (String item: FrameEngine.getSaveFile().getInventory()){
//			font.draw(batch, item, 200, 200 + (FrameEngine.TILE * ii));
//			ii++;
//		}
		batch.end();
	}

	/**
	 * Updates the camera in the overworld to follow the player.
	 */
	private void updateOverworldCam(){
		ow_cam.position.set(FrameEngine.getPlayer().getCenter(), 0);
		ow_cam.position.x = MathUtils.clamp(
				ow_cam.position.x, 
				Gdx.graphics.getWidth()/(2/ZOOM), 
				FrameEngine.getArea().map_width - Gdx.graphics.getWidth()/(2/ZOOM)
				);
		ow_cam.position.y = MathUtils.clamp(
				ow_cam.position.y, 
				Gdx.graphics.getHeight()/(2/ZOOM), 
				FrameEngine.getArea().map_height - Gdx.graphics.getHeight()/(2/ZOOM)
				);
		// Round camera position to avoid ugly tile splitting
		final float roundTo = 10.0f;
		ow_cam.position.x = Math.round(ow_cam.position.x * roundTo)/roundTo;
		ow_cam.position.y = Math.round(ow_cam.position.y * roundTo)/roundTo;
		ow_cam.update();
	}

	/**
	 * 
	 */
	private void updateCameraFixed(){
		ow_cam.position.set(
				FrameEngine.getArea().map_width/(2),
				FrameEngine.getArea().map_height/(2),
				0
				);
		ow_cam.update();
	}

	/**
	 * Draws text in textbox at given position and size.
	 */
	protected void drawText(String text, Vector2 position, Vector2 size, boolean center){
		batch.setColor(DEFAULT_COLOR);
		batch.setProjectionMatrix(center_cam.combined);
		drawTextboxTiles((int)position.x, (int)position.y, (int)size.x, (int)size.y, 0);
		batch.begin();
		GlyphLayout glyph = new GlyphLayout(font, text);
		String spacedText = getSpacedText(((size.x - 2) * FrameEngine.TILE), text);
		if (center){
			font.draw(
					batch, spacedText, 
					position.x + FrameEngine.TILE * size.x/2 - glyph.width/2, 
					position.y + FrameEngine.TILE * size.y/2 + font.getCapHeight()/2
					);
		}
		else{
			font.draw(
					batch, spacedText, 
					position.x + FrameEngine.TILE/2, 
					position.y + (FrameEngine.TILE * (size.y - 0.5f)) 
					);
		}
		batch.end();
	}
	
	/**
	 * Creates text that won't go out of the textbox.
	 */
	private String getSpacedText(float width, String text){
		GlyphLayout glyph;
		String[] words = text.split(" ");
		String spacedText = "";
		for (String word: words){
			String test = spacedText.concat(word);
			glyph = new GlyphLayout(font, test);
			if (glyph.width >= width){
				spacedText += "\n";
				width += FrameEngine.TILE/2;
			}
			spacedText = spacedText.concat(word + " ");
		}
		return spacedText;
	}

	/**
	 * Draws the normal textbox.
	 */
	void drawDefaultTextbox(Textbox textbox) {
		final int x_tiles = (int) (Gdx.graphics.getWidth()/(FrameEngine.TILE/ZOOM)) - 1;
		final int y_tiles = 3;
		drawText(
				textbox.getDisplayedText(), 
				new Vector2(FrameEngine.TILE/2, FrameEngine.TILE/2),
				new Vector2(x_tiles, y_tiles),
				false
				);
	}

	/**
	 * Draws the tiles underlying a textbox.
	 */
	protected void drawTextboxTiles(int position_x, int position_y, int x_tiles, int y_tiles, int center){
		batch.begin();
		for(int xx = 0; xx < x_tiles; ++xx){
			for (int yy = 0; yy < y_tiles; ++yy){
				TextureRegion tile = getProperTile(x_tiles, y_tiles, xx, yy);
				batch.draw(tile, 
						position_x + (xx + center) * FrameEngine.TILE, 
						position_y + yy * FrameEngine.TILE);
			}
		}
		batch.end();
		drawTextOverlay(position_x, position_y, x_tiles, y_tiles, center);
	}
	
	/**
	 * Draws texture over textbox.
	 */
	private void drawTextOverlay(int position_x, int position_y, int x_tiles, int y_tiles, int center){
		beginOverlay();
		batch.draw(textboxOverlay, position_x, position_y, x_tiles * FrameEngine.TILE, y_tiles * FrameEngine.TILE);
		endOverlay();
	}
	
	/**
	 * Prepares batch for drawing an overlay.
	 */
	private void beginOverlay(){
		batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA);
		batch.setColor(1.0f, 1.0f, 1.0f, 0.5f);
		batch.begin();
	}
	
	/**
	 * Flushes overlay and returns batch to normal.
	 */
	private void endOverlay(){
		batch.end();
		batch.setColor(DEFAULT_COLOR);
		batch.setBlendFunction(770, 771); // Changes blend function back to normal.
	}

	/**
	 * Gets the correct tile for the position in the textbox (e.g. upper right corner, left side...)
	 */
	private TextureRegion getProperTile(int x_tiles, int y_tiles, int xx, int yy){
		TextureRegion tile;
		boolean x_side = (xx == 0 || xx == x_tiles - 1);
		boolean y_side = (yy == 0 || yy == y_tiles - 1);
		if (x_side && y_side){
			tile = textboxCorner;
			tile.flip(xx != 0 ^ tile.isFlipX(), yy == 0 ^ tile.isFlipY());
		}
		else if (x_side){
			tile = textboxSide;
			tile.flip(xx != 0 ^ tile.isFlipX(), false);
		}
		else if (y_side){
			tile = textboxTop;
			tile.flip(false, yy == 0 ^ tile.isFlipY());
		}
		else{
			return textboxCenter;
		}
		return tile;
	}
	
	/**
	 * Draws buttons on screen.
	 */
	private void drawButtons(ButtonContainer buttonContainer){
		int position = 0;
		for (Button button: buttonContainer.getButtons()){
			String name = new String(button.getName());
			if (position == buttonContainer.getPosition()){
				name = ">".concat(name);
			}
			drawText(
					name, 
					button.getArea().getPosition(new Vector2()), 
					button.getArea().getSize(new Vector2()).scl(1.0f/FrameEngine.TILE),
					true
					);
			position++;
		}
	}

	/**
	 * Draws the game's Debug menu.
	 */
	void drawDebug() {
		wipeScreen();
		batch.setProjectionMatrix(center_cam.combined);
		int i = 1; // start higher than bottom
		batch.begin();
		for (String mapID: FrameEngine.debugMenu.getMapIDs()){
			++i;
			if (mapID.equals(FrameEngine.debugMenu.getSelectedMapID())){
				mapID = "*" + mapID + "*";
			}
			debugFont.draw(batch, mapID, 200, FrameEngine.TILE * i);
		}
		batch.end();
	}

	/**
	 * Underlying drawing. Cleans screen.
	 */
	protected void wipeScreen(){
		Gdx.gl.glClearColor(wipeColor.r, wipeColor.g, wipeColor.b, wipeColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Sorts order to draw entities by their y-position on screen.
	 */
	private static class EntityDepthSorter implements Comparator<Entity>{
		@Override
		public int compare(Entity o1, Entity o2) {
			return (int) (o2.getPosition().y - o1.getPosition().y);
		}
	}

}
