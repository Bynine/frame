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
import text.Textbox;
import timer.Timer;

/**
 * Handles drawing overworld.
 */
public class GraphicsHandler {
	public static final Color 
	DEFAULT_COLOR = new Color(1, 1, 1, 1),
	SELECT_COLOR1 = new Color(0.75f, 0.85f, 0.95f, 1.0f),
	SELECT_COLOR2 = new Color(0.85f, 0.85f, 0.85f, 1.0f),
	INVALID_COLOR = new Color(0.5f, 0.65f, 0.7f, 1.0f),
	WIPE_COLOR = new Color(0.05f, 0.07f, 0.12f, 1);

	protected SpriteBatch batch;
	protected final OrthographicCamera worldCam = new OrthographicCamera();
	protected final OrthographicCamera centerCam = new OrthographicCamera();
	protected OrthogonalTiledMapRenderer renderer;
	protected BitmapFont font, debugFont, warningFont, textFont;
	protected ShapeRenderer shapeRenderer;
	public static final float ZOOM = 1.0f/2.0f;
	private final EntityDepthSorter sorter = new EntityDepthSorter();
	private static final TextureRegion 
	arrowUp = new TextureRegion(new Texture("sprites/gui/arrow_up.png")),
	arrowDown = new TextureRegion(new Texture("sprites/gui/arrow_down.png")),
	interactBubble = new TextureRegion(new Texture("sprites/player/interact.png")),
	talkingBubble = new TextureRegion(new Texture("sprites/player/talk.png")),
	textboxOverlay = new TextureRegion(new Texture("sprites/gui/textbox_overlay.png")),
	textboxCorner = new TextureRegion(new Texture("sprites/gui/textbox_corner.png")),
	textboxTop = new TextureRegion(new Texture("sprites/gui/textbox_top.png")),
	textboxSide = new TextureRegion(new Texture("sprites/gui/textbox_side.png")),
	textboxCenter = new TextureRegion(new Texture("sprites/gui/textbox_center.png"));
	Timer selectTimer = new Timer(10);

	private static TextureRegion overlay = new TextureRegion(new Texture("sprites/gui/watercolor_dim.png"));
	private static TextureRegion splash = new TextureRegion(new Texture("sprites/gui/splashsmall.png"));
	private static TextureRegion water = new TextureRegion(new Texture("sprites/graphics/water.png"));

	GraphicsHandler(){
		batch = new SpriteBatch();
		worldCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		worldCam.zoom = ZOOM;
		centerCam.zoom = ZOOM;
		centerCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lato.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 18;
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
		parameter.color = new Color(0.8f, 0.1f, 0.05f, 1);
		warningFont = generator.generateFont(parameter);
		generator.dispose();
		textFont = font;
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
	}

