package battle;

import java.util.ArrayList;
import java.util.Comparator;

import main.CSVReader;

class Tech {

	final Monster user;
	final String name;
	final Attribute attribute;
	final Target target;
	final int tg, pow, priority;
	final ArrayList<String> actions = new ArrayList<String>();

	/**
	 * Load a new technique based on its data.
	 */
	public Tech(Monster user, String id){
		this.user = user;
		String[] data = new CSVReader().load_tech_data(id);
		name = data[0];
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
			int user_speed = tech1.user.curr_stats[Monster.AGI];
			int compare_speed = tech2.user.curr_stats[Monster.AGI];
			if (tech1.priority != tech2.priority) return tech2.priority - tech1.priority;
			else if (user_speed != compare_speed) return compare_speed - user_speed;
			else if (tech1.user.level != tech2.user.level) return tech2.user.level - tech1.user.level;
			else return 0; // NOTE: Moves tying may be a problem later.
		}
		
	}

}
