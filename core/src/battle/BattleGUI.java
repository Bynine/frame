package battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import main.FrameEngine;
import main.Pointer;

/**
 * Represents a touch-interface GUI overlaying a Battle.
 */
public class BattleGUI {
	
	private final HashMap<Rectangle, Monster> zone_to_monster = new HashMap<Rectangle, Monster>();
	
	BattleGUI(Team party_team, Team enemy_team){
		for (int ii = 0; ii < party_team.getMembers().size(); ++ii){
			zone_to_monster.put(PARTY_ZONES.get(ii), party_team.getMembers().get(ii));
		}
		for (int ii = 0; ii < enemy_team.getMembers().size(); ++ii){
			zone_to_monster.put(ENEMY_ZONES.get(ii), enemy_team.getMembers().get(ii));
		}
		if (MONSTER_ZONES.isEmpty()){
			MONSTER_ZONES.addAll(PARTY_ZONES);
			MONSTER_ZONES.addAll(ENEMY_ZONES);
		}	
	}
	
	/**
	 * Checks if mouse was clicked and if the position it was clicked in corresponds to something valid.
	 */
	Rectangle evaluate_zone(Battle.State state){
		Pointer pointer = FrameEngine.getInputHandler().getPointer();
		if (!pointer.this_frame) {
			return null;
		}
		switch(state){
		case CHOOSE_TARGET: return check_mouse_in_zones(MONSTER_ZONES);
		case CHOOSE_TECH: return check_mouse_in_zones(TECH_ZONES);
		case TAME: return check_mouse_in_zones(ENEMY_ZONES);
		case DECIDE: return check_mouse_in_zones(DECISION_ZONES);
		case RELEASE: return check_mouse_in_zones(PARTY_ZONES);
		case BATTLE: return null;
		}
		return null;
	}
	
	/**
	 * Checks if the mouse is in any of the zones. If it is, returns said zone.
	 * NOTE: This method doesn't check if the mouse is active! 
	 */
	public static Rectangle check_mouse_in_zones(ArrayList<Rectangle> ZONES){
		Pointer pointer = FrameEngine.getInputHandler().getPointer();
		for (Rectangle zone: ZONES){
			if (pointer.in_zone(zone)) {
				return zone;
			}
		}
		if (pointer.this_frame) System.out.println("Invalid target.");
		return null;
	}
	
	/**
	 * Returns the monster assigned to the requested zone.
	 */
	public Monster getMonsterFromZone(Rectangle zone){
		return zone_to_monster.get(zone);
	}
	
	/**
	 * Returns the zone assigned to the requested monster.
	 */
	public Rectangle getZoneFromMonster(Monster mon){
		HashMap<Monster, Rectangle> monster_to_zone = new HashMap<>();
		for(HashMap.Entry<Rectangle, Monster> entry : zone_to_monster.entrySet()){
		    monster_to_zone.put(entry.getValue(), entry.getKey());
		}
		return monster_to_zone.get(mon);
	}
	
	// The various input zones.
	private static final int hero_width = 480;
	private static final int hero_height = 180;
	private static final int hero_y = 0;
	public static final ArrayList<Rectangle> PARTY_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(0.0f*Gdx.graphics.getWidth()/9, hero_y, hero_width, hero_height),
			new Rectangle(3.0f*Gdx.graphics.getWidth()/9, hero_y, hero_width, hero_height),
			new Rectangle(6.0f*Gdx.graphics.getWidth()/9, hero_y, hero_width, hero_height)
			));
	private static final int enemy_dim = 240;
	private static final int enemy_y = 200;
	public static final ArrayList<Rectangle> ENEMY_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(1.2f*Gdx.graphics.getWidth()/7, enemy_y, enemy_dim, enemy_dim),
			new Rectangle(2.7f*Gdx.graphics.getWidth()/7, enemy_y, enemy_dim, enemy_dim),
			new Rectangle(4.2f*Gdx.graphics.getWidth()/7, enemy_y, enemy_dim, enemy_dim)
			));
	private static final int tech_w = Gdx.graphics.getWidth()/4;
	private static final int tech_h = 120;
	private static final int tech_y = Gdx.graphics.getHeight() - tech_h;
	public static final ArrayList<Rectangle> TECH_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(0*Gdx.graphics.getWidth()/4, tech_y, tech_w, tech_h),
			new Rectangle(1*Gdx.graphics.getWidth()/4, tech_y, tech_w, tech_h),
			new Rectangle(2*Gdx.graphics.getWidth()/4, tech_y, tech_w, tech_h),
			new Rectangle(3*Gdx.graphics.getWidth()/4, tech_y, tech_w, tech_h)
			));
	private static final int dec_w = 480;
	public static final ArrayList<Rectangle> DECISION_ZONES = new ArrayList<Rectangle>(Arrays.asList(
			new Rectangle(0*Gdx.graphics.getWidth()/2, tech_y, dec_w, tech_h),
			new Rectangle(1*Gdx.graphics.getWidth()/2, tech_y, dec_w, tech_h)
			));
	public static final ArrayList<Rectangle> MONSTER_ZONES = new ArrayList<Rectangle>();
	
}
