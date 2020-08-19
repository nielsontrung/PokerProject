package pokerproject.machinery;

import pokerproject.Cards.Card;
import pokerproject.Cards.Deck;

/**
 * Class representing the game table for the poker game. Contains the card deck
 * and tracks the dealt table cards used during the poker game.
 *
 */
public class GameTable {

    ///////**** Variables ****///////
    private Card[] tableCards = new Card[5];
    private Deck deck;
    private int numTableCard;
    private int maxTableCards = 5;
    private int maxHandSize = 2;
    private int[] numPlayerCards;
    boolean GUIInUse;

    ///////**** Constructors ****///////
    /**
     * Creates new GameTable object with pot = 0, new Deck object, numTableCard
     * = 0, and numPlayerCards int[numPlayers]
     *
     * @param numPlayers int
     * @param nGUIInUse
     */
    public GameTable(int numPlayers, boolean nGUIInUse) {
        GUIInUse = nGUIInUse;
        deck = new Deck(GUIInUse);
        numTableCard = 0;
        numPlayerCards = new int[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            numPlayerCards[i] = 0;
        }
        for (int j = 0; j < maxTableCards; j++) {
            tableCards[j] = new Card(GUIInUse);
        }
    }

    ///////**** Methods ****///////
    ////** Getters & Setters **////
    /**
     * Method returns a specific card from the tableCards Card[]
     *
     * @param index Index of card to get.
     * @return Card object from tableCards at index
     */
    public Card getTableCard(int index) {
        Card tableCard = new Card(tableCards[index]);
        return tableCard;
    }

    /**
     * Gets the tableCards instance variable
     *
     * @return tableCards returns the instance variable tableCards a card array
     */
    public Card[] getTableCards() {
        Card[] copyTableCards = new Card[5];
        for (int i = 0; i < 5; i++) {
            copyTableCards[i] = new Card(tableCards[i]);
        }
        return copyTableCards;
    }

    ////** Getters & Setters **////
    /**
     * Sets all the GameTable instance variables to where they should be for
     * beginning of game. Used to reset for each new hand.
     *
     * @param newNumPlayers Number of players in the new game.
     */
    public void newGame(int newNumPlayers) {
        deck.shuffle();
        numTableCard = 0;
        numPlayerCards = new int[newNumPlayers];
        for (int i = 0; i < newNumPlayers; i++) {
            numPlayerCards[i] = 0;
        }
        for (int j = 0; j < 5; j++) {
            tableCards[j] = new Card(GUIInUse);
        }
    }

    /**
     * Method for setting a table card's value and suit
     *
     * @param newCard Cards to set the table cards to.
     */
    public void setTableCard(int[] newCard) {
        if ((newCard[0] >= 2 && newCard[0] <= 14) && (newCard[1] >= 1 && newCard[1] <= 4)) {
            tableCards[numTableCard].setValue(newCard[0]);
            tableCards[numTableCard].setSuit(newCard[1]);
        } else {
            tableCards[numTableCard].setValue(0);
            tableCards[numTableCard].setValue(0);
            System.out.println("Table card " + numTableCard + " values out of range.");
        }
    }

    /**
     * Shuffles the GameTable's Deck object
     */
    public void shuffleDeck() {
        deck.shuffle();
    }

    /**
     * Deal cards2Deal number of cards to tableCards array. Checks to ensure
     * number of cards dealt does not exceed max number of cards allowed on
     * table. Passes values, not references to Card objects.
     *
     * @param cards2Deal:int the desired number of cards to be dealt
     */
    public void dealTableCards(int cards2Deal) {
        int numCardsToDeal = Math.min(cards2Deal, maxTableCards - numTableCard);
        for (int i = 0; i < numCardsToDeal; i++) {
            setTableCard(deck.dealTopCard());
            numTableCard++;
        }
    }

    /**
     * Method returns a nested integer array of size numCards. Used for dealing
     * cards to players.
     *
     * @param numCards Number of cards to deal out.
     * @param playerPos The position of the player to deal to.
     * @return Nested integer array representing the players cards as integer
     * values the first index is the rank the 2nd index is the suit of the card
     */
    public int[][] dealCardsOut(int numCards, int playerPos) {
        int numCardsToDeal = Math.min(numCards, maxHandSize - numPlayerCards[playerPos]);
        int[][] deal = new int[numCardsToDeal][2];
        for (int i = 0; (i < numCardsToDeal); i++) {
            deal[i] = deck.dealTopCard();
            numPlayerCards[playerPos]++;
        }
        return (deal);
    }

    /**
     * Method for obtaining the value and suit of the cards dealt to the table
     * without referencing the memory location of the actual Card objects.
     *
     * @return tableCardsVal type int[numTableCard][2]
     */
    public int[][] getTableCardsVal() {
        int[][] tableCardsVal = new int[numTableCard][2];
        for (int i = 0; i < numTableCard; i++) {
            tableCardsVal[i][0] = tableCards[i].getValue();
            tableCardsVal[i][1] = tableCards[i].getSuit();
        }
        return tableCardsVal;
    }

    /**
     * Method for printing the values of the table cards
     */
    public void printTableCards() {
        for (int i = 0; i < maxTableCards; i++) {
            System.out.println(tableCards[i].getValue() + " " + tableCards[i].getSuit());
        }
    }

    /**
     * Method returns the tableCards as an nested integer array used for
     * evaluating hand strengths in Engine and MathStats class
     *
     * @return A nested integer array where the elements in the array is an
     * integer array, where the first index in the array is the rank of the card
     * and the second index is the suit of the card
     */
    public int[][] returnTableCardsInt() {
        int[][] handVal = new int[numTableCard][2];
        for (int i = 0; i < numTableCard; i++) {
            handVal[i][0] = tableCards[i].getValue();
            handVal[i][1] = tableCards[i].getSuit();
        }
        return (handVal);
    }
}