	void update(){
		selectTimer.countUp();
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
		ArrayList<Entity> backEntities = new ArrayList<Entity>();
		ArrayList<Entity> normalEntities = new ArrayList<Entity>();
		ArrayList<Entity> frontEntities = new ArrayList<Entity>();
		for (Entity entity: EntityHandler.getEntities()){
			if (entity.getLayer() == Entity.Layer.BACK) backEntities.add(entity);
			if (entity.getLayer() == Entity.Layer.NORMAL) normalEntities.add(entity);
			if (entity.getLayer() == Entity.Layer.FRONT) frontEntities.add(entity);
		}
		if (FrameEngine.getArea().cameraFixed){
			updateCameraFixed();
		}
		else{
			updateWorldCam();
		}
		renderer.setView(worldCam);
		batch.setProjectionMatrix(worldCam.combined);

		batch.setColor(DEFAULT_COLOR);
		drawWater();
		renderer.render(new int[]{0, 1});	// Render background tiles.

		if (!FrameEngine.INVIS) drawEntities(backEntities);
		if (!FrameEngine.INVIS) drawShadows(EntityHandler.getEntities());
		if (!FrameEngine.INVIS) drawEntities(normalEntities);
		renderer.render(new int[]{2, 3}); 	// Render foreground tiles.
		if (!FrameEngine.INVIS) drawEntities(frontEntities);
		handleEmitters();

		if (FrameEngine.canUpdateEntities() && FrameEngine.canInteract()) {
			drawInteractBubble();
		}
		else if(null != FrameEngine.getCurrentTextbox()
				&& FrameEngine.getCurrentTextbox().playerTalking()){
			drawTalkingBubble();
		}

		if (null != FrameEngine.getCurrentTextbox()) {
			drawDefaultTextbox(FrameEngine.getCurrentTextbox());
		}

		drawOverlay();

		if (FrameEngine.DRAW){
			shapeRenderer.setProjectionMatrix(worldCam.combined);
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
		batch.setProjectionMatrix(worldCam.combined);
		drawByTiles(overlay, false);
		endOverlay();
	}

	/**
	 * Draws a texture over the entire map.
	 */
	private void drawByTiles(TextureRegion texture, boolean scrolls){
		int x_tiles = 3 + (int) (FrameEngine.getArea().mapWidth / texture.getRegionWidth());
		int y_tiles = 3 + (int) (FrameEngine.getArea().mapHeight / texture.getRegionHeight());
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
	 * Draws an item menu in a bunch of nice little boxes.
	 */
	void drawItems(AbstractMenu menu, boolean price){
		batch.setProjectionMatrix(centerCam.combined);
		int ii = 0;
		int cursor = menu.getCursor();
		int stipend = Math.max( 0, (5 * ((int)(cursor/5))) - 15 );
		Vector2 range = new Vector2(stipend, stipend+20);
		for (Button button: menu.getList()){
			if (range.x <= ii && range.y > ii){
				ItemDescription desc = (ItemDescription)button.getOutput();
				if (price){
					if (desc.tooExpensive()){
						batch.setColor(INVALID_COLOR);
					}
				}
				if (button == menu.getActiveButton()){
					setSelectColor();
				}
				final int dim = 2;
				Vector2 position = menu.getButtonPosition(ii - stipend);
				drawTextboxTiles((int)position.x, (int)position.y, dim, dim, 0);
				batch.begin();
				batch.draw(
						desc.icon, 
						position.x + FrameEngine.TILE - desc.icon.getRegionWidth()/2, 
						position.y + FrameEngine.TILE - desc.icon.getRegionHeight()/2
						);
				batch.end();
			}
			ii++;
		}
		batch.begin();
		float center = FrameEngine.TILE * 4.5f;
		if (range.y < menu.getList().size()) {
			batch.draw(arrowDown, center, FrameEngine.TILE * 0.125f);
		}
		if (range.x > 0) {
			batch.draw(arrowUp, center, FrameEngine.TILE * 8.875f);
		}
		batch.end();
		drawText("Press ENTER to finish.", 
				new Vector2(Gdx.graphics.getWidth()/(1.8f/ZOOM), FrameEngine.TILE), 
				new Vector2(8, 2), 
				true);
		drawItemDescription(menu, price);
	}

	/**
	 * Draws the description of the currently selected item.
	 */
	private void drawItemDescription(AbstractMenu menu, boolean price){
		float itemBoxWidth = 6.0f;
		drawText(Integer.toString(FrameEngine.getSaveFile().getMoney()) + " ACORNS", 
				new Vector2(
						Gdx.graphics.getWidth()/(4/ZOOM) - (FrameEngine.TILE * 2.5f), 
						Gdx.graphics.getHeight()/(2) - FrameEngine.TILE*2
						),
				new Vector2(itemBoxWidth, 2),
				true
				);
		if (null != menu.getActiveButton()){
			ItemDescription desc = ((ItemDescription)menu.getActiveButton().getOutput());
			final int width = 7;
			final float x = (FrameEngine.TILE * 2) + Gdx.graphics.getWidth()/(2/ZOOM);
			drawText(desc.name, 
					new Vector2(
							x, 
							Gdx.graphics.getHeight()/(2/ZOOM) + FrameEngine.TILE * 2
							),
					new Vector2(width, 2),
					true
					);
			drawText(desc.description, 
					new Vector2(
							x, 
							Gdx.graphics.getHeight()/(2/ZOOM) - FrameEngine.TILE
							),
					new Vector2(width, 3),
					false
					);
			if (price){
				if (desc.tooExpensive()) {
					textFont = warningFont;
				}
				drawText("COST: " + desc.price + " ACORN(S)", 
						new Vector2(
								x, 
								Gdx.graphics.getHeight()/(2/ZOOM) - FrameEngine.TILE * 3
								),
						new Vector2(width, 2),
						false
						);
			}
		}
	}

	/**
	 * Updates the camera in the overworld to follow the player.
	 */
	private void updateWorldCam(){
		worldCam.position.set(FrameEngine.getPlayer().getCenter(), 0);
		worldCam.position.x = MathUtils.clamp(
				worldCam.position.x, 
				Gdx.graphics.getWidth()/(2/ZOOM), 
				FrameEngine.getArea().mapWidth - Gdx.graphics.getWidth()/(2/ZOOM)
				);
		worldCam.position.y = MathUtils.clamp(
				worldCam.position.y, 
				Gdx.graphics.getHeight()/(2/ZOOM), 
				FrameEngine.getArea().mapHeight - Gdx.graphics.getHeight()/(2/ZOOM)
				);
		// Round camera position to avoid ugly tile splitting
		final float roundTo = 10.0f;
		worldCam.position.x = Math.round(worldCam.position.x * roundTo)/roundTo;
		worldCam.position.y = Math.round(worldCam.position.y * roundTo)/roundTo;
		worldCam.update();
	}

	/**
	 * Updates the camera in a locked position.
	 */
	private void updateCameraFixed(){
		worldCam.position.set(
				FrameEngine.getArea().mapWidth/(2),
				FrameEngine.getArea().mapHeight/(2),
				0
				);
		worldCam.update();
	}

	protected void drawText(String text, Vector2 position, Vector2 size, boolean center){
		drawText(text, text, position, size, center, false);
	}

	/**
	 * Draws text in textbox at given position and size.
	 */
	protected void drawText(String text, String fullText, 
			Vector2 position, Vector2 size, boolean center, boolean selected){
		batch.setColor(DEFAULT_COLOR);
		batch.setProjectionMatrix(centerCam.combined);
		if (selected) {
			setSelectColor();
		}
		drawTextboxTiles((int)position.x, (int)position.y, (int)size.x, (int)size.y, 0);
		batch.setColor(DEFAULT_COLOR);
		batch.begin();
		GlyphLayout glyph = new GlyphLayout(font, text);
		String spacedText = getSpacedText(
				(Math.max((size.x - 2), 4) * FrameEngine.TILE), 
				text, fullText
				);
		if (center){
			textFont.draw(
					batch, spacedText, 
					position.x + FrameEngine.TILE * size.x/2 - glyph.width/2, 
					position.y + FrameEngine.TILE * size.y/2 + font.getCapHeight()/2
					);
		}
		else{
			textFont.draw(
					batch, spacedText, 
					position.x + FrameEngine.TILE/2, 
					position.y + (FrameEngine.TILE * (size.y - 0.5f)) 
					);
		}
		batch.end();
		textFont = font;
	}

	/**
	 * Creates text that won't go out of the textbox.
	 */
	private String getSpacedText(float width, String text, String fullText){
		String[] words = text.split(" ");
		String[] fullWords = fullText.split(" ");
		int num = text.split(" ").length;
		String spacedText = "";
		String testText = "";
		for (int ii = 0; ii < num; ++ii){
			String word = words[ii];
			String testTextFuture = testText.concat(fullWords[ii]);
			GlyphLayout futureCheck = new GlyphLayout(font, testTextFuture);
			if (futureCheck.width >= (width - FrameEngine.TILE/8)){
				spacedText += "\n";
				testText += "\n";
				width += FrameEngine.TILE/2;
			}
			spacedText = spacedText.concat(word + " ");
			testText = testText.concat(fullWords[ii]);
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
				textbox.getAllText(),
				new Vector2(FrameEngine.TILE/2, FrameEngine.TILE/2),
				new Vector2(x_tiles, y_tiles),
				false,
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
		batch.setColor(1.0f, 1.0f, 1.0f, 0.25f);
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
	 * Draws the buttons of a menu.
	 */
	public void drawMenu(AbstractMenu menu){
		int position = 0;
		for (Button button: menu.getList()){
			boolean selected = false;
			String name = new String(button.getName());
			if (position == menu.cursor){
				selected = true;
			}
			drawText(
					name, 
					name,
					menu.getButtonPosition(position),
					(new Vector2(
							button.getDimensions().x, 
							button.getDimensions().y)),
					true,
					selected
					);
			position++;
		}
	}

	/**
	 * Underlying drawing. Cleans screen.
	 */
	protected void wipeScreen(){
		Gdx.gl.glClearColor(WIPE_COLOR.r, WIPE_COLOR.g, WIPE_COLOR.b, WIPE_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Sorts order to draw entities by their y-position on screen.
	 */
	private static class EntityDepthSorter implements Comparator<Entity>{
		@Override
		public int compare(Entity o1, Entity o2) {
			return (int) (
					(o2.getPosition().y + o2.getZPosition()) - 
					(o1.getPosition().y + o1.getZPosition())
					);
		}
	}

	public void drawMainMenu() {
		wipeScreen();
		batch.begin();
		batch.draw(splash, 0, 0);
		batch.end();
	}

	public void drawDebug() {
		wipeScreen();
	}

	final int blipTime = 30;
	private void setSelectColor(){
		Color currColor = batch.getColor();
		if (selectTimer.getCounter() % blipTime > blipTime/2){
			batch.setColor(
					currColor.r - 0.25f,
					currColor.g - 0.15f,
					currColor.b - 0.05f,
					1
					);
		}
		else{
			batch.setColor(
					currColor.r - 0.12f,
					currColor.g - 0.12f,
					currColor.b - 0.12f,
					1
					);
		}
	}

}
