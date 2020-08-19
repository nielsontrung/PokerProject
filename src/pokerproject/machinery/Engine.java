package pokerproject.machinery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pokerproject.Cards.Card;

import pokerproject.userinterfaces.UIWrapper;
import pokerproject.players.HumanPlayer;
import pokerproject.players.Player;
import pokerproject.players.CpuPlayer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Game engine for a poker game with the ability to be adapted to different
 * versions of poker or even similar card based games.
 *
 * @author thomasnewton
 */
public class Engine {

    ///////**** Variables ****///////
    // Series States
    private GameTable gameTable; // Remeber to change numPlay
    private boolean GUIInUse;
    private UIWrapper userInterface;
    private int gameNum;
    private Player[] playerList;
    private int handSize = 2; // Make changable
    private int startChips = 1000; // Make changable

    // Game States
    private int numPlayers;
    private int humPos;
    private int[] playerPositions;
    private int blind = 10; // Can make blind changable later
    private int[] splitPots;

    // Betting Loop States
    private int pot;
    private int currentBid;
    private boolean firstBid;
    private int lastRaisePos;
    private int curNumPlayers;

    ///////**** Constructors ****///////
    /**
     * Takes the reference to the userInterface that will be used for this game.
     * At the moment the thread that handles the operations of the engine is
     * create within the constructor, but it could be better to do this when we
     * start up a series of a certain type.
     *
     * @param newUserInterface
     * @param nGUIInUse
     */
    public Engine(UIWrapper newUserInterface, boolean nGUIInUse) {
        GUIInUse = nGUIInUse;
        userInterface = newUserInterface;
        System.out.println("Launching the engine");
        Thread EngineThread = new Thread() {
            @Override
            public void run() {
                series();
            }
        };
        EngineThread.start();
    }

    ///////**** Methods ****///////
    /**
     * Starts a series of poker games.
     */
    public void series() {
        int[] initPlayConditions = userInterface.getInitPlayerConditions();
        int initNumPlayers;
        int initHumPos;
        if (initPlayConditions[0] == 1) {
            List<Integer> saveData = loadGame();
            gameNum = saveData.get(0);
            List<Integer> savePlayerData = saveData.subList(2, saveData.size());
            pot = 0;

            setupSeriesLoadGame(saveData.get(1), savePlayerData);
        } else {
            initNumPlayers = initPlayConditions[1];
            initHumPos = initPlayConditions[2] - 1;
            gameNum = 0;
            pot = 0;

            setupSeries(initNumPlayers, initHumPos);
        }

        while (true) {
            setupGame();
            game();
            updatePlayersSeries();
            if (numPlayers == 1) { // Set maxNumGames? change to numPlayers
                break;
            }
            rotatePlayerPositions();
        }
    }

