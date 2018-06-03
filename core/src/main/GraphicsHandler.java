package main;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import encounter.Battle;
import encounter.Monster;
import overworld.Entity;

public class GraphicsHandler {
	private static SpriteBatch batch;
	private static ShapeRenderer debug_shaper;

	static void initialize(){
		batch = new SpriteBatch();
		debug_shaper = new ShapeRenderer();
		debug_shaper.setAutoShapeType(true);
	}

	static void draw_overworld(Entity en){
		draw();
		batch.begin();
		batch.draw(en.getImage(), en.getPosition().x, en.getPosition().y);
		batch.end();
	}

	static void draw_battle(Battle battle){
		draw();
		ArrayList<Monster> enemies = battle.get_enemies();
		for(int ii = 0; ii < enemies.size(); ++ii){
			Monster mon = enemies.get(ii);
			draw_monster(mon, ii, true);
		}
		ArrayList<Monster> heroes = battle.get_heroes();
		for(int ii = 0; ii < heroes.size(); ++ii){
			Monster mon = heroes.get(ii);
			draw_monster(mon, ii, false);
		}

	}

	private static void draw_monster(Monster mon, int ii, boolean enemy){
		batch.setColor(mon.getPalette());
		Rectangle zone;
		batch.begin();
		if (enemy){
			zone = Battle.ENEMY_ZONES.get(ii);
			batch.draw(mon.get_species().front, zone.x, zone.y);
			debug_shaper.setColor(1, 0, 0, 0.5f);
		}
		else{
			zone = Battle.HERO_ZONES.get(ii);
			batch.draw(mon.get_species().back, zone.x, zone.y);
			debug_shaper.setColor(0, 1, 0, 0.5f);
		}
		batch.end();
		if (mon.alive()){
			debug_shaper.begin();
			debug_shaper.rect(zone.x, zone.y, zone.width, zone.height);
			debug_shaper.end();
		}
	}

	private static void draw(){
		Gdx.gl.glClearColor(0.35f, 0.48f, 0.47f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
