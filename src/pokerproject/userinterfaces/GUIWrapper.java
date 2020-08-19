package pokerproject.userinterfaces;

import javafx.application.Application;
import pokerproject.Cards.Card;

/**
 * Class that acts as a wrapper or communication hub for the GUI and the engine.
 *
 */
public class GUIWrapper implements UIWrapper {

    ///////**** Variables s****///////
    private int numPlayers;
    private boolean gotInitPlayerConditions;
    private int[] userActions;
    private boolean gotUserActions;
    private int playerPos;
    static private int currentBet = 0;
    private int playerChipCount = 100;
    private int gameStage = 0;
    private boolean winnerViewed = false;
    int loadSaveGame = 0;

    ///////**** Constructors s****///////
    /**
     * Creates an instance of the GUIWrapper and starts the GUI running.
     */
    public GUIWrapper() {
        System.out.println("Launching the GUI");
        GUIWrapper wrapperObj = this;
        GUI.wrapper = wrapperObj;
        Thread GUIThread = new Thread() {
            @Override
            public void run() {
                Application.launch(GUI.class);
            }
        };
        GUIThread.start();
    }

    ///////**** Method s****///////
    @Override
    public int[] getInitPlayerConditions() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            };
            if (gotInitPlayerConditions) {
                int[] holder = new int[3];
                holder[0] = loadSaveGame;
                holder[1] = numPlayers;
                holder[2] = playerPos;
                numPlayers = 0;
                playerPos = 0;
                gotInitPlayerConditions = false;
                return (holder);
            }
        }
    }

    @Override
    public int[] userAction(boolean mustGoAllInFold) {
        GUI.setGetUserAction(true);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            };
            if (gotUserActions) {
                int[] holder = userActions.clone();
                userActions[0] = 0;
                userActions[1] = 0;
                gotUserActions = false;
                return (holder);
            }
        }
    }

    @Override
    public void newGame(int gameNum, int numPlayers) {
        GUI.newGame(gameNum, numPlayers);
    }

    @Override
    public void displayPlayerCards(Card[] holeCards, int playerPos) {
        GUI.setNewPlayerCards(holeCards, playerPos);
    }

    @Override
    public void updateTableCards(Card[] tableCards) {
        GUI.setNewTableCards(tableCards);
    }

    @Override
    public void updatePot(int newPot) {
        GUI.setPot(newPot);
    }

    @Override
    public void updateGameStage(String nGameStage) {
        GUI.setNewGameStage(nGameStage); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePlayerAction(int playerPosition, int[] playerActions) {
        GUI.updatePlayerAction(playerPosition, playerActions);
    }

    @Override
    public void updatePlayerChips(int playerPos, int newChip) {
        GUI.updatePlayerChips(playerPos, newChip);
    }

    @Override
    public void updateCurrentBet(int currentBid) {
        currentBet = currentBid;
        GUI.newCurrentBet(currentBet);
    }

    @Override
    public void displayWinner(int[] winnerPos, int winType, int winnings,
            Card[][] finalCards) {
        setWinnerViewed(false);
        for (int i = 1; i < finalCards.length; i++) {
            GUI.setNewPlayerCards(finalCards[i], i);
        }
        for (int i = 0; i < winnerPos.length; i++) {
            GUI.updatePlayerLabels(playerPos, playerChipCount, "");
        }
        GUI.displayWinner(winnerPos, winType, winnings, finalCards);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            };
            if (winnerViewed) {
                winnerViewed = false;
                break;
            }
        }
    }

    @Override
    public void displayFinalWinner(int playPos, int chipCount, boolean bool) {
        // GUI.displayFinalWinner(playerPos, chipCount, bool);
    }

    /**
     * Sets up a the GUI for a new series with the given number of players and
     * game number.
     *
     * @param gameNum The game number.
     * @param numPlayers The number of players.
     */
    @Override
    public void setupSeries(int gameNum, int numPlayers) {
        GUI.setupSeries(gameNum, numPlayers);
    }

    ///////**** GUI Communication s****///////
    /**
     * Returns the current bet stored within the GUIWrapper.
     *
     * @return
     */
    public int getCurrentBet() {
        return (currentBet);
    }

    /**
     * Allows the GUI to give the GUIWrapper the needed initial conditions for
     * the engine.
     *
     * @param numPlay
     * @param playPos
     */
    public void setInitPlayers(int numPlay, int playPos) {
        if (numPlay == 0) {
            loadSaveGame = 1;
        }
        playerPos = playPos;
        numPlayers = numPlay;
        gotInitPlayerConditions = true;
    }

    /**
     * Allows the GUI to give the GUIWrapper the needed player actions for the
     * engine.
     *
     * @param newPlayerAction
     */
    public void setPlayerAction(int[] newPlayerAction) {
        userActions = newPlayerAction;
        gotUserActions = true;
    }

    /**
     * Used by the GUI to change to tell the Wrapper and consequentially the
     * engine that the continue button has been clicked after displaying the
     * winner of the game.
     *
     * @param newWinnerViewed True or false dependant on if the button has been
     * clicked.
     */
    public void setWinnerViewed(boolean newWinnerViewed) {
        winnerViewed = newWinnerViewed;
        System.out.println(newWinnerViewed);
    }

}
