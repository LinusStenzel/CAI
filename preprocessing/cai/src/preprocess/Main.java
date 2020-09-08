package preprocess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import json.JsonParser;
import preprocessing_raw.Game;
import preprocessing_raw.Util;

public class Main {

	private static JsonParser jsonParser = new JsonParser();

	private static List<Game> rawGames = new ArrayList<Game>();

	public static void main(String[] args) {

		for (String jsonName : new String[] { "FOUR_PLAYERS_15-11", "FOUR_PLAYERS_16-11", "FOUR_PLAYERS_17-11",
				"FOUR_PLAYERS_18-11" }) {
			
			String fileStr;
			try {
				fileStr = new String(Files.readAllBytes(Paths.get(Util.DATA_PATH + jsonName + ".json")));
			} catch (IOException e) {
				fileStr = null;
			}

			JSONObject gamesJSON = new JSONObject(fileStr);

			List<String> keys = new ArrayList<String>(gamesJSON.keySet());

			for (String key : keys) {
				try {
					rawGames.add(jsonParser.readGame((JSONObject) gamesJSON.get(key)));
				} catch (JSONException e) {
					// Spiele bspw. ohne Startzustand werden nicht beachtet
					System.err.println("Game with wrong format was not used");
				}

			}

		}

		GameHistory gh = new GameHistory(rawGames);

		gh.generateTrainingData();
		TrainingData.closeWriters();
	}
}