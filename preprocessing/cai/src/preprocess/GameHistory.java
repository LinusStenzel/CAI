package preprocess;

import java.util.ArrayList;
import java.util.List;

import preprocess.TrainingData.NetType;
import preprocessing_raw.Card;
import preprocessing_raw.Card.CardValue;
import preprocessing_raw.Game;
import preprocessing_raw.Move;
import preprocessing_raw.Player;
import preprocessing_raw.Round;
import preprocessing_raw.StateProcess;
import preprocessing_raw.Team;
import preprocessing_raw.Turn;
import preprocessing_raw.Move.PlayerMoveType;

/**
 * Klasse zum Analysieren von Canasta Spielen.
 * 
 * @author linusstenzel
 *
 */
public class GameHistory {

	/**
	 * Unverarbeite Game Objekte
	 */
	private List<Game> rawGames;

	/*
	 * Spieler
	 */
	private Player playerOne;
	private Player playerTwo;
	private Player playerThree;
	private Player playerFour;

	/*
	 * Team Meldungen
	 */
	private List<Meld> teamOneMelds;
	private List<Meld> teamTwoMelds;

	/*
	 * Team IDs
	 */
	private int teamOneID;
	private int teamTwoID;

	/**
	 * Ablagestapel
	 */
	private List<Card> discardPile;

	private List<TrainingData> trainingDataList;

	public GameHistory(List<Game> rawGames) {
		this.rawGames = rawGames;
	}

	public List<Game> getRawGames() {
		return rawGames;
	}

	public void setRawGames(List<Game> rawGames) {
		this.rawGames = rawGames;
	}

	/**
	 * Analysiert und verarbeitet die "rohen" Game Objekte und generiert daraus die
	 * Trainingsdaten für die Netze. Hierfür wird jede Entscheidungssituation der
	 * Spieler betrachtet und in Form des Objektes TrainingData dokumentiert.
	 */
	public void generateTrainingData() {
		trainingDataList = new ArrayList<TrainingData>();

		for (Game game : rawGames) {
			for (Round round : game.getRounds()) {

				// initialsieren der Membervariablen je Runde
				StateProcess stateProcessStart = round.getStateProcessStart();
				fillPlayer(stateProcessStart.getCanastaPlayerList());
				fillTeams(stateProcessStart.getTeamsList());
				discardPile = stateProcessStart.getDiscardDeck().getDeck();

				for (Turn turn : round.getTurns()) {

					// Zusammenfassung der Moves zu PlayerMoves
					List<PlayerMove> playerMoves = extractPlayerMoves(turn);

					boolean melded = false;
					boolean added = false;
					boolean drew = false;

					for (PlayerMove playerMove : playerMoves) {

						Player player = playerMove.getPlayer();
						// Input der Trainingsdaten
						TrainingData trainingData = new TrainingData();
						trainingData.setMyCards(new ArrayList<Card>(player.getHandDeck().getDeck()));

						List<Meld> myMelds = new ArrayList<Meld>(getTeamMeldsByID(player.getTeamID(), true));
						List<Meld> yourMelds = new ArrayList<Meld>(getTeamMeldsByID(player.getTeamID(), false));

						// Meldung müssen kompatibel zu den Netzen sein -> CardValue.value >= 3
						if (areCompatible(myMelds) && areCompatible(yourMelds)) {

							trainingData.setMyMelds(myMelds);
							trainingData.setYourMelds(yourMelds);
							trainingData.setDiscardPile(new ArrayList<Card>(discardPile));

							PlayerMoveType pmt = playerMove.getPlayerMoveType();
							List<Card> cards = playerMove.getCards();
							CardValue cvMeld = null;

							boolean useful = true;

							// Verarbeitung der Züge + Output der Trainingsdaten
							switch (pmt) {
							case DRAW_CARD:
								player.addCard(cards.get(0));
								drew = true;

								trainingData.setDrawHidden(false);
								trainingData.setNetType(NetType.DRAW);
								break;
							case ADD_DISCARD_TO_HAND:
								for (Card card : cards) {
									player.addCard(card);
									removeCardFromDiscardPile(card);
								}
								drew = true;

								trainingData.setDrawHidden(true);
								trainingData.setNetType(NetType.DRAW);
								break;
							case CREATING_MELD:
								cvMeld = stateProcessStart.cardValueByDeckID(player.getTeamID(),
										playerMove.getTargetDeckID());
								moveCardsToMelds(player, cards, cvMeld);
								melded = true;

								trainingData.setMelding(true);
								trainingData.setMeldingValue(cvMeld);
								trainingData.setMeldingWildAmount(wildCardAmout(playerMove.getCards()));
								trainingData.setNetType(NetType.MELD);
								break;
							case ADDING_CARD_TO_MELD:
								cvMeld = stateProcessStart.cardValueByDeckID(player.getTeamID(),
										playerMove.getTargetDeckID());
								moveCardsToMelds(player, cards, cvMeld);
								added = true;

								trainingData.setAdding(true);
								trainingData.setAddingValue(cvMeld);
								trainingData.setAddingWildAmount(wildCardAmout(playerMove.getCards()));
								trainingData.setNetType(NetType.ADD);
								break;
							case DROP_CARD:
								player.removeCard(cards.get(0));
								discardPile.add(cards.get(0));

								trainingData.setDiscardValue(CardValue.findByValue(cards.get(0).getValue()));
								trainingData.setNetType(NetType.DROP);
								break;
							default:
								useful = false;
								break;
							}
							
							// Manche Züge sind für die KI nicht von Bedeutung
							if (useful) {
								// Aktionen vor dem Ziehen von Karten sind nicht von Bedeutung
								// außerdem werden wilde Meldungen nicht beachtet
								if (drew && (cvMeld == null || cvMeld.getValue() >= 3)) {
									trainingDataList.add(trainingData);
								}

								if (pmt == PlayerMoveType.DROP_CARD) {
									// Falls dieser Zug ohne Meldung bzw. Hinzufügen war,
									// wird dies in den Trainingsdaten dokumentiert
									if (!melded) {
										TrainingData tdMeld = trainingData.copy(true);
										tdMeld.setMelding(false);
										tdMeld.setNetType(NetType.MELD_IF);
										trainingDataList.add(tdMeld);
									}
									if (!added) {
										TrainingData tdAdd = trainingData.copy(true);
										tdAdd.setAdding(false);
										tdAdd.setNetType(NetType.ADD_IF);
										trainingDataList.add(tdAdd);
									}
								}
							}
						}
					}
				}
			}
		}

		for (TrainingData trainingData : trainingDataList) {
			trainingData.toVectorAndWrite();
		}
	}

