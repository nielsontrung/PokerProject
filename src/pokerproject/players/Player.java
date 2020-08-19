package pokerproject.players;

import pokerproject.Cards.Card;
import pokerproject.userinterfaces.UIWrapper;

/**
 * Player class for our poker game project. Superclass for CPUPlayer and
 * HumanPlayer. Instance variables: chipCount (int) handSize (int) hand (Card[])
 * hasFolded (boolean) Methods: getters and setters printHand() returnHand
 */
public abstract class Player {

    ///////**** Variables ****///////
    protected int chipCount = 0;
    protected int handSize = 2;
    protected Card[] hand;
    private int playerPos;
    protected UIWrapper userInterface;

    private boolean hasFolded = false;
    private boolean broke = false;
    private boolean GUIInUse;
    private boolean allIn = false;

    protected int currentBid;
    protected int[][] tableCards;
    private int lastBet;

    ///////**** Methods ****///////
    ////**Abstract**////
    /**
     * Gets the action of the player, is overriden by each player type so that
     * they get their actions in an appropriate way.
     *
     * @param mustAllInFold Whether or not the player has enough chips to meet
     * the call.
     * @return An array of integers that represents the players actions.
     */
    protected abstract int[] getAction(boolean mustAllInFold);

    ////**Protected**////
    /**
     * Gets the current bid.
     *
     * @return The current bid.
     */
    protected int getCurrentBid() {
        return (currentBid);
    }

    /**
     * Allows the derived classes to see what the table cards are.
     *
     * @return
     */
    protected int[][] getTableCards() {
        return (tableCards.clone());
    }

    ////Private**////
    /**
     * Checks to see if the proposed bet is valid or not.
     *
     * @param proposedBid THe proposed bid.
     * @return
     */
    private boolean checkBet(int proposedBid) {
        boolean goodCall;
        goodCall = (proposedBid <= chipCount) && (proposedBid + lastBet >= currentBid);
        return (goodCall);
    }

    /**
     * Updates the player and their image according to the given actions.
     *
     * @param action The actions the player has/wants to carry out.
     */
    private void updatePlayerAction(int[] action) {
        userInterface.updatePlayerAction(playerPos, action);
    }

    ////**Public**////
    /**
     * Gets and handles the players action, making sure they are valid before
     * acting.
     *
     * @param nCurrentBid The current bid in the game.
     * @param newTableCards The current table cards in the game.
     * @return
     */
    public int[] playerAction(int nCurrentBid, int[][] newTableCards) {
        currentBid = nCurrentBid;
        tableCards = newTableCards;
        boolean gettingInput = true;
        int[] playerAction = new int[3];
        int[] newAction;
        boolean mustAllInFold = (currentBid >= chipCount);
        while (gettingInput) {
            newAction = getAction(mustAllInFold);
            switch (newAction[0]) {
                case 1: { // Call
                    int changeInChips = currentBid - lastBet;
                    /* Check to make sure the player has enough chips left to 
                       make their change in chips, and that the total bet is 
                       at least as big as the current bet*/
                    boolean goodAction = checkBet(changeInChips);
                    if (goodAction) {
                        addChipCount(-changeInChips);
                        playerAction[0] = 1;
                        playerAction[1] = changeInChips;
                        lastBet = currentBid;
                        gettingInput = false; //Breaks the loop of getting input
                    } else {
                        //UI Method to print
                        System.out.println("You cannot meet the call");
                    }
                    break;
                }
                case 4: { //All in
                    /* Set the raise to the total chipcount of the player so
                       that the raise switch will handle the all in action as 
                       a normal raise with all the player has*/
                    newAction[1] = chipCount - currentBid;
                }
                case 2: { // Raise
                    int changeInChips = newAction[1] - lastBet;
                    /* Check to make sure the player has enough chips left to 
                       make their change in chips, and that the total bet is 
                       bigger than the current bet*/
                    boolean goodAction = checkBet(changeInChips - 10);
                    if (goodAction) {
                        addChipCount(-changeInChips);
                        playerAction[0] = 2;
                        playerAction[1] = changeInChips;
                        lastBet = changeInChips + lastBet;
                        gettingInput = false; //Breaks the loop of getting input
                    } else {
                        //UI method to print
                        System.out.println("You cannot raise that much");
                    }
                    break;
                }
                case 3: { // Fold
                    setHasFolded(true);
                    playerAction[0] = 3;
                    playerAction[1] = 0;
                    lastBet = 0;
                    gettingInput = false;
                    break;
                }
                default: {
                    // If input is bad retry
                    newAction = getAction(mustAllInFold);
                    System.out.println("Not a valid action.");
                    break;
                }
            }
        }
        //Check to see if the player has gone all in
        if (chipCount == 0) {
            allIn = true;
            playerAction[0] = 4;
        }
        playerAction[2] = lastBet; //Total in
        int[] holder = new int[2];
        holder[0] = playerAction[0];
        holder[1] = lastBet;
        updatePlayerAction(holder);
        return playerAction;
    }

