package main;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import battle.BattleGUI;
import battle.Monster;
import overworld.Entity;
import overworld.Player;

/**
 * Handles drawing overworld.
 */
public class GraphicsHandler {
	protected SpriteBatch batch;
	protected final OrthographicCamera ow_cam = new OrthographicCamera();
	protected final OrthographicCamera menu_cam = new OrthographicCamera();
	protected OrthogonalTiledMapRenderer renderer;
	protected BitmapFont font;
	protected ShapeRenderer shape_renderer;
	protected final Color default_color = new Color(1, 1, 1, 1);

	GraphicsHandler(){
		batch = new SpriteBatch();
		ow_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		menu_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/nes.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 12;
		parameter.color = new Color(1, 1, 1, 1);
		parameter.borderWidth = 1;
		parameter.borderColor = new Color(0, 0, 0, 1);
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
	 * Draws overworld map and entities.
	 */
	void draw_overworld(){
		wipe_screen();
		update_overworld_cam();
		renderer.setView(ow_cam);
		renderer.render();

		batch.setProjectionMatrix(ow_cam.combined);
		batch.begin();
		for (Entity en: EntityHandler.getEntities()){
			if (en instanceof Player && ((Player)en).isInvincible()){
				batch.setColor(1, 1, 1, 0.5f);
			}
			batch.draw(en.getImage(), en.getPosition().x, en.getPosition().y);
			batch.setColor(default_color);
		}
		batch.end();
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
	 * Draws a menu detailing the player's party.
	 */
	public void draw_menu() {
		ArrayList<Monster> party = FrameEngine.getParty();
		final int margin = Gdx.graphics.getWidth()/16;
		shape_renderer.begin();
		shape_renderer.set(ShapeType.Filled);
		shape_renderer.setColor(0.25f, 0.35f, 0.45f, 1);
		shape_renderer.rect(margin, margin, 
				Gdx.graphics.getWidth() - (margin * 2), Gdx.graphics.getHeight() - (margin * 2));
		shape_renderer.end();
		batch.setProjectionMatrix(menu_cam.combined);
		batch.begin();
		for (int ii = 0; ii < party.size(); ++ii){
			Monster mon = party.get(ii);
			draw_monster(mon, mon.getSpecies().front, BattleGUI.ENEMY_ZONES, ii, -32, -20, 1);
			font.draw(batch, mon.toMenuString(), 
					BattleGUI.ENEMY_ZONES.get(ii).x + 64, Gdx.graphics.getHeight() - margin * 1.5f);
		}
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
		ow_cam.position.set(FrameEngine.getPlayer().getPosition(), 0);
		ow_cam.position.x = MathUtils.clamp(
				ow_cam.position.x, 
				Gdx.graphics.getWidth()/2, 
				FrameEngine.getCurrentArea().getWidth() - Gdx.graphics.getWidth()/2
				);
		ow_cam.position.y = MathUtils.clamp(
				ow_cam.position.y, 
				Gdx.graphics.getHeight()/2, 
				FrameEngine.getCurrentArea().getHeight() - Gdx.graphics.getHeight()/2
				);
		ow_cam.update();
	}

	/**
	 * Underlying drawing. Cleans screen.
	 */
	protected void wipe_screen(){
		Gdx.gl.glClearColor(0.25f, 0.35f, 0.45f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

}
