using System.Collections;
using System.Collections.Generic;
using System;

namespace SmartyCanasta.Logic{

	[System.Serializable]
	public class Card: IComparable  {

		public enum CardSuit {SPADE, DIAMOND, CLUB, HEART};
		public enum CardValue {JOKER, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE};

		public CardSuit suit;
		public CardValue value;

		public int cardID;
		public int currentDeckID;

//		public static int CardIDGenerator = 0;

		public Card(CardSuit suit, CardValue value){
			this.suit = suit;
			this.value = value;

			this.InitIds ();
		}

		public Card(Card baseCard){
			this.suit = baseCard.suit;
			this.value = baseCard.value;
			this.cardID = baseCard.cardID;
			this.currentDeckID = baseCard.currentDeckID;
		}

		public Card(){

		}

        public override string ToString() {
            System.Text.StringBuilder sb = new System.Text.StringBuilder();

            sb.AppendFormat("Card:[{0}][{1}], ", this.value, this.suit);
            sb.AppendFormat("cardID:{0}, ", this.cardID);
            sb.AppendFormat("currentDeckID:{0} ", this.currentDeckID);

            return sb.ToString();
        }

        public virtual string ToEasilyReadableJSONString() {
            System.Text.StringBuilder sb = new System.Text.StringBuilder();

            sb.Append("{");
            sb.AppendFormat("\"Card\":\"[{0}][{1}]\", ", this.value, this.suit);
            sb.AppendFormat("\"cardID\":\"{0}\", ", this.cardID);
            sb.AppendFormat("\"currentDeckID\":\"{0}\" ", this.currentDeckID);
            sb.Append("}");

            return sb.ToString();
        }

        protected void InitIds(){		
			//this.cardID = IDGenerator.CardIDGenerator;	
			this.currentDeckID = -1;

			//IDGenerator.CardIDGenerator++;
		}

		#region IComparable implementation

		public int CompareTo (object obj)
		{
			if (obj == null) return 1;

			Card otherCard = obj as Card;
			if (otherCard != null) 
				return this.value.CompareTo(otherCard.value);
			else
				throw new ArgumentException("Object is not a Card");
		}

		#endregion

		/// <summary>
		/// Check if cards have same card value
		/// </summary>
		public bool EqualValue(Card card){
			return this.value == card.value;
		}
		/// <summary>
		/// Check if cards have same card value
		/// </summary>
		public bool EqualValue(Card.CardValue cardValue){
			return this.value == cardValue;
		}
		/// <summary>
		/// Check if cards have same card value and card suit
		/// </summary>
		public bool EqualCard(Card card){
			return this.value == card.value && this.suit == card.suit;
		}

		public string GetCardFullName(){
			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			sb.AppendFormat ("{0}_{1}", this.value, this.suit);
			return sb.ToString ();
		}

		public bool IsRedSuit(){
			if(this.suit == CardSuit.HEART || this.suit == CardSuit.DIAMOND){
				return true;
			}
			return false;
		}
       
    }
}
