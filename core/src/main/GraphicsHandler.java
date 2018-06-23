package main;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import encounter.BattleGUI;
import encounter.Monster;
import overworld.Entity;

/**
 * Handles drawing overworld.
 */
public class GraphicsHandler {
	public static final Color DEFAULT_COLOR = new Color(1, 1, 1, 1);

	protected SpriteBatch batch;
	protected final OrthographicCamera ow_cam = new OrthographicCamera();
	protected final OrthographicCamera center_cam = new OrthographicCamera();
	protected OrthogonalTiledMapRenderer renderer;
	protected BitmapFont font;
	protected ShapeRenderer shape_renderer;
	private final float ZOOM = 1.0f/2.0f;
	private final EntityDepthSorter sorter = new EntityDepthSorter();

	private static final TextureRegion 
	overlay = new TextureRegion(new Texture("sprites/gui/watercolor.png")),
	interact_bubble = new TextureRegion(new Texture("sprites/overworld/player/interact.png")),
	textbox_corner = new TextureRegion(new Texture("sprites/gui/textbox_corner.png")),
	textbox_top = new TextureRegion(new Texture("sprites/gui/textbox_top.png")),
	textbox_side = new TextureRegion(new Texture("sprites/gui/textbox_side.png")),
	textbox_center = new TextureRegion(new Texture("sprites/gui/textbox_center.png"));

	GraphicsHandler(){
		batch = new SpriteBatch();
		ow_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ow_cam.zoom = ZOOM;
		center_cam.zoom = ZOOM;
		center_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lato.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;
		parameter.spaceY = 8;
		parameter.color = new Color(
				39.0f/255.0f, 
				38.0f/255.0f, 
				50.0f/255.0f, 
				1);
		parameter.mono = true;
		font = generator.generateFont(parameter);
		generator.dispose();
		shape_renderer = new ShapeRenderer();
		shape_renderer.setAutoShapeType(true);
		start_area();
	}

	/**
	 * Sets the renderer to draw the current map.
	 */
	void start_area(){
		renderer = new OrthogonalTiledMapRenderer(FrameEngine.getCurrentArea().getMap());
	}

	/**
	 * Draws all overworld graphics.
	 */
	void draw_overworld(){
		wipe_screen();
		if (FrameEngine.getCurrentArea().isCameraFixed()){
			updateCameraFixed();
		}
		else{
			update_overworld_cam();
		}
		renderer.setView(ow_cam);
		batch.setProjectionMatrix(ow_cam.combined);

		renderer.render(new int[]{0, 1});	// Render background tiles.
		renderEntities();
		renderer.render(new int[]{2}); 		// Render foreground tiles.
		
		if (null != FrameEngine.getCurrentTextbox()) {
			draw_textbox(FrameEngine.getCurrentTextbox());
		}

		draw_overlay();
	}

	/**
	 * Draws all entities.
	 */
	private void renderEntities(){
		batch.begin();
		EntityHandler.getEntities().sort(sorter);
		for (Entity en: EntityHandler.getEntities()){
			if (null != en.getImage() && !en.should_delete()){
				batch.setColor(en.getColor());
				if (en.isFlipped() ^ en.getImage().isFlipX()) en.getImage().flip(true, false);
				batch.draw(en.getImage(), en.getPosition().x, en.getPosition().y);
				batch.setColor(DEFAULT_COLOR);
			}
		}
		if (FrameEngine.canUpdateEntities() && FrameEngine.canInteract()){
			batch.draw(interact_bubble,
					FrameEngine.getPlayer().getPosition().x,
					FrameEngine.getPlayer().getPosition().y
					);
		}
		batch.end();
	}

