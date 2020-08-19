package pokerproject.Cards;

/**
 * Deck class for poker project. Array of Card objects. Instance variables:
 * cards (Card[], array of 52 card objects) nextCard (int, counter variable used
 * to track position of next card to deal) Methods: getCard(int index) shuffle()
 * dealTopCard()
 */
import java.util.Collections;
import java.util.Arrays;

public class Deck {

    ///////**** Variables ****///////
    private Card[] cards = new Card[52];
    private int nextCard;
    boolean GUIInUse;

    ///////**** Constructor ****///////
    /**
     * Default constructor Will create a deck object with 52 cards consisting of
     * the cards 1-14 for suits 1-4
     *
     * @param nGUIInUse True or false whether or not the GUI is in use.
     * @Param nGUIInUse: boolean used to check if the GUI is being used
     */
    public Deck(boolean nGUIInUse) {
        GUIInUse = nGUIInUse;
        int n = 0;
        for (int i = 2; i <= 14; i++) {
            for (int j = 1; j <= 4; j++) {
                cards[n] = new Card(i, j, GUIInUse);
                n++;
            }
        }
        nextCard = 0;
    }

    ///////**** Methods ****///////
    /**
     * Gets the card at the given index in the deck.
     *
     * @param index The index of the deck
     * @return Card object from cards array at index
     */
    public Card getCard(int index) {
        Card returnCard = new Card(cards[index]);
        return (returnCard);
    }

    /**
     * Randomly shuffles the card objects in the cards instance variable
     */
    public void shuffle() {
        nextCard = 0;
        Collections.shuffle(Arrays.asList(this.cards));
    }

    /**
     * Deals top card by returning card at array index corresponding to instance
     * variable nextCard
     *
     * @return dealtCard Card object at index nextCard
     */
    public int[] dealTopCard() {
        int[] dealtCard = new int[2];
        dealtCard[0] = cards[nextCard].getValue();
        dealtCard[1] = cards[nextCard].getSuit();
        nextCard++;
        return dealtCard;
    }

}
