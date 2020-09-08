package preprocessing_raw;

/**
 * Repräsentiert eine einzelne Aktion eines Spielers. Hier die Deckänderung
 * einer Karten , also ihre "Bewegung".
 * 
 * @author linusstenzel
 *
 */
public class Move {

	public enum MoveType {
		START_SHUFFLE(0), START_DEAL(1), PLAYER_MOVE(2), REINIT_MOVE(3);

		private int value;

		MoveType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static MoveType findByValue(int value) {
			for (MoveType mt : values()) {
				if (mt.value == value) {
					return mt;
				}
			}
			return null;
		}
	};

	public enum PlayerMoveType {
		UNDEFINED(0), DRAW_CARD(1), DRAW_AND_DROP_RED(2), ADD_DISCARD_TO_HAND(3), ADD_DISCARD_TO_RED(4),
		CREATING_MELD(5), ADDING_CARD_TO_MELD(6), DROP_CARD(7);

		private int value;

		PlayerMoveType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static PlayerMoveType findByValue(int value) {
			for (PlayerMoveType pmt : values()) {
				if (pmt.value == value) {
					return pmt;
				}
			}
			return null;
		}
	}

	/**
	 * MoveType repräsentiert als Zahl für leichtere JSON Verarbeitung
	 */
	private int moveType;
	/**
	 * PlayerMoveType repräsentiert als Zahl für leichtere JSON Verarbeitung
	 */
	private int playerMoveType;
	private Card card;
	/**
	 * ID des ausführden Spielers
	 */
	private int playerId;
	/**
	 * ID des Ursprungsdeck
	 */
	private int sourceDeckID;
	/**
	 * ID des Zieldeck
	 */
	private int targetDeckID;
	/**
	 * Kurzbeschreibung
	 */
	private String description;

	public int getMoveType() {
		return moveType;
	}

	public void setMoveType(int moveType) {
		this.moveType = moveType;
	}

	public int getPlayerMoveType() {
		return playerMoveType;
	}

	public void setPlayerMoveType(int playerMoveType) {
		this.playerMoveType = playerMoveType;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getSourceDeckID() {
		return sourceDeckID;
	}

	public void setSourceDeckID(int sourceDeckID) {
		this.sourceDeckID = sourceDeckID;
	}

	public int getTargetDeckID() {
		return targetDeckID;
	}

	public void setTargetDeckID(int targetDeckID) {
		this.targetDeckID = targetDeckID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}