    /**
     * Returns the chip count of the player.
     *
     * @return chipCount The chips the player has.
     */
    public int getChipCount() {
        return chipCount;
    }

    /**
     * Adds the given amount of chips to the players chip count.
     *
     * @param amount The amount to add.
     */
    public void addChipCount(int amount) {
        chipCount += amount;
        userInterface.updatePlayerChips(playerPos, chipCount);
    }

    /**
     * Returns true of false whether or not the player has folded.
     *
     * @return hasFolded True or false if the player has folded or not.
     */
    public boolean getHasFolded() {
        return (hasFolded);
    }

    /**
     * Sets the players folded boolean to the new given value.
     *
     * @param newHasFolded New value of has folded.
     */
    public void setHasFolded(boolean newHasFolded) {
        hasFolded = newHasFolded;
    }

    /**
     * Returns true or false whether or not the player is broke or not.
     *
     * @return isBroke True or false dependant on if the player is broke.
     */
    public boolean getBroke() {
        return broke;
    }

    /**
     * Sets the value of broke to the given value.
     *
     * @param newBroke New value of broke.
     */
    public void setBroke(boolean newBroke) {
        broke = newBroke;
    }

    /**
     * Gets whether or not the player has gone all in.
     *
     * @return True or false dependant on the value of all in.
     */
    public boolean getAllIn() {
        return (allIn);
    }

    /**
     * Sets the value of all in.
     *
     * @param newAllIn New all in value.
     */
    public void setAllIn(boolean newAllIn) {
        allIn = newAllIn;
    }

    /**
     * Method for setting value and suit of Cards in player's hand
     *
     * @param deal
     */
    public void setHand(int[][] deal) {
        for (int i = 0; i < deal.length; i++) {
            hand[i].setValue(deal[i][0]);
            hand[i].setSuit(deal[i][1]);
            if (GUIInUse) {
                hand[i].setCardString();
                hand[i].setCardImage();
            }
        }
    }

    /**
     * Prints out values of the player's hand as value then suit
     */
    public void printHand() {
        for (int i = 0; i < 2; i++) {
            System.out.println(hand[i].getValue() + " " + hand[i].getSuit());
        }
    }

    /**
     * Returns the players hand as an int array of values and suits rather than
     * references to card objects.
     *
     * @return int[][] handVal
     */
    public int[][] returnHandInt() {
        int[][] handVal = new int[2][2];
        for (int i = 0; i < 2; i++) {
            handVal[i][1] = hand[i].getSuit();
            handVal[i][0] = hand[i].getValue();
        }
        return (handVal);
    }

    /**
     * Returns the players hand as a copy.
     *
     * @return The copy of the players hand.
     */
    public Card[] returnHand() {
        Card[] copyHand = new Card[handSize];
        for (int i = 0; i < handSize; i++) {
            copyHand[i] = new Card(hand[i]);
        }
        return (copyHand);
    }

    /**
     * The hand size of the player.
     *
     * @return The hand size of the player.
     */
    public int getHandSize() {
        return handSize;
    }

    /**
     * Tells the player a new round of betting has started.
     *
     * @param restartRound
     */
    public void setNewRound() {
        lastBet = 0;
        currentBid = 0;
    }

    /**
     * Returns the user interface object currently in use(not a copy and should
     * not be as all players need to use the same interface object).
     *
     * @return The userInterface.
     */
    public UIWrapper getUserInterface() {
        return (userInterface);
    }

    /**
     * Returns the position of the player.
     *
     * @return The players position.
     */
    public int getPosition() {
        return (playerPos);
    }

    /**
     * Sets the last bet of the player.
     *
     * @param newLastBet The new last bet of the player.
     */
    public void setLastBet(int newLastBet) {
        lastBet = newLastBet;
    }

    /**
     * Gets the last bet of the player.
     *
     * @return The players last bet
     */
    public int getLastBet() {
        return (lastBet);
    }

    ///////****Constructors****///////
    /**
     * Construcor which takes initial chip count , hand size and if GUI in use
     * boolean
     *
     * @param initChipCount Integer the players chip count
     * @param newHandSize Integer the players hand size
     * @param nGUIInUse boolean if the graphical user interface is being used
     * @param initUserInterface The user interface to be used.
     * @param initPosition The position of the player.
     */
    public Player(int initChipCount, int newHandSize, boolean nGUIInUse, UIWrapper initUserInterface, int initPosition) {
        GUIInUse = nGUIInUse;
        if (newHandSize > 0) {
            handSize = newHandSize;
        }
        hand = new Card[handSize];
        userInterface = initUserInterface;
        playerPos = initPosition;
        for (int i = 0; i < handSize; i++) {
            hand[i] = new Card(GUIInUse);
        }
        if (initChipCount > 0) {
            addChipCount(initChipCount);
        } else {
            addChipCount(1000);
        }
    }
}
