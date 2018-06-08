package main;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import battle.Battle;
import battle.Monster;

/**
 * Handles drawing for battles. Subclass of GraphicsHandler.
 */
public class BattleGraphicsHandler extends GraphicsHandler{
	private static Vector2 title_pos = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
	private static ShapeRenderer debug_shaper;
	private static BitmapFont big_font;
	private static SpriteBatch batch;
	
	/**
	 * Called once before the game begins.
	 */
	static void initialize(){
		batch = new SpriteBatch();
		big_font = new BitmapFont();
		big_font.setColor(1, 1, 0, 1);
		debug_shaper = new ShapeRenderer();
		debug_shaper.setAutoShapeType(true);
	}

	/**
	 * Draws the game's battles. TODO: Split into own class?
	 */
	static void draw_battle(Battle battle){
		draw();
		Battle.State battle_state = battle.getState();
		draw_monsters(battle, battle_state);

		debug_shaper.setColor(1, 0, 1, 0.5f);
		debug_shaper.begin();	
		batch.begin();
		switch(battle_state){
		case CHOOSE_TECH: draw_choose_tech(battle); break;
		case CHOOSE_TARGET: draw_choose_target(); break;
		case BATTLE: draw_battle(); break;
		}
		batch.end();
		debug_shaper.end();
		batch.setColor(default_color);
	}

	/**
	 * Draws all monsters on the battle screen.
	 */
	private static void draw_monsters(Battle battle, Battle.State battle_state){
		ArrayList<Monster> enemies = battle.getEnemies();
		for(int ii = 0; ii < enemies.size(); ++ii){
			Monster mon = enemies.get(ii);
			draw_monster(mon, ii, true, battle_state);
		}
		ArrayList<Monster> heroes = FrameEngine.getParty();
		for(int ii = 0; ii < heroes.size(); ++ii){
			Monster mon = heroes.get(ii);
			draw_monster(mon, ii, false, battle_state);
		}
	}

	/**
	 * While player is choosing techs.
	 */
	private static void draw_choose_tech(Battle battle){
		big_font.draw(batch, "Select a tech", title_pos.x, title_pos.y);
		int tech_index = 0;
		for(Rectangle zone: Battle.TECH_ZONES){
			debug_shaper.rect(zone.x, zone.y, zone.width, zone.height);
			if (tech_index < battle.getTechs().size()){ // Only draw techs that exist!
				font.draw(batch, battle.getTechs().get(tech_index), zone.x + zone.width/2, zone.y + zone.height/2);
			}
			tech_index++;
		}
	}

	/**
	 * While player is choosing target.
	 */
	private static void draw_choose_target(){
		big_font.draw(batch, "Select a target", title_pos.x, title_pos.y);
	}

	/**
	 * While battle is ongoing.
	 */
	private static void draw_battle(){
		big_font.draw(batch, "FIGHTING!!!", title_pos.x, title_pos.y);
	}

	/**
	 * Draws an enemy in its correct place.
	 */
	private static void draw_monster(Monster mon, int ii, boolean enemy, Battle.State battle_state){
		batch.setColor(mon.getPalette());
		Rectangle zone;
		batch.begin();
		if (enemy) zone = draw_monster_helper(mon, mon.getSpecies().front, Battle.ENEMY_ZONES, ii, 280);
		else zone = draw_monster_helper(mon, mon.getSpecies().back, Battle.HERO_ZONES, ii, 180);
		batch.end();
		if (mon.alive() && battle_state == Battle.State.CHOOSE_TARGET){
			debug_shaper.begin();
			debug_shaper.rect(zone.x, zone.y, zone.width, zone.height);
			debug_shaper.end();
		}
	}
	
	/**
	 * Draws a single monster. Returns the corresponding zone.
	 */
	private static Rectangle draw_monster_helper(Monster mon, TextureRegion image, 
			ArrayList<Rectangle> zones, int ii, int y_disp){
		Rectangle zone = zones.get(ii);
		batch.draw(image, zone.x, zone.y, 
				image.getRegionWidth() * mon.getSize(), 
				image.getRegionHeight() * mon.getSize());
		debug_shaper.setColor(1, 0, 0.5f, 0.5f);
		if (mon.alive()) font.draw(batch, mon.getNickname(), zone.x + 140, zone.y + y_disp);
		return zone;
	}
}
