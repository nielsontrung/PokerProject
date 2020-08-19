package pokerproject.players;

/**
 * Human player class for our poker game. Subclass of Player.
 */
import pokerproject.userinterfaces.UIWrapper;

public class HumanPlayer extends Player {

    ///////****Methods****///////
    /**
     * Method used to get action of the player
     *
     * @param mustAllInFold:boolean whether the human player goes in all-in
     * @return The array of the players actions.
     *
     */
    @Override
    protected int[] getAction(boolean mustAllInFold) {
        int[] actions = userInterface.userAction(mustAllInFold);
        return (actions);
    }

    ///////****Constructors****///////
    /**
     * Constructor that sets up a new human player with the given initial
     * parameters.
     *
     * @param initChipCount The initial amount of chips
     * @param newHandSize Number of the cards in the hand
     * @param initGUIInUse Boolean if the GUI is being used
     * @param initUserInterface User interface wrapper
     * @param playerPosition Position of the player
     */
    public HumanPlayer(int initChipCount, int newHandSize, boolean initGUIInUse, UIWrapper initUserInterface, int playerPosition) {
        super(initChipCount, newHandSize, initGUIInUse, initUserInterface,
                playerPosition);
    }
}
