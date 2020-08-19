package pokerproject.Cards;

import javafx.scene.image.Image;

/**
 * Class representing standard playing cards. Used for building deck object and
 * playing card games.
 */
public final class Card {

    ///////**** Variables ****///////
    private int suit;
    private int value;
    private Image cardImage;
    private String fileName;
    boolean GUIInUse;

    ///////**** Constructors ****///////
    /**
     * Default constructor for class Will create Card object with instance
     * variables both set to 0
     *
     * @param nGUIInUse
     */
    public Card(boolean nGUIInUse) {
        GUIInUse = nGUIInUse;
        suit = 0;
        value = 0;
        if (GUIInUse);
        setCardString();
    }

    /**
     * Constructor for Card class will create an Card object with instance
     * variables corresponding to the given parameters
     *
     * @param aValue:int the rank of the Card
     * @param aSuit:int the suit of the Card
     * @param nGUIInUse:boolean used to reference a path to an image
     * representing the card if the is in use
     */
    public Card(int aValue, int aSuit, boolean nGUIInUse) {
        GUIInUse = nGUIInUse;
        suit = aSuit;
        value = aValue;
        if (GUIInUse) {
            setCardString();
            setCardImage();
            cardImage = new Image(fileName);
        }
    }

    /**
     * Copy constructor.
     *
     * @param copyCard Card to copy.
     */
    public Card(Card copyCard) {
        GUIInUse = copyCard.getGUIInUse();
        suit = copyCard.getSuit();
        value = copyCard.getValue();
        if (GUIInUse) {
            setCardString();
            setCardImage();
            cardImage = new Image(fileName);
        }
    }

    ///////**** Methods ****///////
    /**
     * Methods returns the suit of the Card
     *
     * @return An integer representing the suit of the Card 1 for Diamonds, 2
     * for Clubs, 3 for Hearts, and 4 for Spades
     */
    public int getSuit() {
        return suit;
    }

    /**
     * Get method for card's numeric value
     *
     * @return An integer representing the rank of the Card from 2 to 14, where
     * 2 represents the lowest value Card two and 14 representing the highest
     * ranking Card Ace
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the cards value and suit as an integer array of form [value,suit]
     *
     * @return Integer array representing the card.
     */
    public int[] getCardInt() {
        int[] card = new int[2];
        card[0] = getValue();
        card[1] = getSuit();
        return (card);
    }

    /**
     * Returns the value of GUIInUSe.
     *
     * @return
     */
    public boolean getGUIInUse() {
        return (GUIInUse);
    }

    /**
     * Set method for Card's suit instance variable only integers of 1,2,3, and
     * 4 are valid else set the Card suit to 0
     *
     * @param newSuit The suit of the Card
     *
     */
    public void setSuit(int newSuit) {
        switch (newSuit) {
            case 1:
                suit = 1;
                break;
            case 2:
                suit = 2;
                break;
            case 3:
                suit = 3;
                break;
            case 4:
                suit = 4;
                break;
            default:
                suit = 0;
                break;
        }
    }

    /**
     * Set method for card's numeric value (e.g. 3,4,ace,queen,etc.)
     *
     * @param newValue The new value of the card.
     */
    public void setValue(int newValue) {
        value = newValue;
    }

    /**
     * Method returns Card's cardImage instance variable
     *
     * @return cardImage Image of the card
     */
    public Image getCardImage() {
        return cardImage;
    }

    /**
     * Method constructs a path according to the Cards rank and suit path
     * corresponding to the Card's image
     */
    public void setCardString() {
        String suitString = "";
        switch (suit) {
            case 1:
                suitString = "D";
                break;
            case 2:
                suitString = "C";
                break;
            case 3:
                suitString = "H";
                break;
            case 4:
                suitString = "S";
                break;
            case 0:
                suitString = "Blank";
                break;
        }
        fileName = "file:CardImages/" + value + suitString + ".PNG";
    }

    /**
     * Method sets the Card's cardImage instance variable according to the path
     * constructed by the setCardString method
     */
    public void setCardImage() {
        cardImage = new Image(fileName);
    }

}
