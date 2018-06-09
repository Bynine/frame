package main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import overworld.Entity;
import overworld.Player;

/**
 * Handles drawing overworld.
 */
public class GraphicsHandler {
	private static SpriteBatch batch;
	private static final OrthographicCamera ow_cam = new OrthographicCamera();
	private static OrthogonalTiledMapRenderer renderer;
	protected static BitmapFont font;
	protected static final Color default_color = new Color(1, 1, 1, 1);

	/**
	 * Called once before the game begins.
	 */
	static void initialize(){
		batch = new SpriteBatch();
		ow_cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/nes.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 12;
		parameter.color = new Color(1, 1, 1, 1);
		font = generator.generateFont(parameter);
		generator.dispose();
		init_overworld();
	}

	/**
	 * Sets the renderer to draw the current map.
	 */
	static void init_overworld(){
		renderer = new OrthogonalTiledMapRenderer(FrameEngine.getCurrentArea().getMap());
	}

	/**
	 * Draws overworld map and entities.
	 */
	static void draw_overworld(){
		draw();
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
	 * Updates the camera in the overworld to follow the player.
	 */
	private static void update_overworld_cam(){
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
	protected static void draw(){
		Gdx.gl.glClearColor(0.35f, 0.48f, 0.47f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
