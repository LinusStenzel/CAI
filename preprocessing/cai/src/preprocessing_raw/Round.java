package preprocessing_raw;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert eine Canasta Runde mittels Startzustand und den danach
 * getätigeten Zügen der Spieler/Dealer. Eine Runde endet, wenn ein Team die
 * Runde ausmacht oder keine Karte übrig sind.
 * 
 * @author linusstenzel
 *
 */
public class Round {

	private List<Turn> turns = new ArrayList<Turn>();
	/**
	 * Startzustand
	 */
	private StateProcess stateProcessStart;

	public List<Turn> getTurns() {
		return turns;
	}

	public void setTurns(List<Turn> turns) {
		this.turns = turns;
	}

	public StateProcess getStateProcessStart() {
		return stateProcessStart;
	}

	public void setStateProcessStart(StateProcess stateProcessStart) {
		this.stateProcessStart = stateProcessStart;
	}

	public boolean addTurn(Turn turn) {
		return turns.add(turn);
	}
}