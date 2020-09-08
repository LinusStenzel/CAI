package preprocessing_raw;

/**
 * Repräsentiert einen Canasta Spieler mittels seiner ID, Handkarten und der
 * Team ID.
 * 
 * @author linusstenzel
 *
 */
public class Player {

	private int playerID;
	private int teamID;
	/**
	 * Handkarten
	 */
	private Deck handDeck;

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public Deck getHandDeck() {
		return handDeck;
	}

	public void setHandDeck(Deck handDeck) {
		this.handDeck = handDeck;
	}

	public boolean addCard(Card card) {
		return handDeck.addCard(card);
	}

	public Card removeCard(Card card) {
		return handDeck.removeCard(card);
	}
}