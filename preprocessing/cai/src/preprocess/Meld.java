package preprocess;

import preprocessing_raw.Card;
import preprocessing_raw.Card.CardValue;

/**
 * Repräsentiert eine Meldung mittels CardValue und Anzahl an normalen bzw.
 * wilden Karten.
 * 
 * @author linusstenzel
 *
 */
public class Meld {

	private final Card.CardValue value;
	private int normalCards;
	private int wildCards;

	public Meld(CardValue value) {
		this.value = value;
	}

	public Card.CardValue getValue() {
		return value;
	}

	public int getNormalCards() {
		return normalCards;
	}

	public void setNormalCards(int normalCards) {
		this.normalCards = normalCards;
	}

	public int getWildCards() {
		return wildCards;
	}

	public void setWildCards(int wildCards) {
		this.wildCards = wildCards;
	}

}
