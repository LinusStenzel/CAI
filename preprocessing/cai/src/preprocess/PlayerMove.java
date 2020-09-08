package preprocess;

import java.util.ArrayList;
import java.util.List;

import preprocessing_raw.Card;
import preprocessing_raw.Move.PlayerMoveType;
import preprocessing_raw.Player;

/**
 * Repräsentiert einen vollständigen Zug, also zum Beispiel im Falle einer
 * Meldung alle gemeledeten Karten.
 * 
 * @author linusstenzel
 *
 */
public class PlayerMove {

	private PlayerMoveType playerMoveType;
	private List<Card> cards = new ArrayList<Card>();
	/**
	 * Ausführender Spieler
	 */
	private Player player;
	private int targetDeckID;

	public PlayerMoveType getPlayerMoveType() {
		return playerMoveType;
	}

	public void setPlayerMoveType(PlayerMoveType playerMoveType) {
		this.playerMoveType = playerMoveType;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getTargetDeckID() {
		return targetDeckID;
	}

	public void setTargetDeckID(int targetDeckID) {
		this.targetDeckID = targetDeckID;
	}

	public boolean addCard(Card card) {
		return cards.add(card);
	}
}
