package preprocessing_raw;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert die Aktionen eines Zuges je Spieler. Ein Turn besteht aus einer
 * Liste von Moves.
 * 
 * @author linusstenzel
 *
 */
public class Turn {

	private List<Move> moves = new ArrayList<Move>();

	public List<Move> getMoves() {
		return moves;
	}

	public void setMoves(List<Move> moves) {
		this.moves = moves;
	}

	public boolean addMove(Move move) {
		return moves.add(move);
	}
}