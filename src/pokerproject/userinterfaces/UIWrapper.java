package pokerproject.userinterfaces;

import pokerproject.Cards.Card;

/**
 * Interface implemented by graphical and text based user interface classes
 *
 */
public interface UIWrapper {

    /**
     * Method to get the initial number of players at the start of a series and
     * the human player position
     *
     * @return numOfPlayers
     */
    public int[] getInitPlayerConditions();

    /**
     * Method used to get users action 1 for bet 2 for call 3 for fold
     *
     * @param mustGoAllInFold
     * @return playerAction Integer[] the players action bet,call,fold, and
     * amount of the bet
     */
    public int[] userAction(boolean mustGoAllInFold);

    /**
     * Method used to display the current game number, starting at 1.
     *
     * @param gameNum
     * @param numPlayers
     * @param num:int The current game number
     */
    public void newGame(int gameNum, int numPlayers);

    /**
     * Method used to display player's hole cards on userInterface.
     *
     * @param holeCards:Card[] The cards the player has in their hand.
     * @param playerPos
     */
    public void displayPlayerCards(Card[] holeCards, int playerPos);

    /**
     * Method used to display table cards on userInterface.
     *
     * @param tableCards:Card[] The cards on the table.
     */
    public void updateTableCards(Card[] tableCards);

    /**
     * Method used to display current pot on the userInterface.
     *
     * @param currentPot:int The current pot.
     */
    public void updatePot(int currentPot);

    /**
     * Method updates the players action label on the userInterface.
     *
     * @param playerPosition:int The position of the player who's chips will be
     * updated.
     * @param playerActions The new action.
     */
    public void updatePlayerAction(int playerPosition, int[] playerActions);

    /**
     * Updates the players chip count at the given position.
     *
     * @param playerPos The players position.
     * @param newChip The new chip count.
     */
    public void updatePlayerChips(int playerPos, int newChip);

    /**
     * Method updates the current bet displayed on the interface
     *
     * @param currentBid:int
     */
    public void updateCurrentBet(int currentBid);

    /**
     * Method updates the game stage displayed on the interface given the
     * parameter
     *
     * @param gameStage
     */
    public void updateGameStage(String gameStage);

    /**
     * Method for displaying the winner or winners on the user interface.
     *
     * @param winnerPos
     * @param winType
     * @param winnings
     * @param finalCards
     */
    public void displayWinner(int[] winnerPos, int winType, int winnings,
            Card[][] finalCards);

    /**
     * Method used for displaying the final winner of the series
     *
     * @param playPos:int, the player position
     * @param chipCount:int, their final ChipCount
     * @param bool:boolean used to determine whether the player wins the series
     */
    public void displayFinalWinner(int playPos, int chipCount, boolean bool);

    /**
     * Sets up the UI for a series with the given number of players and game
     * number.
     *
     * @param gameNum The game number.
     * @param numPlayers The number of players.
     */
    public void setupSeries(int gameNum, int numPlayers);
}