	/**
	 * Die Spieler werden initialisiert.
	 * 
	 * @param playersList alle Spieler
	 */
	private void fillPlayer(List<Player> playersList) {
		playerOne = playersList.get(0);
		playerTwo = playersList.get(1);
		playerThree = playersList.get(2);
		playerFour = playersList.get(3);
	}

	/**
	 * Die Teams werden initialisiert + Meldungen geleert.
	 * 
	 * @param teamsList alle Teams
	 */
	private void fillTeams(List<Team> teamsList) {
		teamOneID = teamsList.get(0).getTeamID();
		teamTwoID = teamsList.get(1).getTeamID();
		teamOneMelds = new ArrayList<Meld>();
		teamTwoMelds = new ArrayList<Meld>();
	}

	/**
	 * Fasst die Moves im Objekt Turn zu PlayerMoves zusammen.
	 * 
	 * @param turn
	 * @return
	 */
	private List<PlayerMove> extractPlayerMoves(Turn turn) {
		List<PlayerMove> playerMoves = new ArrayList<PlayerMove>();
		PlayerMoveType pmtPrev = null;
		PlayerMoveType pmtNext;

		PlayerMove playerMove = null;

		List<Move> moves = turn.getMoves();
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);

			PlayerMoveType pmt = PlayerMoveType.findByValue(move.getPlayerMoveType());