	/**
	 * Draws watercolor overlay on top of map.
	 */
	private void draw_overlay(){
		batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA);
		batch.setProjectionMatrix(ow_cam.combined);
		batch.begin();
		batch.setColor(1, 1, 1, 0.5f);
		int x_tiles = 1 + (int) (FrameEngine.getCurrentArea().getWidth() / overlay.getRegionWidth());
		int y_tiles = 1 + (int) (FrameEngine.getCurrentArea().getHeight() / overlay.getRegionHeight());
		for (int xx = 0; xx < x_tiles; ++xx){
			for (int yy = 0; yy < y_tiles; ++yy){
				batch.draw(overlay, 
						xx * overlay.getRegionWidth(), 
						yy * overlay.getRegionHeight()
						);
			}
		}
		batch.end();
		batch.setBlendFunction(770, 771); // Changes blend function back to normal.
	}

	/**
	 * Draws the victory screen.
	 */
	void draw_victory() {
		wipe_screen();
		ArrayList<Monster> party = FrameEngine.getParty();
		batch.begin();
		for (int ii = 0; ii < party.size(); ++ii){
			Monster mon = party.get(ii);
			mon.refresh(); // Just to show title...
			draw_monster(mon, mon.getSpecies().front, BattleGUI.ENEMY_ZONES, ii, 0, 120, 1);
		}
		font.draw(batch, "You did it!", Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getHeight() * 0.8f);
		batch.end();
	}

	/**
	 * Draws the pause screen.
	 */
	public void draw_pause() {
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
	 * Draws a single monster. Returns the corresponding zone.
	 */
	protected Rectangle draw_monster(Monster mon, TextureRegion image, 
			ArrayList<Rectangle> zones, int ii, int y_disp, int title_y_disp, int size_mod){
		Rectangle zone = zones.get(ii);
		if (!mon.getStatus().alive()) return zone;
		batch.setColor(mon.getPalette());
		batch.draw(image, zone.x, zone.y + y_disp, 
				size_mod * image.getRegionWidth() * mon.getSize(), 
				size_mod * image.getRegionHeight() * mon.getSize());
		font.draw(batch, 
				mon.getNickname() + "   " + mon.getCurrStats()[Monster.VIT] + "/" + mon.getRealStats()[Monster.VIT], 
				zone.x, 
				zone.y + y_disp + title_y_disp);
		return zone;
	}

	/**
	 * Updates the camera in the overworld to follow the player.
	 */
	private void update_overworld_cam(){
		ow_cam.position.set(FrameEngine.getPlayer().getCenter(), 0);
		ow_cam.position.x = MathUtils.clamp(
				ow_cam.position.x, 
				Gdx.graphics.getWidth()/(2/ZOOM), 
				FrameEngine.getCurrentArea().getWidth() - Gdx.graphics.getWidth()/(2/ZOOM)
				);
		ow_cam.position.y = MathUtils.clamp(
				ow_cam.position.y, 
				Gdx.graphics.getHeight()/(2/ZOOM), 
				FrameEngine.getCurrentArea().getHeight() - Gdx.graphics.getHeight()/(2/ZOOM)
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
				FrameEngine.getCurrentArea().getWidth()/(2),
				FrameEngine.getCurrentArea().getHeight()/(2),
				0
				);
		ow_cam.update();
	}

	/**
	 * Draws the contents of a textbox to the screen.
	 */
	public void draw_textbox(Textbox curr_textbox) {
		batch.begin();
		batch.setColor(DEFAULT_COLOR);
		batch.setProjectionMatrix(center_cam.combined);
		draw_textbox_tiles((int) (Gdx.graphics.getWidth()/(FrameEngine.TILE/ZOOM)), 3);
		font.draw(batch, 
				curr_textbox.getDisplayedText(), 
				FrameEngine.TILE * 0.5f, 
				FrameEngine.TILE * 2.5f
				);
		batch.end();
	}

	/**
	 * Draws the tiles underlying a textbox.
	 */
	private void draw_textbox_tiles(int x_tiles, int y_tiles){
		for(int xx = 0; xx < x_tiles; ++xx){
			for (int yy = 0; yy < y_tiles; ++yy){
				TextureRegion tile = getProperTile(x_tiles, y_tiles, xx, yy);
				batch.draw(tile, xx * FrameEngine.TILE, yy * FrameEngine.TILE);
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
	protected void wipe_screen(){
		Gdx.gl.glClearColor(0.25f, 0.35f, 0.45f, 1);
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