    /**
     * Method that saves the current game states to a file.
     */
    private void saveGame() {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("autosave.txt")));
            writer.print(gameNum);
            writer.println();
            writer.print(numPlayers);
            writer.println();
            for (int i = 0; i < numPlayers; i++) {
                writer.print(playerList[i].getPosition());
                writer.println();
                writer.print(playerPositions[i]);
                writer.println();
                writer.print(playerList[i].getChipCount());
                writer.println();
            }
            writer.flush();
            writer.close();
            System.out.println("Game saved");
        } catch (IOException e) {
            System.out.println("Error saving file");
        }
    }

    /**
     * Method that reads the load game states from a file.
     *
     * @return
     */
    private List<Integer> loadGame() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("autosave.txt"));
            List<Integer> saveData = new ArrayList<>();
            saveData.add(Character.getNumericValue(reader.readLine().charAt(0)));
            saveData.add(Character.getNumericValue(reader.readLine().charAt(0)));
            for (int i = 0; i < saveData.get(1); i++) {
                saveData.add(Character.getNumericValue(reader.readLine().charAt(0)));
                saveData.add(Character.getNumericValue(reader.readLine().charAt(0)));
                saveData.add(parseInt(reader.readLine()));
            }
            reader.close();
            System.out.println("Save data read from file");
            return saveData;
        } catch (IOException e) {
            System.out.println("Error reading data from save file");
            return null;
        }
    }

    ///////**** Game Methods ****///////
    /**
     * Method checks the number of players in a given game if the current number
     * of players does not equal one execute other methods within the class
     */
    private void game() {
        saveGame();

        gameNum++;
        userInterface.newGame(gameNum, numPlayers);
        if (!(curNumPlayers == 1)) { //here
            updatePlayerGame();
            draw();
            sleep(1000);
        }
        if (!(curNumPlayers == 1)) {
            updatePlayerGame();
            flop();
            sleep(1000);
        }
        if (!(curNumPlayers == 1)) {
            updatePlayerGame();
            turn();
            sleep(1000);
        }
        if (!(curNumPlayers == 1)) {
            updatePlayerGame();
            river();
            sleep(1000);
        }
        showdown();
        sleep(1000);
    }

    /**
     * Method deals Cards to the players in playerList updates the userInterface
     * to display the players hole cards sets the currentBid to the blind and
     * calls the bettingLoop method
     */
    private void draw() {
        setCurrentBid(blind);
        Card[] playerHand = playerList[playerPositions[humPos]].returnHand();

        userInterface.displayPlayerCards(playerHand, 0);
        userInterface.updateGameStage("Draw");

        bettingLoop();

    }

    /**
     * Method deals first three table cards from the game tables deck updates
     * the userInterface to display the players hole cards sets the currentBid
     * to the blind and calls the bettingLoop method
     */
    private void flop() {
        setCurrentBid(0);
        dealTableCards(3);
        int[][] tableCardsInt = gameTable.returnTableCardsInt();
        Card[] tableCards = new Card[3];
        for (int i = 0; i < tableCardsInt.length; i++) {
            tableCards[i] = new Card(tableCardsInt[i][0], tableCardsInt[i][1], GUIInUse);
        }
        userInterface.updateTableCards(tableCards);
        userInterface.updateGameStage("Flop");

        bettingLoop();

    }

    /**
     * Method deals the fourth table card "The Turn" from the game tables deck
     * updates the userInterface to display the players hole cards sets the
     * currentBid to the blind and calls the bettingLoop method
     */
    private void turn() {
        setCurrentBid(0);
        dealTableCards(1);
        int[][] tableCardsInt = gameTable.returnTableCardsInt();
        Card[] tableCards = new Card[1];
        tableCards[0] = new Card(tableCardsInt[3][0], tableCardsInt[3][1], GUIInUse);

        userInterface.updateTableCards(tableCards);
        userInterface.updateGameStage("Turn");

        bettingLoop();
    }

    /**
     * Method deals the fifth and last table card "The River" from the game
     * tables deck updates the userInterface to display the players hole cards
     * sets the currentBid to the blind and calls the bettingLoop method
     */
    private void river() {
        setCurrentBid(0);
        dealTableCards(1);
        int[][] tableCardsInt = gameTable.returnTableCardsInt();
        Card[] tableCards = new Card[1];
        tableCards[0] = new Card(tableCardsInt[4][0], tableCardsInt[4][1], GUIInUse);

        userInterface.updateTableCards(tableCards);
        userInterface.updateGameStage("River");

        bettingLoop();
    }

    /**
     * Updates anything related to the players that needs updating before a new
     * stage begins.
     */
    private void updatePlayerGame() {
        for (int i = 0; i < numPlayers; i++) {
            setLastBet(i, 0);
        }
    }

    /**
     * Method determines the winning hand iterates through all the players in
     * playerList and compares the strength of each players hand with the table
     * cards
     *
     */
    private void showdown() {
        // Sets any of the players who havent gone all in to the current pot
        // Change so the split pot is handled in the actions and betting more efficently 
        for (int playerPos : playerPositions) {
            if (!playerList[playerPos].getAllIn()) {
                splitPots[playerPos] = pot;
            }
        }

        int winningHandType; //How the player won (eg. flush, pair, by default)
        int winnings; //How much the winner(s) win
        boolean winnerAllIn = false; //True if one of the winners has gone allIn
        int allInPos = -1; //Pos of the winner who went allIn
        Card[][] finalCards = new Card[numPlayers][2]; //Final cards for UI to display
        List<Integer> winnerPosList = new ArrayList<>();

        int[] finPlayerPoses = new int[curNumPlayers]; //Game positions of players
        int[][] handStrengths = new int[curNumPlayers][3];
        int[] winnerPosArray;

        /*Get the strength of the players hands who havent folded and put into
        handStrengths array*/
        int count = 0;
        for (int playerPos : playerPositions) {
            if (!playerList[playerPos].getHasFolded()) {
                int[][] hand1 = playerList[playerPos].returnHandInt();
                int[][] hand2 = gameTable.returnTableCardsInt();
                int[][] hand = Calculator.combineHands(hand1, hand2);

                handStrengths[count] = Calculator.detHandStrength(hand);
                finPlayerPoses[count] = playerPos;
                count++;
            }
        }

        if (curNumPlayers == 1) {
            winningHandType = 0;
            winnerPosArray = new int[1];
            winnerPosArray[0] = finPlayerPoses[0];
            winnings = pot;
        } else {
            winnerPosList = determineWinners(handStrengths, finPlayerPoses);
            winnerPosArray = new int[winnerPosList.size() - 1];
            winningHandType = winnerPosList.remove(0);
            int i = 0;
            for (int num : winnerPosList) {
                winnerPosArray[i] = num;
                i++;
            }
            finalCards = getFinalCards();

            /*Check to see if multiple winners need to be handled or not and execute*/
            if (winnerPosList.size() == 1) { //One winner
                if (!playerList[winnerPosList.get(0)].getAllIn()) {
                    winnings = pot;
                } else { //Has the winner gone all in
                    winnings = splitPots[winnerPosList.get(0)];
                    winnerAllIn = true;
                    allInPos = winnerPosList.get(0);
                }
            } else { //Multiple winners
                int splitWay = winnerPosList.size();
                List<Integer> pots = new ArrayList<>();

                //Has a winner gone allIn
                for (int position : winnerPosList) {
                    int maxWinnings = splitPots[position];
                    pots.add(maxWinnings);
                }
                pot = Collections.min(pots);
                winnings = (int) (pot / splitWay);
            }
        }

        //Give the winners their chips
        for (int playerPos : winnerPosList) {
            playerList[playerPos].addChipCount(winnings);
        }

        userInterface.displayWinner(winnerPosArray, winningHandType, winnings, finalCards);

        //Redo the winner determination with the allIn winner set to folded
        if (winnerAllIn) {
            playerList[allInPos].setHasFolded(true);
            for (int i = 0; i < splitPots.length; i++) {
                splitPots[i] -= winnings;
                if (splitPots[i] <= 0) {
                    playerList[i].setHasFolded(true);
                }
            }
            showdown();
        }
    }

    /**
     * Method iterates through all the players in playerList
     *
     */
    private void bettingLoop() {
        lastRaisePos = playerPositions[0];
        firstBid = true;
        for (Player curPlayer : playerList) {
            curPlayer.setNewRound();
        }
        outerloop:
        // Loop of players betting
        while (true) {
            for (int i = 0; i < numPlayers; i++) {
                if (lastRaisePos == playerPositions[i] && !firstBid) {
                    break outerloop;
                }
                //Make sure the player is ellegible and able to bet
                if ((!getPlayerFolded(i)) && (!getPlayerBroke(i)) && (!getPlayerAllIn(i))) {
                    int[] playerAction;
                    playerAction = playerList[playerPositions[i]].playerAction(currentBid,
                            gameTable.returnTableCardsInt());
                    playerActions(playerPositions[i], playerAction);
                    firstBid = false;
                } else if (getPlayerAllIn(i)) {
                    firstBid = false;
                }
                sleep(2000);
            }

        }
    }

    /**
     * Method evaluates a player's action according to their input
     *
     * @param playerPos:int the current player making a move
     * @param playerInput:int the players action 1 for call, 2 for raise, 3 for
     * fold
     */
    private void playerActions(int playerPos, int playerInput[]) {
        int playerBet = playerInput[1];
        int playerTotalIn = playerInput[2];
        switch (playerInput[0]) { // playerMove
            case 1: { // Call
                addToPot(playerBet, 0);
                break;
            }
            case 2: { // Raise
                addToPot(playerBet, 0);
                /* Set new current bid to the total the player has bet*/
                setCurrentBid(playerTotalIn);
                lastRaisePos = playerPos;
                break;
            }
            case 3: { // Fold
                playerFolded(playerPos);
                break;
            }
            case 4: { //All in
                addToPot(playerBet, 0);
                /* Set new current bid to the total the player has bet if it is
                   more than the old bid.*/
                if (playerTotalIn > currentBid) {
                    lastRaisePos = playerPos;
                    setCurrentBid(playerTotalIn);
                    splitPots[playerPos] = pot;
                } else {
                    splitPots[playerPos] = pot;
                }
                break;
            }
            default: {

            }
        }
    }

    /**
     * Method deals cards from gameTable's deck
     *
     * @param num2Deal:int the number of cards to deal
     */
    private void dealTableCards(int num2Deal) {
        gameTable.dealTableCards(num2Deal);
    }

    /**
     * Determines who has the best hand out of the given hands and returns a
     * list of the game positions of the winners, along with the way they won.
     *
     * @param handStrengths Strengths of the players hands.
     * @param finalPositions The game positions of the given player hands.
     * @return A list where the first element is the method of winning and the
     * rest are the positions of the winners.
     */
    public List<Integer> determineWinners(int[][] handStrengths,
            int[] finalPositions) {
        List<Integer> winnerPositionsList = new ArrayList<>();
        int winnerPosHands = 0;
        boolean multiWinners = false;
        int curNumPlayers = finalPositions.length;

        //Set the first player in the list to the winner as an initial comparison
        winnerPositionsList.add(finalPositions[0]);

        outerloop:
        for (int i = 1; i < curNumPlayers; i++) {
            if (handStrengths[i][0] > handStrengths[winnerPosHands][0]) {
                winnerPosHands = i;
                multiWinners = false;
                winnerPositionsList.removeAll(winnerPositionsList);
            } else if (handStrengths[i][0] == handStrengths[winnerPosHands][0]) {
                if (handStrengths[i][1] > handStrengths[winnerPosHands][1]) {
                    winnerPosHands = i;
                    multiWinners = false;
                    winnerPositionsList.removeAll(winnerPositionsList);
                } else if (handStrengths[i][1] == handStrengths[winnerPosHands][1]) {
                    if (handStrengths[i][2] > handStrengths[winnerPosHands][2]) {
                        winnerPosHands = i;
                        multiWinners = false;
                        winnerPositionsList.removeAll(winnerPositionsList);
                    } else if (handStrengths[i][2] == handStrengths[winnerPosHands][2]) {
                        //Hand split pots resulting from equal hand strength
                        winnerPositionsList.add(finalPositions[winnerPosHands]);
                        winnerPosHands = i;
                        multiWinners = true;
                        winnerPositionsList.add(finalPositions[i]);
                    }
                }
            }
        }
        //If only one person is the winner add them to the list
        if (!multiWinners) {
            winnerPositionsList.clear();
            winnerPositionsList.add(finalPositions[winnerPosHands]);
        }
        //Add the hand the winner(s) have to the begining of the list
        winnerPositionsList.add(0, handStrengths[winnerPosHands][0]);
        return (winnerPositionsList);
    }

    /**
     * Returns the final cards to be shown for the game.
     *
     * @return
     */
    private Card[][] getFinalCards() {
        Card[][] finalCards = new Card[numPlayers][2];
        for (int playerPos : playerPositions) {
            if (!playerList[playerPos].getHasFolded()) {
                finalCards[playerPos] = playerList[playerPos].returnHand();
                playerList[playerPos].printHand();
            } else {
                finalCards[playerPos][0] = new Card(0, 0, true);
                finalCards[playerPos][1] = new Card(0, 0, true);
            }
        }
        return (finalCards);
    }

    ///////**** Series Method ****///////
    /**
     * Method sets up a new game series according to the parameters
     *
     * @param initNumPlayers int the number of players
     * @param initHumPos int the users position in the game
     */
    private void setupSeries(int initNumPlayers, int initHumPos) {
        userInterface.setupSeries(gameNum, initNumPlayers);
        sleep(1000);
        createPlayers(initNumPlayers, initHumPos);
        gameTable = new GameTable(numPlayers, GUIInUse);
    }

    /**
     * Sets up a saved game.
     *
     * @param saveNumPlayers Number of players in saved game.
     * @param savePlayerData The saved data for the players.
     */
    private void setupSeriesLoadGame(int saveNumPlayers, List<Integer> savePlayerData) {
        numPlayers = saveNumPlayers;
        userInterface.setupSeries(gameNum, numPlayers);
        sleep(500);

        playerList = new Player[saveNumPlayers];
        playerPositions = new int[numPlayers];
        splitPots = new int[saveNumPlayers];
        for (int i = 0; i < saveNumPlayers * 3; i += 3) {
            if (savePlayerData.get(i) == 0) {
                playerList[savePlayerData.get(i)] = new HumanPlayer(savePlayerData.get(i + 2),
                        handSize, GUIInUse, userInterface,
                        savePlayerData.get(i));
                humPos = savePlayerData.get(i);
            } else {
                playerList[savePlayerData.get(i)] = new CpuPlayer(savePlayerData.get(i + 2),
                        handSize, GUIInUse, userInterface,
                        savePlayerData.get(i));
            }
            playerPositions[savePlayerData.get(i)] = savePlayerData.get(i + 1);
        }
        gameTable = new GameTable(saveNumPlayers, GUIInUse);
    }

    /**
     * Method Creates a new playerList according to the parameters
     *
     * @param newNumPlayers:int the new number of players in the game
     * @param newHumPos:int the users new position in the game
     */
    private void createPlayers(int newNumPlayers, int newHumPos) {
        numPlayers = newNumPlayers;
        humPos = newHumPos;
        playerList = new Player[numPlayers];
        playerPositions = new int[numPlayers];
        splitPots = new int[numPlayers];

        // Makes array of players vals, human player has a value of 0
        for (int i = 0; i < humPos; i++) {
            playerPositions[i] = i + (numPlayers - humPos);
        }
        for (int i = humPos; i < numPlayers; i++) {
            playerPositions[i] = i - humPos;
        }

        playerList[0] = new HumanPlayer(startChips, handSize, GUIInUse,
                userInterface, 0);
        for (int i = 1; i < numPlayers; i++) {
            playerList[i] = new CpuPlayer(startChips, handSize, GUIInUse,
                    userInterface, i);
        }
    }

    /**
     * Method updates gameTables players, the new number of players, sets the
     * pot to 0 deals new cards to the players
     */
    private void setupGame() {
        // update the nums
        // updatePlayersSeries(numPlayers, humPos);
        gameTable.newGame(numPlayers);
        curNumPlayers = numPlayers;
        addToPot(-pot, 0);

        // Give players hands
        for (int i = 0; i < numPlayers; i++) {
            int curPlayerPos = playerPositions[i];
            playerList[curPlayerPos].setHand(gameTable.dealCardsOut(handSize,
                    curPlayerPos));
        }
    }

    /**
     * Method that updates the players after each game, should determine if
     */
    private void updatePlayersSeries() {
        int[] playerPosToKeep = new int[0];
        //Checks to see which players are broke and removes them from the game
        for (int i = 0; i < numPlayers; i++) {
            if (playerList[playerPositions[i]].getChipCount() <= 0) {
                setPlayerBroke(i);
            }
            if (!getPlayerBroke(i)) {
                setLastBet(i, 0);
                setPlayerFolded(i, false);
                setPlayerAllIn(i, false);
                int[] holder = new int[playerPosToKeep.length + 1];
                System.arraycopy(playerPosToKeep, 0, holder, 0, playerPosToKeep.length);
                holder[holder.length - 1] = playerPositions[i];
                playerPosToKeep = holder.clone();
            }
        }
        numPlayers = playerPosToKeep.length;
        Player[] newPlayerList = new Player[numPlayers];

        int n = 0;
        for (Player curPlayer : playerList) {
            if (!curPlayer.getBroke()) {
                newPlayerList[n] = curPlayer;
                n++;
            }
        }
        playerList = newPlayerList;
        playerPositions = playerPosToKeep;
    }

    /**
     * Method rotates the players position
     */
    private void rotatePlayerPositions() {
        if (humPos == (numPlayers - 1)) {
            humPos = 0;
        } else {
            humPos++;
        }
        int n = 0;
        for (int i = humPos; i < numPlayers; i++) {
            playerPositions[i] = n;
            n++;
        }
        for (int i = 0; i < humPos; i++) {
            playerPositions[i] = n;
            n++;
        }
    }

    ///////**** State Methods ****///////
    /**
     * Method returns a players hasFolded boolean from a player in playerList
     *
     * @param playerPos:int the specified player in playerList
     * @return boolean if the player has folded or not
     */
    private boolean getPlayerFolded(int playerPos) {
        return (playerList[playerPositions[playerPos]].getHasFolded());
    }

    /**
     * Returns true or false if the player at the given position has gone all in
     * or not.
     *
     * @param playerPos The position of the player
     * @return True of false whether or not the player has gone all in or not.
     */
    private boolean getPlayerAllIn(int playerPos) {
        return (playerList[playerPositions[playerPos]].getAllIn());
    }

    /**
     * Sets the player at the given position to the given boolean for all in.
     *
     * @param playerPos Position of the player to change.
     * @param newAllIn The new value to give the player.
     */
    private void setPlayerAllIn(int playerPos, boolean newAllIn) {
        playerList[playerPositions[playerPos]].setAllIn(newAllIn);
    }

    /**
     * Method returns a players broke boolean
     *
     * @param playerPos:int a player in playerList corresponding to playerPos
     * @return broke:boolean boolean true are false depending on the players
     * chip count
     */
    private boolean getPlayerBroke(int playerPos) {
        return (playerList[playerPositions[playerPos]].getBroke());
    }

    /**
     * Method updates pot according to the parameter
     *
     * @param numToAdd:int the amount of chips added to the pot
     */
    private void addToPot(int numToAdd, int totalIn) {
        pot += numToAdd;
        userInterface.updatePot(pot);

        for (Player curPlayer : playerList) {
            if (curPlayer.getAllIn()) {
                int lastBet = totalIn - numToAdd;
                if (lastBet < curPlayer.getLastBet()) {
                    splitPots[curPlayer.getPosition()] += Math.min(numToAdd,
                            curPlayer.getLastBet() - lastBet);
                }
            }
        }
    }

    /**
     * Sets the current bid to the provided bid, updating the user interface.
     *
     * @param newBid The new bid to set the current bid to.
     */
    private void setCurrentBid(int newBid) {
        currentBid = newBid;
        userInterface.updateCurrentBet(currentBid);
    }

    /**
     * Method resets each player in playerList's hasFolded boolean to false
     * after a game is over
     *
     * @param playerPos
     */
    private void playerFolded(int playerPos) {
        curNumPlayers--;
        playerList[playerPos].setHasFolded(true);
    }

    /**
     * Method sets a players hasFolded boolean to the boolean specified in the
     * parameter
     *
     * @param playerPos:int a player in playerList corresponding to the
     * playerPos
     * @param value:boolean
     */
    private void setPlayerFolded(int playerPos, boolean value) {
        playerList[playerPositions[playerPos]].setHasFolded(value);
    }

    /**
     * Method sets a players broke boolean to true if their chip count is zero
     *
     * @param playerPos:int a player in playerList corresponding to playerPos
     */
    private void setPlayerBroke(int playerPos) {
        numPlayers--;
        playerList[playerPositions[playerPos]].setBroke(true);
    }

    /**
     * Sets the last bet of the given player to the given amount.
     *
     * @param playerPos The position of the player in the game.
     * @param lastBet The new last bet of the player.
     */
    private void setLastBet(int playerPos, int lastBet) {
        playerList[playerPositions[playerPos]].setLastBet(lastBet);
    }

    ///////**** Static Methods ****///////
    /**
     * Pauses the execution of the current thread for a given amount of time.
     *
     * @param time Time to pause the program in milliseconds.
     */
    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
