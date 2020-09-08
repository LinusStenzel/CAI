package preprocessing_raw;

import java.util.ArrayList;
import java.util.List;

/**
 * Teams werden durch ihre ID und ausgepsielten Meldungen dargestellt.
 * @author linusstenzel
 *
 */
public class Team {

	private int teamID;
	/**
	 * Wird aus dem StateProcess Objekt entnommen/generiert.
	 */
	private List<Deck> teamMeldsValues = new ArrayList<Deck>();

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public List<Deck> getTeamMelds() {
		return teamMeldsValues;
	}

	public void setTeamMelds(List<Deck> teamMelds) {
		this.teamMeldsValues = teamMelds;
	}

	public boolean addTeamMeld(Deck teamMeld) {
		return teamMeldsValues.add(teamMeld);
	}
}