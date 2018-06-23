package encounter;

import java.util.ArrayList;
import java.util.Comparator;

import main.CSVReader;

public class Tech {

	public final Monster user;
	public final String name;
	public final Attribute attribute;
	public final Target target;
	public final int tg, pow, priority;
	public final ArrayList<String> actions = new ArrayList<String>();

	/**
	 * Load a new technique based on its data.
	 */
	public Tech(Monster user, String id){
		this.user = user;
		String[] data = new CSVReader().load_tech_data(id);
		name = data[1];
		attribute = Attribute.valueOf(data[2]);
		tg = Integer.valueOf(data[3]);
		priority = Integer.valueOf(data[4]);
		target = Target.valueOf(data[5]);
		pow = Integer.valueOf(data[6]);
		for (String effect: data[7].split(CSVReader.long_split)){
			actions.add(effect);
		}
	}

	public static enum Attribute{
		NULL, HOT, COLD, BLOW, SLASH, HEAL, JOKE
	}

	public static enum Target{
		NONE, SINGLE, ALL, ENEMY, TEAM, SELF, PARTNERS
	}

	static class TechComparator implements Comparator<Tech>{
		
		/**
		 * Determines tech order. 
		 */
		@Override
		public int compare(Tech tech1, Tech tech2) {
			int user_speed = tech1.user.getStatus().curr_stats[Monster.AGI];
			int compare_speed = tech2.user.getStatus().curr_stats[Monster.AGI];
			if (tech1.priority != tech2.priority) return tech1.priority - tech2.priority;
			else if (user_speed != compare_speed) return user_speed - compare_speed;
			else if (tech1.user.level != tech2.user.level) return tech1.user.level - tech2.user.level;
			else return 0; // NOTE: Moves tying may be a problem later.
		}
		
	}
	
	/**
	 * Relevant information about this tech.
	 */
	@Override
	public String toString(){
		String pow_string = "POW: " + pow;
		if (pow == -1) pow_string = "Special";
		return name
				+ "\n" + "ATTRIBUTE: " + attribute
				+ "\n" + pow_string
				+ "\n" + "TG: " + tg
				+ "\n" + "TARGETS: " + target
				;
	}

}
