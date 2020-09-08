package preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import preprocessing_raw.Card;
import preprocessing_raw.Card.CardValue;
import preprocessing_raw.Util;

public class TrainingData {

	public enum NetType {
		DRAW, MELD, MELD_IF, ADD, ADD_IF, DROP;
	}

	private NetType netType;

	/*
	 * Input
	 */
	private List<Card> myCards;
	private List<Meld> myMelds;
	private List<Meld> yourMelds;
	private List<Card> discardPile;

	/*
	 * Mögliche Outputs
	 */
	private boolean drawHidden;
	private boolean isMelding;
	private CardValue meldingValue;
	private int meldingWildAmount;
	private boolean isAdding;
	private CardValue addingValue;
	private int addingWildAmount;
	private CardValue discardValue;

	/*
	 * Writer für Input Vektoren je Netz
	 */
	private static BufferedWriter drawIn;
	private static BufferedWriter meldIfIn;
	private static BufferedWriter meldValueIn;
	private static BufferedWriter meldWildIn;
	private static BufferedWriter addIfIn;
	private static BufferedWriter addValueIn;
	private static BufferedWriter addWildIn;
	private static BufferedWriter dropIn;

	/*
	 * Writer für Output Vektoren je Netz
	 */
	private static BufferedWriter drawOut;
	private static BufferedWriter meldIfOut;
	private static BufferedWriter meldValueOut;
	private static BufferedWriter meldWildOut;
	private static BufferedWriter addIfOut;
	private static BufferedWriter addValueOut;
	private static BufferedWriter addWildOut;
	private static BufferedWriter dropOut;

