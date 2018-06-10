package battle;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;

import main.FrameEngine;

public class Monster {

	public static final int NUM_STATS = 5;
	public static final int VIT = 0, POW = 1, DEF = 2, EMP = 3, AGI = 4;
	private static final double size_variance = 6.0; // larger values mean smaller variance

	// Unchangeable aspects
	final Species species;
	final Color palette; // TODO: replace with shader
	final float[] stat_weights = new float[NUM_STATS];
	final float size;

	// Modal aspects
	ArrayList<String> tech_ids = new ArrayList<String>();
	ArrayList<String> traits = new ArrayList<String>();
	private int[] real_stats = new int[NUM_STATS]; // Monster's real stats.
	String nickname = "NO_NICKNAME";
	int level = 1;
	long experience = 0;
	float wildness = 10;
	private Status status;

	/**
	 * Load a new monster based on its species.
	 */
	public Monster(String id, int level){
		this.species = new Species(id);
		this.nickname = species.name + "_" + (char)((25 * Math.random()) + 65);
		this.level = level;
		this.experience = determine_exp_for_level(level);
		this.palette = new Color(
				1 - ((float)Math.random() * species.palette_range.x), 
				1 - ((float)Math.random() * species.palette_range.y),
				1 - ((float)Math.random() * species.palette_range.z),
				1);
		this.size = (float) ( (1.0 - (1.0/size_variance)) + (Math.random()/size_variance));
		assign_by_levelup(1, species.tech_levelup, tech_ids);
		assign_by_levelup(1, species.trait_levelup, traits);
		assign_stat_weights();
		determine_real_stats();
		status = new Status(this);
	}

	/**
	 * Assigning techs or traits per level.
	 */
	private void assign_by_levelup(
			int min_level, HashMap<Integer, ArrayList<String>> levelup, ArrayList<String> list){
		for (int ii = min_level; ii <= level; ++ii){
			if (levelup.containsKey(ii)){
				list.addAll(levelup.get(ii));
			}
		}
	}

	/**
	 * Determines the monster's stats at this level.
	 */
	private void determine_real_stats(){
		//vitality
		getRealStats()[Monster.VIT] = 
				(int) (5 + (0.25 * species.base_stats[Monster.VIT] * level));
		//all other stats
		for(int ii = 1; ii < NUM_STATS; ++ii){
			getRealStats()[ii] = (int) (3 + (0.15 * species.base_stats[ii] * level));
		}
	}

	/**
	 * Creates stat weights for a new monster. These are used to calculate what stats increase on level-up.
	 */
	private void assign_stat_weights(){
		final double heat = 1.8; // Lower values mean more variance in stat growth
		float sum = 0;
		for (int ii = 0; ii < NUM_STATS; ++ii){
			stat_weights[ii] = (float) (heat + Math.random());
			sum += stat_weights[ii];
		}
		for (int ii = 0; ii < NUM_STATS; ++ii){ // Ensures sum of stat weights is always equal to 1
			stat_weights[ii] /= sum;
		}
	}
	
	// BATTLE CONSEQUENCES

	/**
	 * Resets the monster's status.
	 */
	public void refresh(){
		status = new Status(this);
	}
	
	public void add_experience(int exp) {
		experience += exp;
		System.out.println("EXP: " + experience + "/" + determine_exp_for_level(level + 1));
		while (experience >= determine_exp_for_level(level + 1)){
			level_up();
		}
	}
	
	/**
	 * How much EXP is required to level up, based on the given level.
	 */
	private int determine_exp_for_level(int level){
		return (int)(1 + Math.pow(level, 3.0f));
	}
	
	/**
	 * How much exp this monster gives on being defeated.
	 */
	public int determine_exp_gain(){
		return (int) Math.pow(level, 2.75f);
	}
	
	private void level_up(){
		level += 1;
		determine_real_stats();
		assign_by_levelup(level, species.tech_levelup, tech_ids);
		assign_by_levelup(level, species.trait_levelup, traits);
		FrameEngine.getCurrentBattle().add_textbox(nickname + " has leveled up to level " + level + "!");
	}
	
	// GETTERS
	
	public Color getPalette() {
		return palette;
	}
	
	public float getSize(){
		return size;
	}

	public Species getSpecies() {
		return species;
	}

	public String getNickname() {
		return nickname;
	}
	
	@Override
	public String toString(){
		String stats_str = "STATS: ";
		for (int ii = 0; ii < NUM_STATS; ++ii){
			stats_str = stats_str.concat(status.getCurrStats()[ii] + "/" + getRealStats()[ii] + " ");
		}
		String techs_str = "TECHS: ";
		for (String tech: tech_ids){
			techs_str = techs_str.concat(tech + " ");
		}
		String traits_str = "TRAIT: ";
		for (String trait: traits){
			traits_str = traits_str.concat(trait + " ");
		}
		return nickname + " the " + species.name + " " + stats_str
				+ "\n " + techs_str + " " + traits_str
				;
	}
	
	public String toMenuString() {
		String stats_str = "";
		stats_str = stats_str.concat("VIT: " + getRealStats()[Monster.VIT] + "\n");
		stats_str = stats_str.concat("POW: " + getRealStats()[Monster.POW] + "\n");
		stats_str = stats_str.concat("DEF: " + getRealStats()[Monster.DEF] + "\n");
		stats_str = stats_str.concat("EMP: " + getRealStats()[Monster.EMP] + "\n");
		stats_str = stats_str.concat("AGI: " + getRealStats()[Monster.AGI] + "\n");
		String exp_str = "EXP: " + experience + "/" + determine_exp_for_level(level + 1);
		String techs_str = "TECHS: \n";
		for (Tech tech: status.techs){
			techs_str = techs_str.concat(tech.name + "\n");
		}
		return species.name + " LV:" + level
				+ "\n" + exp_str
				+ "\n" + stats_str
				+ "\n" + techs_str 
				;
	}
	
	public int[] getCurrStats(){
		return status.getCurrStats();
	}

	public int[] getRealStats() {
		return real_stats;
	}
	public Status getStatus(){
		return status;
	}
	public ArrayList<String> getTechIDs(){
		return tech_ids;
	}

}
