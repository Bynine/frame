package main;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import battle.Battle;
import battle.BattleGUI;
import battle.Monster;
import battle.Team;

/**
 * Handles drawing for battles. Subclass of GraphicsHandler.
 */
public class BattleGraphicsHandler extends GraphicsHandler{
	private static Vector2 title_pos = new Vector2(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight());
	private static ShapeRenderer debug_shaper;
	private static BitmapFont big_font;
	private static SpriteBatch batch;

	private static TextureRegion arrow = new TextureRegion(new Texture("sprites/gui/arrow.png"));

	/**
	 * Called once before the game begins.
	 */
	static void initialize(){
		batch = new SpriteBatch();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/nes.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 24;
		parameter.color = new Color(1, 1, 0, 1);
		big_font = generator.generateFont(parameter);
		generator.dispose();
		debug_shaper = new ShapeRenderer();
		debug_shaper.setAutoShapeType(true);
	}

	/**
	 * Draws the game's battles.
	 */
	static void draw_battle(Battle battle){
		draw();
		Battle.State battle_state = battle.getState();
		draw_monsters(battle, battle_state);	
		debug_shaper.begin();	
		debug_shaper.set(ShapeType.Filled);
		debug_shaper.setColor(0, 0, 0, 1.0f);
		debug_shaper.rect(0, Gdx.graphics.getHeight() - BattleGUI.TECH_ZONES.get(0).height, 
				Gdx.graphics.getWidth(), BattleGUI.TECH_ZONES.get(0).height);
		debug_shaper.set(ShapeType.Line);
		debug_shaper.setColor(1, 0, 1, 0.5f);
		batch.begin();
		draw_state(battle, battle_state);
		big_font.draw(batch, 
				"TEAM GAUGE: " + battle.getParty().getTeamGauge() + "/" + Team.TEAM_GAUGE_MAX, 
				title_pos.x,
				title_pos.y - BattleGUI.TECH_ZONES.get(0).height
				);
		batch.end();
		debug_shaper.end();
		batch.setColor(default_color);
	}

	/**
	 * Draws various GUI elements depending on the battle state.
	 */
	private static void draw_state(Battle battle, Battle.State battle_state){
		switch(battle_state){
		case CHOOSE_TECH: {
			draw_choose_tech(battle);
		} break;
		case CHOOSE_TARGET: {
			Rectangle zone = BattleGUI.check_mouse_in_zones(BattleGUI.MONSTER_ZONES);
			if (battle.valid_monster_target(zone)){
				draw_arrow(zone);
			}
			draw_choose_target();
		} break;
		case BATTLE: {
			draw_battle();
		} break;
		case DECIDE: {
			draw_decide();
		} break;
		case TAME: {
			Rectangle zone = BattleGUI.check_mouse_in_zones(BattleGUI.ENEMY_ZONES);
			if (battle.valid_monster_target(zone)){
				draw_arrow(zone);
			}
		} break;
		}
	}

	/**
	 * Draws all monsters on the battle screen.
	 */
	private static void draw_monsters(Battle battle, Battle.State battle_state){
		ArrayList<Monster> enemies = battle.getEnemies().getMembers();
		for(int ii = 0; ii < enemies.size(); ++ii){
			Monster mon = enemies.get(ii);
			draw_monster(mon, ii, true, battle_state);
		}
		ArrayList<Monster> heroes = battle.getParty().getMembers();
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
		for(Rectangle zone: BattleGUI.TECH_ZONES){
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
	 * While deciding whether to fight or tame.
	 * TODO: This is extremely ugly. Refactor.
	 */
	private static void draw_decide() {
		big_font.draw(batch, "What to do...?", title_pos.x, title_pos.y);
		int zone_pos = 0;
		for(Rectangle zone: BattleGUI.DECISION_ZONES){
			debug_shaper.rect(zone.x, zone.y, zone.width, zone.height);
			switch(zone_pos){
			case 0: {
				font.draw(batch, "FIGHT", zone.x + zone.width/2, zone.y + zone.height/2);
			} break;
			case 1: {
				font.draw(batch, "TAME", zone.x + zone.width/2, zone.y + zone.height/2);
			} break;
			}
			zone_pos++;
		}
	}

	/**
	 * Draws an enemy in its correct place.
	 */
	private static void draw_monster(Monster mon, int ii, boolean enemy, Battle.State battle_state){
		batch.setColor(mon.getPalette());
		Rectangle zone;
		batch.begin();
		if (enemy) zone = draw_monster_helper(mon, mon.getSpecies().front, BattleGUI.ENEMY_ZONES, ii, 240, 1);
		else zone = draw_monster_helper(mon, mon.getSpecies().back, BattleGUI.HERO_ZONES, ii, 20, 2);
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
			ArrayList<Rectangle> zones, int ii, int y_disp, int size_mod){
		Rectangle zone = zones.get(ii);
		batch.draw(image, zone.x, zone.y, 
				size_mod * image.getRegionWidth() * mon.getSize(), 
				size_mod * image.getRegionHeight() * mon.getSize());
		debug_shaper.setColor(1, 0, 0.5f, 0.5f);
		if (mon.alive()) {
			font.draw(batch, 
					mon.getNickname() + "   " + mon.getCurrStats()[Monster.VIT] + "/" + mon.getRealStats()[Monster.VIT], 
					zone.x + 40, 
					zone.y + y_disp);
		}
		return zone;
	}

	/**
	 * Draws arrow pointing to target.
	 */
	private static void draw_arrow(Rectangle zone){
		if (zone == null){
			return;
		}
		batch.draw(arrow, 
				zone.x + zone.width/2 - arrow.getRegionWidth()/2, 
				zone.y + zone.height);
	}

}
