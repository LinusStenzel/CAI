package preprocessing_raw;

/**
 * Hilfsklassen für schnelle Speicherung eine Deckzustandes, genauer der Kartenart.
 * 
 * @author linusstenzel
 *
 */
public class CardCounter {

	private int allCards;
	private int normalCards;
	private int wildCards;

	public int getAllCards() {
		return allCards;
	}

	public void setAllCards(int allCards) {
		this.allCards = allCards;
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