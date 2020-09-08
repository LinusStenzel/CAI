using System;
using System.Collections.Generic;

using Keras.Models;
using Numpy;
using SmartyCanasta.Logic;

namespace CAI
{
    //Info: Melds with the values Joker, Two or Three cant be created or added cards to. 
    //This comes from the meldValueModel/addValueModel output dimension (11) representing the values Four to Ace.

    class Program
    {

        static void Main(string[] args)
        {
            Environment.SetEnvironmentVariable("PATH", @"C:\Users\Linus\AppData\Local\Programs\Python\Python36", EnvironmentVariableTarget.Process);

            List<Card> myCards = new List<Card>();
            List<Card>[] myMelds = new List<Card>[11];
            List<Card>[] yourMelds = new List<Card>[11];
            List<Card> discardPile = new List<Card>();

            Card clubAce = new Card(Card.CardSuit.CLUB, Card.CardValue.ACE);
            Card clubFour = new Card(Card.CardSuit.CLUB, Card.CardValue.FOUR);
            Card heartTwo = new Card(Card.CardSuit.HEART, Card.CardValue.TWO);
            Card heartJoker = new Card(Card.CardSuit.HEART, Card.CardValue.JOKER);

            myCards.Add(clubAce);
            myCards.Add(clubFour);
            myCards.Add(heartTwo);
            myCards.Add(heartJoker);
            
            
            myMelds[10] = new List<Card>() { clubAce, clubAce, heartJoker};
            yourMelds[0] = new List<Card>() { clubFour, clubFour, heartJoker };
            discardPile.Add(clubFour);

            //---------------------------------------------
            //EXAMPLE - happens in AILogic in Unity project

            Predictions preds = new Predictions(@"C:\Users\Linus\Desktop\h5\", myCards, myMelds, yourMelds, discardPile);

            bool drawHidden = preds.Draw(true);
            List<Card> drawn = new List<Card>();
            if (drawHidden)
            {
                //drawn = top card from hidden pile
            } else
            {
                //drawn = discard pile
            }
            preds.AddMyCards(drawn);

            List<Card>[] toMeld = preds.Meld(50);
            //add melds of toMeld to SCGameState
            List<Card>[] toAdd = preds.Add();
            //add melds(adds) of toAdd to SCGameState
            Card toDrop = preds.Drop();
            //drop card
            //---------------------------------------------
        }
    }

    public class Predictions
    {
        private BaseModel drawModel;
        private BaseModel meldIfModel;
        private BaseModel meldValueModel;
        private BaseModel meldWildModel;
        private BaseModel addIfModel; 
        private BaseModel addValueModel;
        private BaseModel addWildModel;
        private BaseModel dropModel;

        /**
         * AI input numpy array.
         */
        private NDarray input = np.empty(1, 70);

        /**
         * Cards player holds.
         */
        private List<Card> myCards;
        /**
         * Melds of player. 
         */
        private List<Card>[] myMelds;
        /**
         * Melds of opponent player(s). 
         */
        private List<Card>[] yourMelds;
        /**
         * Discard pile cards.
         */
        private List<Card> discardPile;

        /**
         * Set given variables and fill input numpy array.
         * 
         * @param modelPath     Fully qualified path of .h5 files
         * @param myCards       List of my cards
         * @param myMelds       Array with the length 11. Including CardValue FOUR 
         *                      it holds one List for each ascending CardValue ending with ACE.
         * @param yourMelds     Array with the length 11. Including CardValue FOUR 
         *                      it holds one List for each ascending CardValue ending with ACE.
         * @param discardPile   List of cards in discard pile
         * 
         */
        public Predictions(String modelPath, List<Card> myCards, List<Card>[] myMelds,
                            List<Card>[] yourMelds, List<Card> discardPile)
        {
            //loadíng models
            drawModel = BaseModel.LoadModel(modelPath + "draw.h5");
            meldIfModel = BaseModel.LoadModel(modelPath + "meld_if.h5");
            meldValueModel = BaseModel.LoadModel(modelPath + "meld_value.h5");
            meldWildModel = BaseModel.LoadModel(modelPath + "meld_wild.h5");
            addIfModel = BaseModel.LoadModel(modelPath + "add_if.h5");
            addValueModel = BaseModel.LoadModel(modelPath + "add_value.h5");
            addWildModel = BaseModel.LoadModel(modelPath + "add_wild.h5");
            dropModel = BaseModel.LoadModel(modelPath + "drop.h5");

            this.myCards = myCards;
            this.myMelds = myMelds;
            this.yourMelds = yourMelds;
            this.discardPile = discardPile;
            FillInput();
        }

