package encounter;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;

public class Monster {
	
	public static final int NUM_STATS = 5;
	public static final int VIT = 0, POW = 1, DEF = 2, EMP = 3, AGI = 4;

	final Species species;
	final Color palette; // TODO: replace with shader
	final ArrayList<String> techs = new ArrayList<String>();
	final ArrayList<String> traits = new ArrayList<String>();
	final float[] stat_weights = new float[NUM_STATS];
	final int[] curr_stats = new int[NUM_STATS];
	private final int[] real_stats = new int[NUM_STATS];

	String nickname;
	int level;
	long experience;
	float wildness;
	boolean commanded = false;

	/**
	 * Load a new monster based on its species.
	 */
	public Monster(String id, int level){
		this.species = new Species(id);
		this.nickname = species.name;
		this.level = level;
		this.palette = new Color(
				1 - ((float)Math.random() * species.palette_range.x), 
				1 - ((float)Math.random() * species.palette_range.y),
				1 - ((float)Math.random() * species.palette_range.z),
				1);
		assign_by_levelup(species.tech_levelup, techs);
		assign_by_levelup(species.trait_levelup, traits);
		assign_stat_weights();
		determine_curr_stats();
	}

	/**
	 * Assigning techs or traits per level.
	 */
	private void assign_by_levelup(HashMap<Integer, ArrayList<String>> levelup, ArrayList<String> list){
		for (int ii = 0; ii < level; ++ii){
			if (levelup.containsKey(ii)){
				list.addAll(levelup.get(ii));
			}
		}
	}

	/**
	 * Figures out monster's stats, starting with level 1 stats and adding for each additional level.
	 */
	private void determine_curr_stats(){
		for(int ii = 0; ii < NUM_STATS; ++ii){
			int curr_stat = (int) (species.base_stats[ii]/3.0);
			if (curr_stat == 0) curr_stat = 1;
			real_stats[ii] = curr_stat;
		}
		for (int ii = 1; ii < level; ++ii){
			add_to_stat();
		}
	}

	/**
	 * Finds the highest-weighted stat and adds one to it.
	 */
	private void add_to_stat(){
		HashMap<Integer, Float> rankings = new HashMap<Integer, Float>();
		for(int ii = 0; ii < NUM_STATS; ++ii){
			int stat_diff = species.base_stats[ii] - real_stats[ii];
			rankings.put(ii, stat_diff * stat_weights[ii]);
		}
		int highest_rank_num = -1;
		float highest_rank = -1;
		for(int ii = 0; ii < NUM_STATS; ++ii){
			if (rankings.get(ii) > highest_rank){
				highest_rank = rankings.get(ii);
				highest_rank_num = ii;
			}
		}
		// Don't add a stat that would put above base!
		if (real_stats[highest_rank_num] < species.base_stats[highest_rank_num] * 4){ 
			real_stats[highest_rank_num] += 1;
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
	
	/**
	 * Sets up the monster before battle begins
	 */
	public void initialize_for_battle(){
		for (int ii = 0; ii < NUM_STATS; ++ii){
			curr_stats[ii] = real_stats[ii];
		}
	}
	
	public boolean alive(){
		return curr_stats[Monster.VIT] > 0;
	}

	public Color getPalette() {
		return palette;
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

	public Species get_species() {
		return species;
	}

	public void take_damage(int damage) {
		curr_stats[Monster.VIT] -= damage;
		if (curr_stats[Monster.VIT] < 0){
			curr_stats[Monster.VIT] = 0;
		}
	}

}