	static {
		clearCSV();
		try {
			// alle Writer initialsieren
			drawIn = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "input/draw.csv")));
			meldIfIn = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "input/meld_if.csv")));
			meldValueIn = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "input/meld_value.csv")));
			meldWildIn = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "input/meld_wild.csv")));
			addIfIn = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "input/add_if.csv")));
			addValueIn = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "input/add_value.csv")));
			addWildIn = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "input/add_wild.csv")));
			dropIn = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "input/drop.csv")));

			drawOut = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "output/draw.csv")));
			meldIfOut = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "output/meld_if.csv")));
			meldValueOut = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "output/meld_value.csv")));
			meldWildOut = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "output/meld_wild.csv")));
			addIfOut = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "output/add_if.csv")));
			addValueOut = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "output/add_value.csv")));
			addWildOut = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "output/add_wild.csv")));
			dropOut = new BufferedWriter(new FileWriter(new File(Util.DATA_PATH + "output/drop.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Leert bzw. löscht die CSV Dateien der Trainingsdaten.
	 */
	private static void clearCSV() {
		String[] pathNames = new String[] { "input/", "output/" };

		for (String pName : pathNames) {
			File dir = new File(Util.DATA_PATH + pName);
			for (File file : dir.listFiles())
				if (!file.isDirectory())
					file.delete();
		}
	}

	/**
	 * Schließt alle Writer.
	 */
	public static void closeWriters() {
		try {
			drawIn.close();
			meldIfIn.close();
			meldValueIn.close();
			meldWildIn.close();
			addIfIn.close();
			addValueIn.close();
			addWildIn.close();
			dropIn.close();

			drawOut.close();
			meldIfOut.close();
			meldValueOut.close();
			meldWildOut.close();
			addIfOut.close();
			addValueOut.close();
			addWildOut.close();
			dropOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public NetType getNetType() {
		return netType;
	}

	public void setNetType(NetType netType) {
		this.netType = netType;
	}

	public List<Card> getMyCards() {
		return myCards;
	}

	public void setMyCards(List<Card> myCards) {
		this.myCards = myCards;
	}

	public List<Meld> getMyMelds() {
		return myMelds;
	}

	public void setMyMelds(List<Meld> myMelds) {
		this.myMelds = myMelds;
	}

	public List<Meld> getYourMelds() {
		return yourMelds;
	}

	public void setYourMelds(List<Meld> yourMelds) {
		this.yourMelds = yourMelds;
	}

	public List<Card> getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(List<Card> discardPile) {
		this.discardPile = discardPile;
	}

	public Boolean getDrawHidden() {
		return drawHidden;
	}

	public boolean isMelding() {
		return isMelding;
	}

	public void setMelding(boolean isMelding) {
		this.isMelding = isMelding;
	}

	public CardValue getMeldingValue() {
		return meldingValue;
	}

	public void setMeldingValue(CardValue meldingValue) {
		this.meldingValue = meldingValue;
	}

	public int getMeldingWildAmount() {
		return meldingWildAmount;
	}

	public void setMeldingWildAmount(int meldingWildAmount) {
		this.meldingWildAmount = meldingWildAmount;
	}

	public boolean isAdding() {
		return isAdding;
	}

	public void setAdding(boolean isAdding) {
		this.isAdding = isAdding;
	}

	public CardValue getAddingValue() {
		return addingValue;
	}

	public void setAddingValue(CardValue addingValue) {
		this.addingValue = addingValue;
	}

	public int getAddingWildAmount() {
		return addingWildAmount;
	}

	public void setAddingWildAmount(int addingWildAmount) {
		this.addingWildAmount = addingWildAmount;
	}

	public CardValue getDiscardValue() {
		return discardValue;
	}

	public void setDiscardValue(CardValue discardValue) {
		this.discardValue = discardValue;
	}

	public void setDrawHidden(boolean drawHidden) {
		this.drawHidden = drawHidden;
	}

	/**
	 * Generiert aus diesem TrainingData alle möglichen Input und Output Vektoren
	 * und schreibt sie in die dazugehörigen CSV Dateien.
	 */
	public void toVectorAndWrite() {
		int[] input = getInput();
		int[] output;

		switch (netType) {
		case DRAW:
			output = new int[1];
			output[0] = drawHidden ? 1 : 0;
			writeToCSV(1, input, output);
			break;
		case MELD:
			output = new int[11];
			output[meldingValue.getValue() - 3]++;
			writeToCSV(3, input, output);

			output = new int[5];
			output[meldingWildAmount <= 4 ? meldingWildAmount : 4]++;
			writeToCSV(4, input, output);
		case MELD_IF:
			output = new int[1];
			output[0] = isMelding ? 1 : 0;
			writeToCSV(2, input, output);
			break;
		case ADD:
			output = new int[11];
			output[addingValue.getValue() - 3]++;
			writeToCSV(6, input, output);

			output = new int[5];
			output[addingWildAmount <= 4 ? addingWildAmount : 4]++;
			writeToCSV(7, input, output);
		case ADD_IF:
			output = new int[1];
			output[0] = isAdding ? 1 : 0;
			writeToCSV(5, input, output);
			break;
		case DROP:
			output = new int[13];
			if (discardValue == CardValue.JOKER || discardValue == CardValue.TWO) {
				output[0]++;
			} else {
				output[discardValue.getValue() - 1]++;
			}
			writeToCSV(8, input, output);
			break;
		default:
			break;
		}
	}

	/**
	 * Generiert aus bestimmeten Membern der Klasse den Input Vektor.
	 * 
	 * @return
	 */
	private int[] getInput() {
		int[] myCardsVec = cardsToVec(myCards);
		int[] myMeldsVec = meldsToVec(myMelds);
		int[] yourMeldsVec = meldsToVec(yourMelds);
		int[] discardPileVec = cardsToVec(discardPile);

		int[] inputVec = new int[70];
		int inputVecIdx = 0;

		for (int num : myCardsVec) {
			inputVec[inputVecIdx] = num;
			inputVecIdx++;
		}
		for (int num : myMeldsVec) {
			inputVec[inputVecIdx] = num;
			inputVecIdx++;
		}
		for (int num : yourMeldsVec) {
			inputVec[inputVecIdx] = num;
			inputVecIdx++;
		}
		for (int num : discardPileVec) {
			inputVec[inputVecIdx] = num;
			inputVecIdx++;
		}
		return inputVec;
	}

	/**
	 * Generiert aus den übergebenen Karten einen 13 dimensionalen Vektor.
	 * 
	 * [0] Anzahl an wilden Karten. [1] Anzahl Dreien. [2] Anzahl Vieren. ...
	 * 
	 * @param cards
	 * @return
	 */
	private int[] cardsToVec(List<Card> cards) {
		int[] vec = new int[13];

		for (Card card : cards) {
			if (card.isWild()) {
				vec[0]++;
			} else {
				vec[card.getValue() - 1]++;
			}
		}
		return vec;
	}

	/**
	 * Generiert aus den übergebenen Meldungen einen 22 dimensionalen Vektor.
	 * 
	 * [0][1] Anzahl Vieren, aufgeteilt in normale und wilde Karten. ...
	 * 
	 * @param melds
	 * @return
	 */
	private int[] meldsToVec(List<Meld> melds) {
		int[][] vecMulti = new int[11][2];
		int[] vec = new int[22];

		for (Meld meld : melds) {
			CardValue cvMeld = meld.getValue();
			vecMulti[cvMeld.getValue() - 3][0] = meld.getNormalCards();
			vecMulti[cvMeld.getValue() - 3][1] = meld.getWildCards();
		}

		int vecIdx = 0;

		for (int i = 0; i < vecMulti.length; i++) {
			for (int j = 0; j < vecMulti[i].length; j++) {
				vec[vecIdx] = vecMulti[i][j];
				vecIdx++;
			}
		}
		return vec;
	}

	/**
	 * Schreibt die beiden übergebene Vektor in zwei CSV Datei anhand der NetNumber.
	 * 
	 * @param netNumber
	 * @param input
	 * @param output
	 */
	private void writeToCSV(int netNumber, int[] input, int[] output) {
		BufferedWriter in = null;
		BufferedWriter out = null;

		switch (netNumber) {
		case 1:
			in = drawIn;
			out = drawOut;
			break;
		case 2:
			in = meldIfIn;
			out = meldIfOut;
			break;
		case 3:
			in = meldValueIn;
			out = meldValueOut;
			break;
		case 4:
			in = meldWildIn;
			out = meldWildOut;
			break;
		case 5:
			in = addIfIn;
			out = addIfOut;
			break;
		case 6:
			in = addValueIn;
			out = addValueOut;
			break;
		case 7:
			in = addWildIn;
			out = addWildOut;
			break;
		case 8:
			in = dropIn;
			out = dropOut;
			break;
		default:
			break;
		}

		try {
			String inputStr = Arrays.toString(input);
			inputStr = inputStr.substring(1);
			inputStr = inputStr.substring(0, inputStr.length() - 1);
			in.write(inputStr + "\n");

			String outputStr = Arrays.toString(output);
			outputStr = outputStr.substring(1);
			outputStr = outputStr.substring(0, outputStr.length() - 1);
			out.write(outputStr + "\n");

			in.flush();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Kopiert diese TrainingData Instanz.
	 * 
	 * @param onlyInput
	 * @return
	 */
	public TrainingData copy(boolean onlyInput) {
		TrainingData copy = new TrainingData();

		copy.setMyCards(new ArrayList<Card>(myCards));
		copy.setMyMelds(new ArrayList<Meld>(myMelds));
		copy.setYourMelds(new ArrayList<Meld>(yourMelds));
		copy.setDiscardPile(new ArrayList<Card>(discardPile));

		if (!onlyInput) {
			copy.setDrawHidden(drawHidden);
			copy.setMelding(isMelding);
			copy.setMeldingValue(meldingValue);
			copy.setMeldingWildAmount(meldingWildAmount);
			copy.setAdding(isAdding);
			copy.setAddingValue(addingValue);
			copy.setAddingWildAmount(addingWildAmount);
			copy.setDiscardValue(discardValue);

		}
		return copy;
	}
}