        private void FillInput()
        {
            List<int> allList = new List<int>(CardsToVec(myCards));
            allList.AddRange(MeldsToVec(myMelds));
            allList.AddRange(MeldsToVec(yourMelds));
            allList.AddRange(CardsToVec(discardPile));
            input[0] = allList.ToArray();
        }

        /**
        * Use draw net to predict wether to draw hidden card or discard pile.
        * @param discardPossible true, if drawing the discard pile is possible
        * @return true -> draw hidden, false -> draw discard pile
        */
        public bool Draw(bool discardPossible)
        {
            bool draw;

            //get raw prediction and convert vector to boolean
            NDarray pred = drawModel.Predict(input)[0];
            float value = pred.item<float>();
            draw = Math.Round(value) == 1;

            return !discardPossible || draw;
        }

        /**
         * Add given cards to player cards.
         * @param cards Cards to add
         */
        public void AddMyCards(List<Card> cards)
        {
            myCards.AddRange(cards);
            FillInput();
        }

        /**
        * Use meldIf net to predict wether to meld cards or not. If yes, meld value and (wild)card amount
        * get predicted by (meld)value/wild net. Runs as long AI wants(= prediction) to and also can(=cards) meld.
        * @param minMeldPoints Minimal points needed to be a valid meld
        * @return All melds to be made. Every array element is representing one meld with a list of cards.
        * The element at index == 0 represents the meld with CardValue == Four, index == 1 -> CardValuue == Five, ...
        * When an array element == null, there are no cards of this CardValue to be meld.  
        */
        public List<Card>[] Meld(int minMeldPoints = 0)
        {
            List<Card>[] toMeld = new List<Card>[11];
            int meldPoints = 0;

            //get raw "if" prediction and convert vector to boolean 
            NDarray predIf = meldIfModel.Predict(input)[0];
            float valueIf = predIf.item<float>();
            bool doMeld = Math.Round(valueIf) == 1;

            if (doMeld)
            {
                Card toMeldElem;
                do
                {
                    //get raw "value" predictions and copy it. when cutting of later the orginal one is not touched
                    NDarray predValue = meldValueModel.Predict(input)[0];
                    NDarray predValueCleaned = np.array(predValue);

                    //CardValues that the AI predicted but are not in the playershand
                    List<Card.CardValue> tried = new List<Card.CardValue>();
                    do
                    {
                        //convert prediction vector to CardValue
                        NDarray argMaxValue = np.argmax(predValueCleaned);
                        int valueValue = argMaxValue.item<int>();
                        Card.CardValue tryMeld = ValueToCardValue(valueValue + 3);
                        tryMeld = ValueToCardValue(CardValueToValue(tryMeld) + CountLowerOrSame(tried, tryMeld));

                        //check if player actually holds predicted CardValue  
                        toMeldElem = FindFirstCard(tryMeld);

                        //cuts out tried CardValue 
                        var sliceIdx = Numpy.Models.Slice.Index(CardValueToValue(tryMeld) - 3 - CountLowerOrSame(tried, tryMeld));
                        predValueCleaned = np.delete(predValueCleaned, sliceIdx);
                        tried.Add(tryMeld);
                    }
                    while (predValueCleaned.size > 0 && (toMeldElem == null || !CanMeld(toMeldElem.value)) );

                    if (toMeldElem != null)
                    {
                        //get raw "wild" prediction and convert vector to wild card amount. also considering max wild cards
                        NDarray predWild = meldWildModel.Predict(input)[0];
                        NDarray argMaxWild = np.argmax(predWild);
                        int wildCardAmount = WildCardAmount();
                        int cardAmount = CardAmount(toMeldElem.value, myCards);
                        int valueWild = argMaxWild.item<int>() <= wildCardAmount ? argMaxWild.item<int>() : wildCardAmount;



                        //correct if not enough cards or too many wild cards
                        if (valueWild + cardAmount < 3)
                        {
                            valueWild = 3 - cardAmount;
                        }
                        if (valueWild > cardAmount)
                        {
                            valueWild = cardAmount;
                        }

                        //build meld
                        List<Card> meld = AllCardsOfCVs(new List<Card.CardValue>() { toMeldElem.value });
                        meld.AddRange(AllCardsOfCVs(new List<Card.CardValue>() { Card.CardValue.TWO, Card.CardValue.JOKER }, valueWild));

                        //update meld array and meld points
                        toMeld[CardValueToValue(toMeldElem.value) - 3] = meld;
                        meldPoints += MeldToPoints(meld);

                        //update myCards and myMelds
                        RunMeld(meld, toMeldElem.value);

                        //get raw "if" prediction (after updating input vector) and convert vector to boolean
                        FillInput();
                        predIf = meldIfModel.Predict(input)[0];
                        valueIf = predIf.item<float>();
                        doMeld = Math.Round(valueIf) == 1;
                    } else
                    {
                        doMeld = false;
                    }
                } while (doMeld);
            }
            return meldPoints >= minMeldPoints ? toMeld : new List<Card>[11];
        }

