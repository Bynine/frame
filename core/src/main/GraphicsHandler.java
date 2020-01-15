package main;

import java.util.ArrayList;
import java.util.Comparator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
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
import entity.Currency;
import entity.Emitter;
import entity.Entity;
import entity.InteractableEntity;
import entity.Item;
import entity.NPC;
import entity.Player;
import entity.Secret;
import entity.Emitter.Graphic;
import text.MenuOption;
import text.Textbox;
import timer.Timer;

/**
 * Handles drawing everything.
 */
public class GraphicsHandler {
	public static final float ZOOM = 1.0f/2.0f;

	public static final Color 
	DEFAULT_COLOR = new Color(1, 1, 1, 1),
	SELECT_COLOR1 = new Color(0.75f, 0.85f, 0.95f, 1.0f),
	SELECT_COLOR2 = new Color(0.85f, 0.85f, 0.85f, 1.0f),
	INVALID_COLOR = new Color(0.5f, 0.65f, 0.7f, 1.0f),
	WIPE_COLOR = new Color(0.05f, 0.07f, 0.12f, 1);
	private static Vector2 offsetTarget = new Vector2();
	private static Vector2 offset = new Vector2();

	protected SpriteBatch batch;
	protected final OrthographicCamera 
	worldCam = new OrthographicCamera(),
	reflectionCam = new OrthographicCamera(),
	lightReflectionCam = new OrthographicCamera(),
	centerCam = new OrthographicCamera();
	protected OrthogonalTiledMapRenderer renderer;
	protected BitmapFont font, debugFont, warningFont, textFont;
	protected ShapeRenderer shapeRenderer;
	private final EntityDepthSorter sorter = new EntityDepthSorter();
	private static final TextureRegion 
	treasure1Art = new TextureRegion(new Texture("art/treasure1.png")),
	treasure2Art = new TextureRegion(new Texture("art/treasure2.png")),
	treasure3Art = new TextureRegion(new Texture("art/treasure3.png")),
	treasure4Art = new TextureRegion(new Texture("art/treasure4.png")),
	treasure5Art = new TextureRegion(new Texture("art/treasure5.png")),
	treasure6Art = new TextureRegion(new Texture("art/treasure6.png")),
	secretArt = new TextureRegion(new Texture("art/secret.png")),
	arrowUp = new TextureRegion(new Texture("sprites/gui/arrow_up.png")),
	arrowDown = new TextureRegion(new Texture("sprites/gui/arrow_down.png")),
	heart = new TextureRegion(new Texture("sprites/gui/heart.png")),
	acorn = new TextureRegion(new Texture("sprites/items/acorn.png")),
	title = new TextureRegion(new Texture("sprites/gui/title.png")),
	logo = new TextureRegion(new Texture("sprites/gui/logo.png")),
	snailOn = new TextureRegion(new Texture("sprites/gui/snail_on.png")),
	snailOff = new TextureRegion(new Texture("sprites/gui/snail_off.png")),
	map = new TextureRegion(new Texture("sprites/gui/map.png")),
	interactBubble = new TextureRegion(new Texture("sprites/player/interact.png")),
	surpriseBubble = new TextureRegion(new Texture("sprites/player/surprise.png")),
	talkingBubble = new TextureRegion(new Texture("sprites/player/talk.png")),
	textboxOverlay = new TextureRegion(new Texture("sprites/gui/textbox_overlay.png")),
	textboxBegin = new TextureRegion(new Texture("sprites/gui/textbox_begin.png")),
	textboxMid = new TextureRegion(new Texture("sprites/gui/textbox_mid.png")),
	textboxCorner = new TextureRegion(new Texture("sprites/gui/textbox_corner.png")),
	selectedTextboxCorner = new TextureRegion(new Texture("sprites/gui/textbox_corner_selected.png")),
	textboxTop = new TextureRegion(new Texture("sprites/gui/textbox_top.png")),
	textboxSide = new TextureRegion(new Texture("sprites/gui/textbox_side.png")),
	textboxCenter = new TextureRegion(new Texture("sprites/gui/textbox_center.png"));
	private final ArrayList<Animation<TextureRegion>> 
		gotMail = Animator.createAnimation(30, "sprites/gui/got_mail.png", 2, 1);
	Timer selectTimer = new Timer(10);
	private final String credits;
	final float blackR = 26.0f/255.0f;
	final float blackG = 29.0f/255.0f;
	final float blackB = 39.0f/255.0f;

