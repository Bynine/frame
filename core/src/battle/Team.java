package battle;

import java.util.ArrayList;

/**
 * Defines a team in a battle, including their Team Gauge.
 */
public class Team{

	public static final int TEAM_GAUGE_MAX = 10;
	private int team_gauge = TEAM_GAUGE_MAX;
	private final ArrayList<Monster> members = new ArrayList<Monster>();

	Team(ArrayList<Monster> members){
		this.members.addAll(members);
	}

	public void boost_team_gauge() {
		team_gauge += 1;
		if (team_gauge > TEAM_GAUGE_MAX) team_gauge = TEAM_GAUGE_MAX;
	}

	public boolean has_tech(Tech tech) {
		return members.contains(tech.user);
	}
	
	public boolean enough_team_gauge(int tg){
		return tg <= team_gauge;
	}

	void use_team_gauge(int tg){
		team_gauge -= tg;
	}

	public int getTeamGauge(){
		return team_gauge;
	}

	public ArrayList<Monster> getMembers() {
		return members;
	}
}