        /**
        * Use addIf net to predict wether to add cards or not. If yes, add value and (wild)card amount
        * get predicted by (add)value/wild net. Runs as long AI wants(= prediction) to and also can(=cards) add.
        * @return All melds(to be added to) to be made. Every array element is representing one meld(add) with a list of cards.
        * The element at index == 0 represents the meld(add) with CardValue == Four, index == 1 -> CardValuue == Five, ...
        * When an array element == null, there are no cards of this CardValue to be added.  
        */
        public List<Card>[] Add()
        {
            List<Card>[] toAdd = new List<Card>[11];

            //get raw "if" prediction and convert vector to boolean 
            NDarray predIf = addIfModel.Predict(input)[0];
            float valueIf = predIf.item<float>();
            bool doAdd = Math.Round(valueIf) == 1;

            if (doAdd)
            {
                do
                {
                    bool hasWildCards = WildCardAmount() > 0;

                    //get raw "value" predictions and copy it. when cutting of later the orginal one is not touched
                    NDarray predValue = addValueModel.Predict(input)[0];
                    NDarray predValueCleaned = np.array(predValue);

                    //CardValues that the AI predicted but are not in the playershand
                    List<Card.CardValue> tried = new List<Card.CardValue>();
                    Card.CardValue tryAdd;
                    do
                    {
                        //convert prediction vector to CardValue
                        NDarray argMaxValue = np.argmax(predValueCleaned);
                        int valueValue = argMaxValue.item<int>();
                        tryAdd = ValueToCardValue(valueValue + 3);
                        tryAdd = ValueToCardValue(CardValueToValue(tryAdd) + CountLowerOrSame(tried, tryAdd));

                        //cuts out tried CardValue 
                        var sliceIdx = Numpy.Models.Slice.Index(CardValueToValue(tryAdd) - 3 - CountLowerOrSame(tried, tryAdd));
                        predValueCleaned = np.delete(predValueCleaned, sliceIdx);
                        tried.Add(tryAdd);
                    }
                    while (predValueCleaned.size > 0 && (FindFirstCard(tryAdd) == null && !hasWildCards || myMelds[CardValueToValue(tryAdd) - 3] == null));

                    if (myMelds[CardValueToValue(tryAdd) - 3] != null && (FindFirstCard(tryAdd) != null || hasWildCards))
                    {
                        //get raw "wild" prediction and convert vector to wild card amount. also considering max wild cards
                        NDarray predWild = addWildModel.Predict(input)[0];
                        NDarray argMaxWild = np.argmax(predWild);
                        int wildCardAmount = WildCardAmount();
                        int valueWild = argMaxWild.item<int>() <= wildCardAmount ? argMaxWild.item<int>() : wildCardAmount;


                        int meldWildAmount = CardAmount(Card.CardValue.JOKER, myMelds[CardValueToValue(tryAdd) - 3]) +
                                            CardAmount(Card.CardValue.TWO, myMelds[CardValueToValue(tryAdd) - 3]);
                        int meldNaturalAmount = myMelds[CardValueToValue(tryAdd) - 3].Count - meldWildAmount;

                        //correct if too many wild cards 
                        if (meldWildAmount == 3)
                        {
                            valueWild = 0;
                        }
                        else if (valueWild + meldWildAmount > meldNaturalAmount)
                        {
                            valueWild = meldNaturalAmount - meldWildAmount;
                        }

                        //build meld
                        List<Card> add = AllCardsOfCVs(new List<Card.CardValue>() { tryAdd });
                        add.AddRange(AllCardsOfCVs(new List<Card.CardValue>() { Card.CardValue.TWO, Card.CardValue.JOKER }, valueWild));

                        //update meld array and meld points
                        toAdd[CardValueToValue(tryAdd) - 3] = add;

                        //update myCards and myMelds
                        RunMeld(add, tryAdd);

                        //get raw "if" prediction (after updating input vector) and convert vector to boolean
                        FillInput();
                        predIf = meldIfModel.Predict(input)[0];
                        valueIf = predIf.item<float>();
                        doAdd = Math.Round(valueIf) == 1;
                    }
                    else
                    {
                        doAdd = false;
                    }
                } while (doAdd);
            }
            return toAdd;
        }

