package preprocessing_raw;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert die Ansammlung von Karten durch eine ID, einem Namen und den
 * Karten. Hier genutzt für Handkarten und Meldungen.
 * 
 * @author linusstenzel
 *
 */
public class Deck {

	private int deckID;
	private List<Card> deck = new ArrayList<Card>();
	private CardCounter cardCounter;
	private String meldKeyName;

	public int getDeckID() {
		return deckID;
	}

	public void setDeckID(int deckID) {
		this.deckID = deckID;
	}

	public List<Card> getDeck() {
		return deck;
	}

	public void setDeck(List<Card> deck) {
		this.deck = deck;
	}

	public CardCounter getCardCounter() {
		return cardCounter;
	}

	public void setCardCounter(CardCounter cardCounter) {
		this.cardCounter = cardCounter;
	}

	public String getMeldKeyName() {
		return meldKeyName;
	}

	public void setMeldKeyName(String meldKeyName) {
		this.meldKeyName = meldKeyName;
	}

	public boolean addCard(Card card) {
		return deck.add(card);
	}

	/**
	 * Entfernt übergene Karten aus diesem Deck.
	 * 
	 * @param card
	 * @return
	 */
	public Card removeCard(Card card) {
		Card removedCard = null;

		for (int i = 0; i < deck.size() && removedCard == null; i++) {
			if (deck.get(i).isEqual(card))
				removedCard = deck.remove(i);
		}

		return removedCard;
	}
}