			if (pmt != PlayerMoveType.UNDEFINED) {

				pmtNext = i + 1 <= moves.size() - 1 ? PlayerMoveType.findByValue(moves.get(i + 1).getPlayerMoveType())
						: null;

				if (pmtPrev != pmt) {
					playerMove = new PlayerMove();
					playerMove.setPlayerMoveType(pmt);
					playerMove.setPlayer(getPlayerByID(move.getPlayerId()));
					playerMove.setTargetDeckID(move.getTargetDeckID());
				}
				playerMove.addCard(move.getCard());

				// Der nächste Move ist unterschiedlich zum Momentanen, also abgeschlossen
				if (pmt != pmtNext) {
					playerMoves.add(playerMove);
				}
			}
			pmtPrev = pmt;
		}
		return playerMoves;
	}

	/**
	 * Ermittelt mittels einer ID den dazugehörigen Spieler.
	 * 
	 * @param id
	 * @return
	 */
	private Player getPlayerByID(int id) {
		Player p = null;

		if (playerOne.getPlayerID() == id) {
			p = playerOne;
		} else if (playerTwo.getPlayerID() == id) {
			p = playerTwo;
		} else if (playerThree.getPlayerID() == id) {
			p = playerThree;
		} else if (playerFour.getPlayerID() == id) {
			p = playerFour;
		}
		return p;
	}

	/**
	 * Ermittelt mittels einer Team ID die (nicht) dazugehörigen Meldungen.
	 * 
	 * @param id
	 * @param my
	 * @return
	 */
	private List<Meld> getTeamMeldsByID(int id, boolean my) {
		List<Meld> melds = null;

		if (id == teamOneID) {
			melds = my ? teamOneMelds : teamTwoMelds;
		} else if (id == teamTwoID) {
			melds = my ? teamTwoMelds : teamOneMelds;
		}
		return melds;
	}

	/**
	 * Entfernt die übergebene Karte aus dem Ablagestapel.
	 * 
	 * @param card
	 * @return
	 */
	private Card removeCardFromDiscardPile(Card card) {
		Card removedCard = null;

		for (int i = 0; i < discardPile.size() && removedCard == null; i++) {
			if (discardPile.get(i).isEqual(card))
				removedCard = discardPile.remove(i);
		}

		return removedCard;
	}

	/**
	 * Zieht mehrere Karten von einem Spieler zu einer Meldung.
	 * 
	 * @param player
	 * @param cards
	 * @param cvMeld
	 */
	private void moveCardsToMelds(Player player, List<Card> cards, CardValue cvMeld) {
		for (Card card : cards) {
			player.removeCard(card);
			addCardToMeld(card, cvMeld, player.getTeamID());
		}
	}

	/**
	 * Fügt die übergene Karte zu einer bestimmten Meldung hinzu, also erhöht den
	 * Zähler der normalen oder wilden Karten hoch.
	 * 
	 * @param card
	 * @param cvMeld
	 * @param teamID
	 */
	private void addCardToMeld(Card card, CardValue cvMeld, int teamID) {
		Meld meld = getMeldByValue(cvMeld, teamID);
		CardValue cardValue = CardValue.findByValue(card.getValue());

		if (meld != null) {

			if (cardValue == CardValue.JOKER || cardValue == CardValue.TWO) {
				meld.setWildCards(meld.getWildCards() + 1);
			} else {
				meld.setNormalCards(meld.getNormalCards() + 1);
			}
		} else {
			meld = new Meld(cvMeld);

			if (cardValue == CardValue.JOKER || cardValue == CardValue.TWO) {
				meld.setWildCards(meld.getWildCards() + 1);
			} else {
				meld.setNormalCards(meld.getNormalCards() + 1);
			}

			getTeamMeldsByID(teamID, true).add(meld);
		}
	}

	/**
	 * Ermittelt ein Meld Objekt mittels eines CardValue und einer Team ID.
	 * 
	 * @param cardValue
	 * @param teamID
	 * @return
	 */
	private Meld getMeldByValue(CardValue cardValue, int teamID) {

		for (Meld meld : getTeamMeldsByID(teamID, true)) {
			if (meld.getValue() == cardValue) {
				return meld;
			}
		}
		return null;
	}

	/**
	 * Hilfsfunktion bestimmt die Anzahl von wilden Karten in den übergebenen
	 * Karten.
	 * 
	 * @param cards
	 * @return
	 */
	private static int wildCardAmout(List<Card> cards) {
		int amount = 0;
		for (Card card : cards) {
			if (card.isWild())
				amount++;
		}
		return amount;
	}

	/**
	 * Hilfsfunktion bestimmt ob alle übergebenen Meldung kompatibel zu den Netzen
	 * sind -> CardValue.value >= 3
	 * 
	 * @param melds
	 * @return
	 */
	private static boolean areCompatible(List<Meld> melds) {
		boolean compatible = true;

		for (int i = 0; i < melds.size() && compatible; i++) {
			compatible = melds.get(i).getValue().getValue() >= 3;
		}
		return compatible;
	}
}