        /**
         * Use drop net to predict card to drop. When the player does not hold the card,
         * the second highest prediction value gets chosen and so on.
         * @return Card to drop, identified by SUIT and VALUE
         */
        public Card Drop()
        {
            Card toDrop;
            //get raw predictions and copy it. when cutting of later the orginal one is not touched
            NDarray pred = dropModel.Predict(input)[0];
            NDarray predCleaned = np.array(pred);

            //CardValues that the AI predicted but are not in the playershand
            List<Card.CardValue> tried = new List<Card.CardValue>();
            do
            {
                //convert prediction vector to CardValue
                NDarray argMax = np.argmax(predCleaned);
                int value = argMax.item<int>();
                Card.CardValue tryDrop = value > 0 ? ValueToCardValue(value + 1) : Card.CardValue.JOKER;
                tryDrop = ValueToCardValue(CardValueToValue(tryDrop) + CountLowerOrSame(tried, tryDrop));

                //check if player actually holds predicted CardValue  
                toDrop = FindFirstCard(tryDrop);

                //JOKER stands for wild card in general -> also consider TWO
                if (tryDrop == Card.CardValue.JOKER)
                {
                    toDrop = toDrop ?? FindFirstCard(Card.CardValue.TWO);
                }

                //cuts out tried CardValue 
                var sliceIdx = Numpy.Models.Slice.Index(
                    tryDrop != Card.CardValue.JOKER ? CardValueToValue(tryDrop) - CountLowerOrSame(tried, tryDrop) - 1 : 0);
                predCleaned = np.delete(predCleaned, sliceIdx);
                tried.Add(tryDrop);
            }
            while (toDrop == null);

            return toDrop;
        }

        /**
         * Check if player is allowed to make meld of given CardValue, 
         * in terms of natural to wild card ratio.
         * @param cv CardValue of meld
         * @return True -> is allowed, False -> is not allowed
         */
        private bool CanMeld(Card.CardValue cv)
        {
            int natural = CardAmount(cv, myCards);
            int wild = WildCardAmount();
            return natural > 1 && (wild > 0 || natural > 2);
        }

        /**
         * Count wild card amount of player.
         * @return Amount of wild cards.
         */
        private int WildCardAmount()
        {
            int amount = 0;
            foreach (Card card in myCards)
            {
                amount += card.value == Card.CardValue.JOKER || card.value == Card.CardValue.TWO ? 1 : 0;
            }
            return amount;
        }

        /**
         * Collect all cards of player of given CardValues.
         * @param cvs       CardValues to consider
         * @param maxCards  Max card amount to be collected
         * @return Collected cards as List
         */
        private List<Card> AllCardsOfCVs(List<Card.CardValue> cvs, int maxCards = int.MaxValue)
        {
            List<Card> allCardsOfCV = new List<Card>();
            foreach (Card card in myCards)
            {
                if (cvs.Contains(card.value) && allCardsOfCV.Count < maxCards)
                {
                    allCardsOfCV.Add(card);
                }
            }
            return allCardsOfCV;
        }

        /**
         * Search for first card of player of given CardValue
         * @param cv CardValue to consider
         * @return First Card of given CardValue, Null if not found
         */
        private Card FindFirstCard(Card.CardValue cv)
        {
            foreach (Card card in myCards)
            {
                if (card.value == cv)
                {
                    return card;
                }
            }
            return null;
        }

        /**
         * Count amount of given cards of given CardValue
         * @param cv    CardValue to consider
         * @param cards Cards to count
         * @return Amount of cards of CardValue
         */
        private static int CardAmount(Card.CardValue cv, List<Card> cards)
        {
            int amount = 0;
            foreach (Card card in cards)
            {
                amount += card.value == cv ? 1 : 0;
            }
            return amount;
        }

        /**
         * Run given meld -> remove cards from player hand and add cards to player melds.
         * @param meld      Meld to run
         * @param meldValue CardValue of meld
         */
        private void RunMeld(List<Card> meld, Card.CardValue meldValue)
        {
            foreach (Card card in meld)
            {
                myCards.Remove(card);
            }
            myMelds[CardValueToValue(meldValue) - 3] = meld;
        }

        /**
         * Count card points of given meld.
         * @param meld Cards to be counted
         * @return On added card points
         */
        private static int MeldToPoints(List<Card> meld)
        {
            int points = 0;
            foreach (Card card in meld)
            {
                points += CardToPoints(card);
            }
            return points;
        }


