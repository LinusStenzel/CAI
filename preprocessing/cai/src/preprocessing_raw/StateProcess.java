package preprocessing_raw;

import java.util.ArrayList;
import java.util.List;

import preprocessing_raw.Card.CardValue;

/**
 * Repräsentiert den Zustand eines Canasta Spiels zu einen Zeitpunkt.
 * 
 * @author linusstenzel
 *
 */
public class StateProcess {

	private List<Player> canastaPlayerList = new ArrayList<Player>();
	private List<Team> teamsList = new ArrayList<Team>();

	/**
	 * Ablegestapel
	 */
	private Deck discardDeck;
	/**
	 * Aufnahmestapel
	 */
	private Deck pileDeck;

	public List<Player> getCanastaPlayerList() {
		return canastaPlayerList;
	}

	public void setCanastaPlayerList(List<Player> canastaPlayerList) {
		this.canastaPlayerList = canastaPlayerList;
	}

	public List<Team> getTeamsList() {
		return teamsList;
	}

	public void setTeamsList(List<Team> teamsList) {
		this.teamsList = teamsList;
	}

	public Deck getDiscardDeck() {
		return discardDeck;
	}

	public void setDiscardDeck(Deck discardDeck) {
		this.discardDeck = discardDeck;
	}

	public Deck getPileDeck() {
		return pileDeck;
	}

	public void setPileDeck(Deck pileDeck) {
		this.pileDeck = pileDeck;
	}

	public boolean addPlayer(Player player) {
		return canastaPlayerList.add(player);
	}

	public boolean addTeam(Team team) {
		return teamsList.add(team);
	}

	/**
	 * Bestimmt CardValue eines bestimmten Decks(/Meldung) mittels der Deck ID.
	 * 
	 * @param teamID
	 * @param deckID
	 * @return
	 */
	public CardValue cardValueByDeckID(int teamID, int deckID) {
		List<Deck> teamMelds = getTeamByID(teamID).getTeamMelds();

		for (Deck deck : teamMelds) {
			if (deck.getDeckID() == deckID)
				return CardValue.valueOf(deck.getMeldKeyName());
		}
		return null;
	}

	/**
	 * Gibt ein Team mittels ID zurück.
	 * 
	 * @param teamID
	 * @return
	 */
	private Team getTeamByID(int teamID) {
		for (Team team : teamsList) {
			if (team.getTeamID() == teamID)
				return team;
		}
		return null;
	}
}