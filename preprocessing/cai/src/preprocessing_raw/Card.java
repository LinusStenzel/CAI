package preprocessing_raw;

/**
 * Repräsentiert eine Karte im Spiel Canasta mittels Suit und Value.
 * 
 * @author linusstenzel
 *
 */
public class Card {

	public enum CardSuit {
		DIAMOND(1), HEART(3), SPADE(0), CLUB(2);

		private int value;

		CardSuit(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static CardSuit findByValue(int value) {
			for (CardSuit s : values()) {
				if (s.value == value) {
					return s;
				}
			}
			return null;
		}
	};

	public enum CardValue {
		JOKER(0), TWO(1), THREE(2), FOUR(3), FIVE(4), SIX(5), SEVEN(6), EIGHT(7), NINE(8), TEN(9), JACK(10), QUEEN(11),
		KING(12), ACE(13);

		private int value;

		CardValue(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static CardValue findByValue(int value) {
			for (CardValue v : values()) {
				if (v.value == value) {
					return v;
				}
			}
			return null;
		}
	};

	/**
	 * Suit repräsentiert als Zahl für leichtere JSON Verarbeitung
	 */
	private int suit;
	/**
	 * Value repräsentiert als Zahl für leichtere JSON Verarbeitung
	 */
	private int value;

	public int getSuit() {
		return suit;
	}

	public void setSuit(int suit) {
		this.suit = suit;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isEqual(Card other) {
		return this.getSuit() == other.getSuit() && this.getValue() == other.getValue();
	}

	public boolean isWild() {
		return value <= 1;
	}
}
