package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import preprocessing_raw.Deck;
import preprocessing_raw.Game;
import preprocessing_raw.Move;
import preprocessing_raw.Player;
import preprocessing_raw.Round;
import preprocessing_raw.StateProcess;
import preprocessing_raw.Team;
import preprocessing_raw.Turn;
import preprocessing_raw.Util;

/**
 * Klasse zum Einlesen der Canasta Logs in Objekte.
 * 
 * @author linusstenzel
 *
 */
public class JsonParser {

	/**
	 * Gson Klasse für leichteres Parsen des "Inner Core" der Logs.
	 */
	private static Gson gson = new Gson();

	/**
	 * Liest ein einzelnes Spiel ein und füllt damit ein Game Objekt bzw. dessen
	 * Runden. Falls eine Runde keinen Startzustand besitzt, wird eine JSONException
	 * geschmissen.
	 * 
	 * @param gameJSON
	 * @return
	 */
	public Game readGame(JSONObject gameJSON) {
		Game game = new Game();

		JSONObject roundJSON;
		int roundNumber = 0;
		boolean foundRound = true;

		do {
			try {
				roundJSON = gameJSON.getJSONObject(Util.ROUND + twoDigits(roundNumber));
			} catch (JSONException e) {
				foundRound = false;
				roundJSON = null;
			}

			if (foundRound) {
				try {
					game.addRound(readRound(roundJSON));
				} catch (JSONException e) {
					throw new JSONException(e.getMessage());
				}
				roundNumber++;
			}
		} while (foundRound);

		return game;
	}

	/**
	 * Liest eine einzelne Runde ein und füllt damit ein Round Objekt bzw. dessen
	 * Züge und Startzustand. Falls kein Startzustand vorhanden ist, wird eine
	 * JSONException geschmissen.
	 * 
	 * @param roundJSON
	 * @return
	 */
	private Round readRound(JSONObject roundJSON) {
		Round round = new Round();

		JSONObject turnJSON;
		int turnNumber = 0;
		boolean foundTurn = true;

		do {
			try {
				turnJSON = roundJSON.getJSONObject(Util.MOVES_AT_TURN + twoDigits(turnNumber));
			} catch (JSONException e) {
				foundTurn = false;
				turnJSON = null;
			}

			if (foundTurn) {
				round.addTurn(readTurn(turnJSON));
				turnNumber++;
			} else {
				JSONObject stateProcessStartJSON;
				try {
					stateProcessStartJSON = roundJSON.getJSONObject(Util.STATE_PROCESS_START + twoDigits(0));
					round.setStateProcessStart(readStateProcess(stateProcessStartJSON));
				} catch (JSONException e) {
					throw new JSONException("Missing \"STATE_PROCESS_START\" Object");
				}
			}
		} while (foundTurn);

		return round;
	}

	/**
	 * Liest einen einzelnen Zug ein und füllt damit ein Turn Objekt bzw. dessen
	 * Moves.
	 * 
	 * @param turnJSON
	 * @return
	 */
	private Turn readTurn(JSONObject turnJSON) {
		Turn turn = new Turn();

		JSONArray moveArr = turnJSON.getJSONArray("tableMovesList");

		Move move;
		for (Object m : moveArr) {

			move = gson.fromJson(((JSONObject) m).toString(), Move.class);
			turn.addMove(move);
		}
		return turn;
	}

	/**
	 * Liest einen Zustand ein und füllt damit ein StateProcess Objekt.
	 * 
	 * @param stateProcessJSON
	 * @return
	 */
	private StateProcess readStateProcess(JSONObject stateProcessJSON) {
		StateProcess stateProcess = new StateProcess();

		stateProcess.setDiscardDeck(gson.fromJson(stateProcessJSON.get("discardDeck").toString(), Deck.class));
		stateProcess.setPileDeck(gson.fromJson(stateProcessJSON.get("pileDeck").toString(), Deck.class));

		JSONArray playerArr = stateProcessJSON.getJSONArray("canastaPlayersList");
		for (Object p : playerArr) {
			stateProcess.addPlayer((gson.fromJson(((JSONObject) p).toString(), Player.class)));
		}

		JSONArray teamArr = stateProcessJSON.getJSONArray("teamsList");
		Team team;

		for (Object t : teamArr) {
			team = gson.fromJson(((JSONObject) t).toString(), Team.class);

			JSONArray teamMeldsValues = ((JSONObject) ((JSONObject) t).get("teamMelds")).getJSONArray("_Values");

			for (Object tm : teamMeldsValues) {
				team.addTeamMeld(gson.fromJson(((JSONObject) tm).toString(), Deck.class));
			}
			stateProcess.addTeam(team);
		}
		return stateProcess;
	}

	/**
	 * Hilfsmethode um eine beliebigen Zahl in eine zweistellige in
	 * Stringdarstellung zu konvertieren.
	 * 
	 * @param num
	 * @return
	 */
	private static String twoDigits(int num) {
		String twoDigits = String.valueOf(num);

		if (twoDigits.length() <= 1) {
			twoDigits = "0" + twoDigits;
		} else if (twoDigits.length() > 2) {
			twoDigits = "99";
		}
		return twoDigits;
	}
}