	private static TextureRegion 
		overlay = new TextureRegion(new Texture("sprites/gui/watercolor_dim.png")),
		water = new TextureRegion(new Texture("sprites/graphics/water.png")),
		overcast = new TextureRegion(new Texture("sprites/graphics/overcast.png")),
		mountainsky = new TextureRegion(new Texture("sprites/graphics/mountainsky.png"));

	GraphicsHandler(){
		batch = new SpriteBatch();
		worldCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		worldCam.zoom = ZOOM;
		centerCam.zoom = ZOOM;
		centerCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		reflectionCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		reflectionCam.zoom = ZOOM;
		lightReflectionCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		lightReflectionCam.zoom = ZOOM;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lato.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 18;
		parameter.spaceY = 2;
		parameter.color = new Color(blackR, blackG, blackB, 1);
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
		FileHandle handle = Gdx.files.internal("misc/credits.txt");
		credits = handle.readString();
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
		ArrayList<Entity> wayBackEntities = new ArrayList<Entity>();
		ArrayList<Entity> backEntities = new ArrayList<Entity>();
		ArrayList<Entity> normalEntities = new ArrayList<Entity>();
		ArrayList<Entity> frontEntities = new ArrayList<Entity>();
		ArrayList<Entity> overheadEntities = new ArrayList<Entity>();
		ArrayList<Entity> lightEntities = new ArrayList<Entity>();
		for (Entity entity: EntityHandler.getEntities()){
			if (entity.getLayer() == Entity.Layer.WAYBACK) wayBackEntities.add(entity);
			if (entity.getLayer() == Entity.Layer.BACK) backEntities.add(entity);
			if (entity.getLayer() == Entity.Layer.NORMAL) normalEntities.add(entity);
			if (entity.getLayer() == Entity.Layer.FRONT) frontEntities.add(entity);
			if (entity.getLayer() == Entity.Layer.OVERHEAD) overheadEntities.add(entity);
			if (entity.getLayer() == Entity.Layer.LIGHT) lightEntities.add(entity);
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
		renderer.getBatch().setColor(getReflectionColor(DEFAULT_COLOR, 0.6f));
		handleEmittersReflection();
		renderer.setView(lightReflectionCam);
		renderer.render(new int[]{3});	// Reflection tiles
		renderer.setView(reflectionCam);
		renderer.render(new int[]{4});
		renderer.getBatch().setColor(DEFAULT_COLOR);
		batch.setColor(DEFAULT_COLOR);
		drawEntities(wayBackEntities);
		drawWaterEntities(EntityHandler.getEntities());
		renderer.setView(worldCam);
		renderer.render(new int[]{0});	// Background tiles.

		drawEntities(backEntities);
		renderer.render(new int[]{1});
		if (!FrameEngine.INVIS) drawShadows(EntityHandler.getEntities());
		drawEntities(normalEntities);
		renderer.render(new int[]{2}); 	// Foreground tiles.
		drawEntities(frontEntities);
		renderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		renderer.getBatch().setColor(FrameEngine.getArea().lightColor);
		renderer.render(new int[]{3});		// Light tiles.
		renderer.getBatch().setColor(DEFAULT_COLOR);
		drawEntities(lightEntities);
		endOverlay(renderer.getBatch(), false);
		renderer.render(new int[]{4}); 	// Overhead tiles.
		drawEntities(overheadEntities);
		handleEmitters();
		if (FrameEngine.canControlPlayer() && FrameEngine.canInteract()) {
			InteractableEntity ien = FrameEngine.getCurrentInteractable();
			if (ien instanceof NPC) drawAbovePlayer(talkingBubble);
			else if (ien instanceof Secret || ien instanceof Item || ien instanceof Currency) {
				drawAbovePlayer(surpriseBubble);
			}
			else drawAbovePlayer(interactBubble);
		}
		else if(null != FrameEngine.getCurrentTextbox()
				&& FrameEngine.getCurrentTextbox().playerTalking()){
			drawAbovePlayer(talkingBubble);
		}
		else if(Player.getImageState() == Player.ImageState.GET 
				&& null != FrameEngine.getCurrentThing()){
			drawAbovePlayer(FrameEngine.getCurrentThing().icon);
		}

		if (null != FrameEngine.getCurrentTextbox()
				&& !FrameEngine.inventoryRequest) {
			drawDefaultTextbox(FrameEngine.getCurrentTextbox());
		}

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
		if (FrameEngine.getArea().sky) {
			TextureRegion sky;
			if (FrameEngine.getArea().id.equals("MOUNTAIN")) {
				sky = FrameEngine.getSaveFile().getFlag("FOUND_FLAME") ? mountainsky : overcast;
			}
			else {
				sky = FrameEngine.getArea().frost ? overcast : water;
			}
			drawByTiles(
					sky, 
					sky == water ? false : true
			);
		}
		if (FrameEngine.getArea().drawRipple()) {
			batch.draw(Player.ripple,
					FrameEngine.getPlayer().getPosition().x,
					FrameEngine.getPlayer().getPosition().y
					);
		}
		batch.end();
	}

	/**
	 * Draws given texture above player's head.
	 */
	private void drawAbovePlayer(TextureRegion texture){
		batch.begin();
		final int xPlus = 
				(FrameEngine.getPlayer().getImage().getRegionWidth() - texture.getRegionWidth())/2;
		batch.draw(texture,
				FrameEngine.getPlayer().getPosition().x + xPlus,
				FrameEngine.getPlayer().getPosition().y + 40
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
			if (!(en.equals(FrameEngine.getPlayer()) && FrameEngine.INVIS)){
				drawEntity(en);
			}
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
					en.getPosition().y + en.getZPosition(),
					en.getImage().getRegionWidth()/2,
					en.getImage().getRegionHeight()/2,
					en.getImage().getRegionWidth() * en.getWidthMod(),
					en.getImage().getRegionHeight() * en.getHeightMod(),
					1,
					1,
					en.getAngle()
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
	 * Draws reflections for given entities.
	 */
	private void drawWaterEntities(ArrayList<Entity> entities){
		batch.begin();
		shapeRenderer.begin();
		entities.sort(sorter);
		for (Entity en: entities){
			if (null != en.getImage() && !en.shouldDelete() && 
					en.getLayer() != Entity.Layer.BACK && en.getLayer() != Entity.Layer.WAYBACK){
				drawWaterEntity(en);
			}
		}
		batch.end();
		shapeRenderer.end();
	}

	/**
	 * Draws an entity's reflection in the water.
	 */
	private void drawWaterEntity(Entity en){
		final float maxDistance = 960.0f;
		float sizeReduction = Math.max((maxDistance - en.getZPosition()), 0)/maxDistance;
		batch.setColor(getReflectionColor(en.getColor(), sizeReduction));
		if (en.isFlipped() ^ en.getImage().isFlipX()) en.getImage().flip(true, false);
		en.getImage().flip(false, true);
		batch.draw(
				en.getImage(), 
				en.getPosition().x, 
				en.getPosition().y - en.getZPosition()/12 - (en.getImage().getRegionHeight()*0.9f),
				en.getImage().getRegionWidth() * sizeReduction,
				en.getImage().getRegionHeight() * sizeReduction
				);
		batch.setColor(DEFAULT_COLOR);
		en.getImage().flip(false, true);
	}

	/**
	 * The color of given character's reflection in the water.
	 */
	private Color getReflectionColor(Color color, float sizeReduction){
		return new Color(
				color.r*sizeReduction + blackR*(1-sizeReduction),
				color.g*sizeReduction + blackG*(1-sizeReduction),
				color.b*sizeReduction + blackB*(1-sizeReduction), 
				(sizeReduction * color.a) / 1.8F);
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

	private void handleEmittersReflection(){
		batch.begin();
		for (Entity en: EntityHandler.getEntities()){
			if (en instanceof Emitter){
				handleEmitterReflection((Emitter)en);
			}
		}
		batch.end();
	}

	/**
	 * Handles functions of an emitter.
	 */
	private void handleEmitterReflection(Emitter emitter){
		for (Graphic graphic: emitter.getGraphics()){
			float transparency = MathUtils.clamp(
					graphic.getTimeLeft()/25.0f,
					0, 0.5f
					);
			batch.setColor(1, 1, 1, transparency);
			emitter.getGraphicImage().flip(false, true);
			batch.draw(emitter.getGraphicImage(), 
					graphic.getPosition().x, 
					graphic.getPosition().y - (FrameEngine.TILE*4)
					);
			emitter.getGraphicImage().flip(false, true);
		}
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
	public void drawOverlay(){
		beginOverlay(0.0f);
		batch.setProjectionMatrix(worldCam.combined);
		final int overlayMultiplier = 1;
		for (int ii = 0; ii < overlayMultiplier; ++ii){
			drawByTiles(overlay, false);
		}
		endOverlay(batch, true);
	}

	/**
	 * Draws a texture over the entire map.
	 */
	private void drawByTiles(TextureRegion texture, boolean scrolls){
		int x_tiles = 3 + (int) 
				(FrameEngine.getArea().mapWidth / 
						texture.getRegionWidth());
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
		int stipend = menu.getPage() * Inventory.WIDTH;
		Vector2 range = new Vector2(stipend, stipend + Inventory.WIDTH * Inventory.HEIGHT);
		for (MenuOption button: menu.getList()){
			if (range.x <= ii && range.y > ii){
				ItemDescription desc = (ItemDescription)button.getOutput();
				boolean selected = button == menu.getActiveButton();
				if (price){
					if (desc.tooExpensive()){
						batch.setColor(INVALID_COLOR);
					}
				}
				final int dim = 2;
				Vector2 position = menu.getButtonPosition(ii - stipend);
				if (selected){
					setSelectColor();
				}
				drawTextboxTiles((int)position.x, (int)position.y, dim, dim, 0, selected);
				batch.begin();
				batch.draw(
						desc.icon, 
						position.x + FrameEngine.TILE - desc.icon.getRegionWidth()/2, 
						position.y + FrameEngine.TILE - desc.icon.getRegionHeight()/2
						);
				if (!price){
					float iconX = position.x + FrameEngine.TILE + 8;
					float iconY = position.y + FrameEngine.TILE + 8;
					if (desc.hasAttribute("TREASURE")){
						batch.draw(heart, iconX, iconY);
					}
					if (desc.id.equals("SNAIL")){
						batch.draw(FrameEngine.snailActive ? snailOn : snailOff, iconX, iconY);
					}
				}
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
		drawEnterToExit();
		drawItemDescription(menu, price);
	}

	/**
	 * Draws the description of the currently selected item.
	 */
	private void drawItemDescription(AbstractMenu menu, boolean price){
		float itemBoxWidth = 6.0f;
		final float moneyX = Gdx.graphics.getWidth()/(4/ZOOM) - (FrameEngine.TILE * 2.5f);
		final float moneyY = menu.getButtonPosition(0).y + (FrameEngine.TILE * 3.0f);
		drawText(Integer.toString(FrameEngine.getSaveFile().getMoney()) + " ACORNS", 
				new Vector2(moneyX, moneyY),
				new Vector2(itemBoxWidth, 2),
				true
				);
		batch.begin();
		batch.draw(
				acorn, 
				moneyX + (itemBoxWidth - 1) * FrameEngine.TILE, 
				moneyY + 32
				);
		batch.end();
		if (null != menu.getActiveButton()){
			ItemDescription desc = ((ItemDescription)menu.getActiveButton().getOutput());
			final int width = 8;
			final float x = Gdx.graphics.getWidth()/(1.8f/ZOOM);
			drawText(desc.name, 
					new Vector2(
							x, 
							Gdx.graphics.getHeight()/(2/ZOOM) + FrameEngine.TILE * 1
							),
					new Vector2(width, 2),
					true
					);
			drawText(desc.description, 
					new Vector2(
							x, 
							Gdx.graphics.getHeight()/(2/ZOOM) - FrameEngine.TILE * 2
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
								Gdx.graphics.getHeight()/(2/ZOOM) - FrameEngine.TILE * 4
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
		offset = offset.lerp(offsetTarget, 0.06f);
		final float min = 0.1f;
		if (Math.abs(offset.x) < min) offset.x = 0;
		if (Math.abs(offset.y) < min) offset.y = 0;
		worldCam.position.set(FrameEngine.getPlayer().getCenter(), 0);
		worldCam.position.x = MathUtils.clamp(
				worldCam.position.x + offset.x, 
				Gdx.graphics.getWidth()/(2/ZOOM), 
				FrameEngine.getArea().mapWidth - Gdx.graphics.getWidth()/(2/ZOOM)
				);
		worldCam.position.y = MathUtils.clamp(
				worldCam.position.y + offset.y, 
				Gdx.graphics.getHeight()/(2/ZOOM), 
				FrameEngine.getArea().mapHeight - Gdx.graphics.getHeight()/(2/ZOOM)
				);
		// Round camera position to avoid ugly tile splitting
		final float roundTo = 5.0f;
		worldCam.position.x = Math.round(worldCam.position.x * roundTo)/roundTo;
		worldCam.position.y = Math.round(worldCam.position.y * roundTo)/roundTo;
		worldCam.update();
		reflectionCam.position.x = worldCam.position.x;
		reflectionCam.position.y = worldCam.position.y + FrameEngine.TILE * 3;
		reflectionCam.update();
		lightReflectionCam.position.x = worldCam.position.x;
		lightReflectionCam.position.y = worldCam.position.y + FrameEngine.TILE * 1;
		lightReflectionCam.update();
		offsetTarget.setZero();
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
		drawTextboxTiles((int)position.x, (int)position.y, (int)size.x, (int)size.y, 0, selected);
		batch.setColor(DEFAULT_COLOR);
		batch.begin();
		GlyphLayout glyph = new GlyphLayout(textFont, text);
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
					position.x + FrameEngine.TILE/2.8f, 
					position.y + (FrameEngine.TILE * (size.y - 0.5f)) + 2
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
			GlyphLayout futureCheck = new GlyphLayout(textFont, testTextFuture);
			int distance = width > FrameEngine.TILE * 6 ? FrameEngine.TILE/2 : FrameEngine.TILE/8;
			if (futureCheck.width >= (width - distance) || word.equals("~")){
				spacedText += "\n";
				testText += "\n";
				width += FrameEngine.TILE/2;
			}
			if (!word.equals("~")) {
				spacedText = spacedText.concat(word + " ");
				testText = testText.concat(fullWords[ii]);
			}
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
				new Vector2(FrameEngine.TILE/2, FrameEngine.TILE/4),
				new Vector2(.25f + x_tiles, y_tiles),
				false,
				false
				);
		InteractableEntity speaker = textbox.getSpeaker();
		if (null != speaker && speaker instanceof NPC){
			NPC npc = (NPC) speaker;
			drawText(
					npc.getName(),
					npc.getName(),
					new Vector2(FrameEngine.TILE/2, 3.25f * FrameEngine.TILE),
					new Vector2(8, 1),
					true,
					false
					);
		}
	}

	/**
	 * Draws the tiles underlying a textbox.
	 */
	protected void drawTextboxTiles(int position_x, int position_y, 
			int x_tiles, int y_tiles,
			int center, boolean selected){
		batch.begin();
		for(int xx = 0; xx < x_tiles; ++xx){
			for (int yy = 0; yy < y_tiles; ++yy){
				TextureRegion tile = getProperTile(x_tiles, y_tiles, xx, yy, selected);
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
		beginOverlay(0.5f);
		batch.draw(textboxOverlay, position_x, position_y, x_tiles * FrameEngine.TILE, y_tiles * FrameEngine.TILE);
		endOverlay(batch, true);
	}

	/**
	 * Prepares batch for drawing an overlay.
	 */
	private void beginOverlay(float trans){
		//batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA); // 0.5f alpha for best result
		batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA);
		float colorMod = 1.0f;
		if (FrameEngine.inTransition()) colorMod = FrameEngine.getTransitionMod();
		float r = MathUtils.clamp(colorMod, 0, 1);
		float g = MathUtils.clamp(colorMod, 0, 1);
		float b = MathUtils.clamp(colorMod, 0, 1);
		batch.setColor(r, g, b, trans);
		batch.begin();
	}

	/**
	 * Flushes overlay and returns batch to normal.
	 */
	private void endOverlay(Batch batchToEnd, boolean end){
		if (end) batchToEnd.end();
		batchToEnd.setColor(DEFAULT_COLOR);
		batchToEnd.setBlendFunction(770, 771); // Changes blend function back to normal.
	}

	/**
	 * Gets the correct tile for the position in the textbox (e.g. upper right corner, left side...)
	 */
	private TextureRegion getProperTile(int x_tiles, int y_tiles, int xx, int yy, boolean selected){
		TextureRegion tile;
		if (y_tiles == 1){
			if (xx == 0){
				tile = textboxBegin;
				tile.flip(tile.isFlipX(), false);
			}
			else if (xx == x_tiles-1){
				tile = textboxBegin;
				tile.flip(!tile.isFlipX(), false);
			}
			else{
				tile = textboxMid;
			}
		}
		else{
			boolean x_side = (xx == 0 || xx == x_tiles - 1);
			boolean y_side = (yy == 0 || yy == y_tiles - 1);
			if (x_side && y_side){
				tile = selected ? selectedTextboxCorner : textboxCorner;
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
		}
		return tile;
	}

	/**
	 * Draws the buttons of a menu.
	 */
	public void drawMenu(AbstractMenu menu){
		int position = 0;
		for (MenuOption button: menu.getList()){
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

	public void drawTitle() {
		drawOverworld();
		batch.begin();
		batch.draw(title, 
				FrameEngine.getPlayer().getPosition().x - (FrameEngine.TILE * 4.5f),
				FrameEngine.getPlayer().getPosition().y + (FrameEngine.TILE * 2)
				);
		batch.draw(logo, 
				FrameEngine.getPlayer().getPosition().x - (FrameEngine.TILE * 7f),
				FrameEngine.getPlayer().getPosition().y + (FrameEngine.TILE * 6)
				);
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
					avg(currColor.r, 0.3f),
					avg(currColor.r, 0.35f),
					avg(currColor.r, 0.7f),
					1
					);
		}
		else{
			batch.setColor(
					avg(currColor.r, 0.6f),
					avg(currColor.r, 0.6f),
					avg(currColor.r, 0.6f),
					1
					);
		}
	}

	private float avg(float a, float b){
		return (a + b)/2.0f;
	}

	final float creditSpeed = 0.5f;
	public void drawCredits() {
		drawOverworld();
		batch.setProjectionMatrix(centerCam.combined);

		final float posX = FrameEngine.TILE/2;
		final float posY = 200 + (Math.min(FrameEngine.getTime(), 
				FrameEngine.getCreditsTime() - 1000) * creditSpeed);
		drawArt(0, posX, posY, treasure1Art, "FREBKING_REWARD");
		drawArt(1, posX, posY, treasure3Art, "GRUB_REWARD");
		drawArt(2, posX, posY, treasure4Art, "CAFE_REWARD");
		drawArt(3, posX, posY, treasure5Art, "WORLD_REWARD");
		drawArt(4, posX, posY, treasure6Art, "GHOST_REWARD");
		drawArt(5, posX, posY, treasure2Art, "CURSE_REWARD");
		drawArt(6, posX, posY, secretArt, "FOUND_GOAL");
		batch.begin();
		debugFont.draw(batch, credits, posX, posY);
		batch.end();
	}

	private void drawArt(int offset, float posX, float posY, TextureRegion art, String flag){
		batch.begin();
		if (!FrameEngine.getSaveFile().getFlag(flag)) {
			batch.setColor(0.06f, 0.08f, 0.1f, 0.08f);
		}
		else{
			batch.setColor(1.00f, 1.00f, 1.00f, 1.00f);
		}
		batch.draw(art, 
				posX - FrameEngine.TILE/2, 
				posY + ((offset+1) * -FrameEngine.TILE * 13.7f) - (FrameEngine.TILE * 10),
				544, 320);
		batch.setColor(DEFAULT_COLOR);
		batch.end();
	}

	public static void setOffset(Vector2 change) {
		offsetTarget.set(change);
	}

	public static boolean isZoomed(){
		return ZOOM < 1;
	}

	public void drawMap() {
		batch.begin();
		batch.setProjectionMatrix(centerCam.combined);
		batch.draw(map, FrameEngine.TILE * 5, FrameEngine.TILE * 2);
		batch.end();
		drawEnterToExit();
	}

	private void drawEnterToExit() {
		drawText("Press ENTER to exit.", 
				new Vector2(Gdx.graphics.getWidth()/(1.8f/ZOOM), Gdx.graphics.getHeight()*ZOOM - (FrameEngine.TILE*2)), 
				new Vector2(8, 2), true);
	}

	public void drawMailbox(Mailbox mailbox) {
		int position = 0;
		for (MenuOption button: mailbox.getList()){
			boolean selected = position == mailbox.cursor;
			@SuppressWarnings("unchecked")
			ArrayList<TextureRegion> texs = (ArrayList<TextureRegion>) button.getOutput();
			Vector2 pos = mailbox.getButtonPosition(position);
			drawText("", "", pos,
					(new Vector2(
							button.getDimensions().x, 
							button.getDimensions().y)),
					true,
					selected
					);
			batch.begin();
			batch.draw(
					button.getProperties().get(Mailbox.OPENED).equals("true") ? texs.get(2) : texs.get(1), 
					pos.x + FrameEngine.TILE/2, pos.y + FrameEngine.TILE/2);
			if (selected) batch.draw(texs.get(0), FrameEngine.TILE * 10, FrameEngine.TILE * 1);
			batch.end();
			position++;
		}
		drawEnterToExit();
	}

	public void drawGotMail() {
		batch.begin();
		batch.draw(gotMail.get(0).getKeyFrame(FrameEngine.getTime()), FrameEngine.TILE * 14, FrameEngine.TILE * 2.87f);
		batch.end();
	}

}
