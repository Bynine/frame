package battle;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;

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
	ArrayList<String> techs = new ArrayList<String>();
	ArrayList<String> traits = new ArrayList<String>();
	int[] curr_stats = new int[NUM_STATS]; // Temporary stats that can change in a battle.
	private int[] real_stats = new int[NUM_STATS]; // Monster's real stats.
	String nickname = "NO_NICKNAME";
	int level = 1;
	long experience = 0;
	float wildness = 10;

	/**
	 * Load a new monster based on its species.
	 */
	public Monster(String id, int level){
		this.species = new Species(id);
		this.nickname = species.name;
		this.level = level;
		this.experience = determine_exp_for_level(level);
		this.palette = new Color(
				1 - ((float)Math.random() * species.palette_range.x), 
				1 - ((float)Math.random() * species.palette_range.y),
				1 - ((float)Math.random() * species.palette_range.z),
				1);
		this.size = (float) ( (1.0 - (1.0/size_variance)) + (Math.random()/size_variance));
		assign_by_levelup(1, species.tech_levelup, techs);
		assign_by_levelup(1, species.trait_levelup, traits);
		assign_stat_weights();
		determine_real_stats();
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
		real_stats[Monster.VIT] = 
				(int) (2 + (0.50 * species.base_stats[Monster.VIT] * level));
		//all other stats
		for(int ii = 1; ii < NUM_STATS; ++ii){
			real_stats[ii] = (int) (1 + (0.35 * species.base_stats[ii] * level));
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
	
	boolean commanded = false;

	/**
	 * Sets up the monster before battle begins.
	 */
	public void initialize_for_battle(){
		for (int ii = 0; ii < NUM_STATS; ++ii){
			curr_stats[ii] = real_stats[ii];
		}
	}

	/**
	 * Whether this monster can currently act.
	 */
	public boolean can_act(){
		return alive();
	}

	/**
	 * Whether this monster can currently be targeted.
	 */
	public boolean can_target(){
		return alive();
	}

	/**
	 * Whether or not the monster is still in the battle.
	 */
	public boolean alive(){
		return curr_stats[Monster.VIT] > 0;
	}

	public void take_damage(int damage) {
		curr_stats[Monster.VIT] -= damage;
		if (curr_stats[Monster.VIT] < 0){ // 0 is as low as it goes
			curr_stats[Monster.VIT] = 0;
		}
	}

	public void heal(int heal) {
		curr_stats[Monster.VIT] += heal;
		if (curr_stats[Monster.VIT] > real_stats[Monster.VIT]){ // no overheal
			curr_stats[Monster.VIT] = real_stats[Monster.VIT];
		}
	}
	
	public void add_experience(int exp) {
		experience += exp;
		while (experience > determine_exp_for_level(level + 1)){
			level_up();
		}
	}
	
	private int determine_exp_for_level(int level){
		return (int)Math.pow(level, 1.5);
	}
	
	private void level_up(){
		level += 1;
		determine_real_stats();
		assign_by_levelup(level, species.tech_levelup, techs);
		assign_by_levelup(level, species.trait_levelup, traits);
		System.out.println(nickname + " has leveled up to level " + level + "!");
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
			stats_str = stats_str.concat(curr_stats[ii] + "/" + real_stats[ii] + " ");
		}
		String techs_str = "TECHS: ";
		for (String tech: techs){
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

}