         /**
          * Count amount of given cards of given or lower CardValue
          * @param cv       CardValue (or lower) to consider
          * @param cards    Cards to count
          * @return Amount of cards of CardValue or lower
          */
        private static int CountLowerOrSame(List<Card.CardValue> values, Card.CardValue cv)
        {
            int count = 0;
            foreach (Card.CardValue value in values)
            {
                count += CardValueToValue(value) <= CardValueToValue(cv) ? 1 : 0;
            }
            return count;
        }

        /**
         * Generates a 13 dimensional array from given cards. Where
         * [0] count of wild cards, [1] count of 3s, [2] count of 4s ...
         * @param cards Cards to count
         * @return 13 dimensional array containing card amounts
         */
        private static int[] CardsToVec(List<Card> cards)
        {
            int[] vec = new int[13];
            foreach (Card card in cards)
            {
                if (card.value == Card.CardValue.JOKER || card.value == Card.CardValue.TWO)
                {
                    vec[0]++;
                }
                else
                {
                    vec[CardValueToValue(card.value) - 1]++;
                }
            }
            return vec;
        }

        /**
         * Generates a 22 dimensional array from given melds. Where
         * [0][1] count of 4s splitted in natural and wild cards, [2][3] count of 5s ...
         * @param melds Array with the length 11. Including CardValue FOUR 
         * it holds one List for each ascending CardValue ending with ACE.
         * @return 22 dimensional array containing card amounts
         */
        private static int[] MeldsToVec(List<Card>[] melds)
        {
            int[] vec = new int[22];

            for (int i = 0; i < melds.Length; i++)
            {
                List<Card> meld = melds[i];
                if (meld != null)
                {
                    foreach (Card card in meld)
                    {
                        int idx = i * 2;
                        idx += CardValueToValue(card.value) >= 2 ? 0 : 1;
                        vec[idx]++;
                    }
                }
            }
            return vec;
        }

        private static int CardValueToValue(Card.CardValue cv)
        {
            return cv switch
            {
                Card.CardValue.JOKER => 0,
                Card.CardValue.TWO => 1,
                Card.CardValue.THREE => 2,
                Card.CardValue.FOUR => 3,
                Card.CardValue.FIVE => 4,
                Card.CardValue.SIX => 5,
                Card.CardValue.SEVEN => 6,
                Card.CardValue.EIGHT => 7,
                Card.CardValue.NINE => 8,
                Card.CardValue.TEN => 9,
                Card.CardValue.JACK => 10,
                Card.CardValue.QUEEN => 11,
                Card.CardValue.KING => 12,
                Card.CardValue.ACE => 13,
                _ => -1,
            };
        }

        private static Card.CardValue ValueToCardValue(int value)
        {
            return value switch
            {
                0 => Card.CardValue.JOKER,
                1 => Card.CardValue.TWO,
                2 => Card.CardValue.THREE,
                3 => Card.CardValue.FOUR,
                4 => Card.CardValue.FIVE,
                5 => Card.CardValue.SIX,
                6 => Card.CardValue.SEVEN,
                7 => Card.CardValue.EIGHT,
                8 => Card.CardValue.NINE,
                9 => Card.CardValue.TEN,
                10 => Card.CardValue.JACK,
                11 => Card.CardValue.QUEEN,
                12 => Card.CardValue.KING,
                13 => Card.CardValue.ACE,
                _ => Card.CardValue.JOKER,
            };
        }

        private static int CardToPoints(Card card)
        {
            Card.CardValue cv = card.value;
            Card.CardSuit cs = card.suit;

            if (cv == Card.CardValue.THREE && (cs == Card.CardSuit.DIAMOND || cs == Card.CardSuit.HEART))
            {
                return 100;
            }

            return cv switch
            {
                Card.CardValue.JOKER => 50,
                Card.CardValue.TWO => 50,
                Card.CardValue.THREE => 5,
                Card.CardValue.FOUR => 5,
                Card.CardValue.FIVE => 5,
                Card.CardValue.SIX => 5,
                Card.CardValue.SEVEN => 5,
                Card.CardValue.EIGHT => 10,
                Card.CardValue.NINE => 10,
                Card.CardValue.TEN => 10,
                Card.CardValue.JACK => 10,
                Card.CardValue.QUEEN => 10,
                Card.CardValue.KING => 10,
                Card.CardValue.ACE => 20,
                _ => -1,
            };
        }
    }
}