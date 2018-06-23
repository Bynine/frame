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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import encounter.Battle;
import encounter.BattleGUI;
import encounter.Monster;
import encounter.Team;

/**
 * Handles drawing for battles. Subclass of GraphicsHandler.
 */
public class BattleGraphicsHandler extends GraphicsHandler{
	private Vector2 title_pos = 
			new Vector2(Gdx.graphics.getWidth()/3, 1 * Gdx.graphics.getHeight()/5);
	private BitmapFont big_font;
	private TextureRegion arrow = new TextureRegion(new Texture("sprites/gui/arrow.png"));

	BattleGraphicsHandler(){
		batch = new SpriteBatch();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/nes.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		parameter.color = new Color(1.0f, 0.9f, 0.4f, 1);
		parameter.borderWidth = 2;
		parameter.borderColor = new Color(0, 0, 0, 1);
		big_font = generator.generateFont(parameter);
		generator.dispose();
	}

	/**
	 * Draws the game's battles.
	 */
	void draw_battle(Battle battle){
		wipe_screen();
		Battle.State battle_state = battle.getState();
		draw_monsters(battle, battle_state);	
		shape_renderer.begin();	
		draw_text_underlay();
		shape_renderer.set(ShapeType.Line);
		shape_renderer.setColor(1.0f, 0.9f, 0.4f, 1);
		batch.begin();
		draw_state(battle, battle_state);
		batch.end();
		shape_renderer.end();
		batch.setColor(DEFAULT_COLOR);
		if (null != battle.getCurrentTextbox()){
			shape_renderer.begin();
			draw_text_underlay();
			shape_renderer.end();
			batch.begin();
			font.draw(batch, battle.getCurrentTextbox().getText(), 
					BattleGUI.TECH_ZONES.get(0).x + FrameEngine.TILE, BattleGUI.TECH_ZONES.get(0).y + FrameEngine.TILE);
			batch.end();
		}
		batch.begin();
		big_font.draw(batch, 
				"TEAM GAUGE: " + battle.getParty().getTeamGauge() + "/" + Team.TEAM_GAUGE_MAX, 
				title_pos.x,
				Gdx.graphics.getHeight() - BattleGUI.TECH_ZONES.get(0).height
				);
		batch.end();
	}

	/**
	 * Draws a nice black box under text.
	 */
	private void draw_text_underlay(){
		shape_renderer.set(ShapeType.Filled);
		shape_renderer.setColor(0.04f, 0.06f, 0.1f, 1.0f);
		shape_renderer.rect(0, Gdx.graphics.getHeight() - BattleGUI.TECH_ZONES.get(0).height, 
				Gdx.graphics.getWidth(), BattleGUI.TECH_ZONES.get(0).height);
		shape_renderer.set(ShapeType.Line);
	}

	/**
	 * Draws various GUI elements depending on the battle state.
	 */
	private void draw_state(Battle battle, Battle.State battle_state){
		switch(battle_state){
		case CHOOSE_TECH: {
			draw_arrow(battle.getGUI().getZoneFromMonster(battle.getCurrentMonster()));
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
			draw_decide(battle);
		} break;
		case TAME: {
			Rectangle zone = BattleGUI.check_mouse_in_zones(BattleGUI.ENEMY_ZONES);
			if (battle.valid_monster_target(zone)){
				draw_arrow(zone);
			}
			draw_tame();
		} break;
		case RELEASE: {
			Rectangle zone = BattleGUI.check_mouse_in_zones(BattleGUI.PARTY_ZONES);
			draw_arrow(zone);
			draw_release();
		} break;
		}
	}

	/**
	 * Draws all monsters on the battle screen.
	 */
	private void draw_monsters(Battle battle, Battle.State battle_state){
		ArrayList<Monster> enemies = battle.getEnemies().getMembers();
		for(int ii = 0; ii < enemies.size(); ++ii){
			Monster mon = enemies.get(ii);
			draw_monster_correct_position(mon, ii, true, battle_state);
		}
		ArrayList<Monster> heroes = battle.getParty().getMembers();
		for(int ii = 0; ii < heroes.size(); ++ii){
			Monster mon = heroes.get(ii);
			draw_monster_correct_position(mon, ii, false, battle_state);
		}
	}

	/**
	 * While player is choosing techs.
	 */
	private void draw_choose_tech(Battle battle){
		big_font.draw(batch, "Select a tech", title_pos.x, title_pos.y);
		int tech_index = 0;
		for(Rectangle zone: BattleGUI.TECH_ZONES){
			shape_renderer.rect(zone.x, zone.y, zone.width, zone.height);
			if (tech_index < battle.getTechs().size()){ // Only draw techs that exist!
				font.draw(batch, battle.getCurrentMonster().getStatus().getTechs().get(tech_index).toString(), 
						zone.x + FrameEngine.TILE, zone.y + zone.height - FrameEngine.TILE);
			}
			tech_index++;
		}
	}

	/**
	 * While player is choosing target.
	 */
	private void draw_choose_target(){
		big_font.draw(batch, "Select a target", title_pos.x, title_pos.y);
	}

	/**
	 * While battle is ongoing.
	 */
	private void draw_battle(){
		//
	}

	/**
	 * While battle is ongoing.
	 */
	private void draw_release(){
		big_font.draw(batch, "Who do you want to replace?", title_pos.x, title_pos.y);
	}

	/**
	 * While battle is ongoing.
	 */
	private void draw_tame(){
		big_font.draw(batch, "Who do you want to join?", title_pos.x, title_pos.y);
	}

	/**
	 * While deciding whether to fight or tame.
	 * TODO: This is extremely ugly. Refactor.
	 */
	private void draw_decide(Battle battle) {
		big_font.draw(batch, "What to do...?", title_pos.x, title_pos.y);
		int zone_pos = 0;
		for(Rectangle zone: BattleGUI.DECISION_ZONES){
			//shape_renderer.rect(zone.x, zone.y, zone.width, zone.height);
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
	private void draw_monster_correct_position(Monster mon, int ii, boolean enemy, Battle.State battle_state){
		batch.begin();
		if (enemy)
			draw_monster(mon, mon.getSpecies().front, BattleGUI.ENEMY_ZONES, ii, 0, 240, 1);
		else
			draw_monster(mon, mon.getSpecies().back, BattleGUI.PARTY_ZONES, ii, 0, 20, 2);
		batch.end();
	}

	/**
	 * Draws arrow pointing to target.
	 */
	private void draw_arrow(Rectangle zone){
		if (zone == null){
			return;
		}
		batch.draw(arrow, 
				zone.x + zone.width/2 - arrow.getRegionWidth()/2, 
				zone.y + zone.height);
	}

}
