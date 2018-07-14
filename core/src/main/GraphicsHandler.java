package main;

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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import overworld.Emitter;
import overworld.Emitter.Graphic;
import overworld.Entity;
import overworld.InteractableEntity;
import overworld.Player;

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

	private boolean shaderOn = false;
	private static ShaderProgram shader;

	private static final TextureRegion 
	interact_bubble = new TextureRegion(new Texture("sprites/overworld/player/interact.png")),
	shadow = new TextureRegion(new Texture("sprites/overworld/player/shadow.png")),
	textbox_corner = new TextureRegion(new Texture("sprites/gui/textbox_corner.png")),
	textbox_top = new TextureRegion(new Texture("sprites/gui/textbox_top.png")),
	textbox_side = new TextureRegion(new Texture("sprites/gui/textbox_side.png")),
	textbox_center = new TextureRegion(new Texture("sprites/gui/textbox_center.png"));

	private static TextureRegion overlay = new TextureRegion(new Texture("sprites/gui/watercolor_dim.png"));
	private static TextureRegion water = new TextureRegion(new Texture("sprites/overworld/graphics/water.png"));

	GraphicsHandler(){
		shader = new ShaderProgram(
				Gdx.files.internal("shaders/vert.glsl"),
				Gdx.files.internal("shaders/frag.glsl")
				);
		if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
		batch = new SpriteBatch();
		if (shaderOn) batch.setShader(shader);
		ow_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ow_cam.zoom = ZOOM;
		center_cam.zoom = ZOOM;
		center_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lato.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
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
		startArea();
	}

	/**
	 * Sets the renderer to draw the current map.
	 */
	void startArea(){
		renderer = new OrthogonalTiledMapRenderer(FrameEngine.getCurrentArea().getMap());
		if (shaderOn) renderer.getBatch().setShader(shader);
		overlay = new TextureRegion(new Texture(
				"sprites/gui/watercolor_" + FrameEngine.getCurrentArea().overlayString + ".png"));
	}

	/**
	 * Draws all overworld graphics.
	 */
	void drawOverworld(){
		wipeScreen();
		if (FrameEngine.getCurrentArea().cameraFixed){
			updateCameraFixed();
		}
		else{
			updateOverworldCam();
		}
		renderer.setView(ow_cam);
		batch.setProjectionMatrix(ow_cam.combined);

		batch.setColor(DEFAULT_COLOR);
		drawByTiles(water, true);
		renderer.render(new int[]{0, 1});	// Render background tiles.
		
		renderEntities();
		renderer.render(new int[]{2, 3}); 		// Render foreground tiles.
		handleEmitters();
		
		

		if (FrameEngine.canUpdateEntities() && FrameEngine.canInteract()) drawInteractBubble();

		if (null != FrameEngine.getCurrentTextbox()) {
			drawDefaultTextbox(FrameEngine.getCurrentTextbox());
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

		drawOverlay();
	}

	/**
	 * Draws the transition between maps.
	 */
	void drawTransition(){
		wipeScreen();
		drawOverlay();
	}

	/**
	 * Draws the bubble above the player's head that shows they can interact with something.
	 */
	private void drawInteractBubble(){
		batch.begin();
		batch.draw(interact_bubble,
				FrameEngine.getPlayer().getPosition().x,
				FrameEngine.getPlayer().getPosition().y
				);
		batch.end();
	}

	/**
	 * Draws all entities.
	 */
	private void renderEntities(){
		batch.begin();
		shapeRenderer.begin();
		EntityHandler.getEntities().sort(sorter);
		for (Entity en: EntityHandler.getEntities()){
			if (null != en.getImage() && !en.should_delete()){
				if (en instanceof Player) {
					batch.setColor(DEFAULT_COLOR);
					batch.draw(shadow, en.getPosition().x, en.getPosition().y);
				}
				batch.setColor(en.getColor());
				if (en.isFlipped() ^ en.getImage().isFlipX()) en.getImage().flip(true, false);
				batch.draw(en.getImage(), en.getPosition().x, en.getPosition().y);
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
		batch.end();
		shapeRenderer.end();
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
		if (shaderOn) batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_ONE);
		else batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA);
		batch.setProjectionMatrix(ow_cam.combined);
		batch.setColor(1, 1, 1, 0.5f);
		drawByTiles(overlay, false);
		batch.setBlendFunction(770, 771); // Changes blend function back to normal.
	}

	private void drawByTiles(TextureRegion texture, boolean scrolls){
		batch.begin();
		int x_tiles = 2 + (int) (FrameEngine.getCurrentArea().map_width / texture.getRegionWidth());
		int y_tiles = 2 + (int) (FrameEngine.getCurrentArea().map_height / texture.getRegionHeight());
		for (int xx = 0; xx < x_tiles; ++xx){
			for (int yy = 0; yy < y_tiles; ++yy){
				final float speed = 0.5f;
				float x_disp = 
						scrolls ? FrameEngine.getTime() * speed % texture.getRegionWidth() : 0;
						float y_disp = 
								scrolls ? FrameEngine.getTime() * speed/2.0f % texture.getRegionHeight() : 0;
								batch.draw(texture, 
										xx * texture.getRegionWidth() - x_disp, 
										yy * texture.getRegionHeight() - y_disp
										);
			}
		}
		batch.end();
	}

	/**
	 * Draws the pause screen.
	 */
	public void drawPause() {
		batch.setProjectionMatrix(center_cam.combined);
		batch.begin();
		font.draw(batch, 
				"Taking a break...", 
				Gdx.graphics.getWidth()/(2/ZOOM), 
				Gdx.graphics.getHeight()/(2/ZOOM)
				);
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
				FrameEngine.getCurrentArea().map_width - Gdx.graphics.getWidth()/(2/ZOOM)
				);
		ow_cam.position.y = MathUtils.clamp(
				ow_cam.position.y, 
				Gdx.graphics.getHeight()/(2/ZOOM), 
				FrameEngine.getCurrentArea().map_height - Gdx.graphics.getHeight()/(2/ZOOM)
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
				FrameEngine.getCurrentArea().map_width/(2),
				FrameEngine.getCurrentArea().map_height/(2),
				0
				);
		ow_cam.update();
	}

	/**
	 * Draws text in textbox at given position and size.
	 */
	protected void drawText(String text, Vector2 position, Vector2 size, boolean center){
		batch.begin();
		batch.setColor(DEFAULT_COLOR);
		batch.setProjectionMatrix(center_cam.combined);
		drawTextboxTiles((int)position.x, (int)position.y, (int)size.x, (int)size.y, 0);
		GlyphLayout glyph = new GlyphLayout(font, text);
		if (center){
			font.draw(
					batch, text, 
					position.x + FrameEngine.TILE * size.x/2 - glyph.width/2, 
					position.y + FrameEngine.TILE * size.y/2 + font.getCapHeight()/2
					);
		}
		else{
			font.draw(
					batch, text, 
					position.x + FrameEngine.TILE/2, 
					position.y + (FrameEngine.TILE * (size.y - 0.5f)) 
					);
		}
		batch.end();
	}

	/**
	 * Draws the normal textbox.
	 */
	public void drawDefaultTextbox(Textbox textbox) {
		final int x_tiles = (int) (Gdx.graphics.getWidth()/(FrameEngine.TILE/ZOOM));
		final int y_tiles = 2;
		drawText(
				textbox.getDisplayedText(), 
				new Vector2(0, 0),
				new Vector2(x_tiles, y_tiles),
				false
				);
	}

	/**
	 * Draws the tiles underlying a textbox.
	 */
	protected void drawTextboxTiles(int position_x, int position_y, int x_tiles, int y_tiles, int center){
		for(int xx = 0; xx < x_tiles; ++xx){
			for (int yy = 0; yy < y_tiles; ++yy){
				TextureRegion tile = getProperTile(x_tiles, y_tiles, xx, yy);
				batch.draw(tile, 
						position_x + (xx + center) * FrameEngine.TILE, 
						position_y + yy * FrameEngine.TILE);
			}
		}
	}

	/**
	 * Gets the correct tile for the position in the textbox (e.g. upper right corner, left side...)
	 */
	private TextureRegion getProperTile(int x_tiles, int y_tiles, int xx, int yy){
		TextureRegion tile;
		boolean x_side = (xx == 0 || xx == x_tiles - 1);
		boolean y_side = (yy == 0 || yy == y_tiles - 1);
		if (x_side && y_side){
			tile = textbox_corner;
			tile.flip(xx != 0 ^ tile.isFlipX(), yy == 0 ^ tile.isFlipY());
		}
		else if (x_side){
			tile = textbox_side;
			tile.flip(xx != 0 ^ tile.isFlipX(), false);
		}
		else if (y_side){
			tile = textbox_top;
			tile.flip(false, yy == 0 ^ tile.isFlipY());
		}
		else{
			return textbox_center;
		}
		return tile;
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

	/**
	 * Draws the game's Debug menu.
	 */
	public void drawDebug() {
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

}
