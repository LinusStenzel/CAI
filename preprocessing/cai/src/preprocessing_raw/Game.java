package preprocessing_raw;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert ein Cansata Spiel mittels der gespielten Runden.
 * 
 * @author linusstenzel
 *
 */
public class Game {

	private List<Round> rounds = new ArrayList<Round>();

	public List<Round> getRounds() {
		return rounds;
	}

	public void setRounds(List<Round> rounds) {
		this.rounds = rounds;
	}

	public boolean addRound(Round round) {
		return rounds.add(round);
	}
}