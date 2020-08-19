package pokerproject.userinterfaces;

/**
 * Class representing the test based user interface
 */
import java.util.Scanner;
import pokerproject.Cards.Card;

public class TUIWrapper implements UIWrapper {

    private int numberOfPlayers;
    private int playerPos;
    private int userAction;
    private String[] tableCardsT = new String[5];
    private int tableCardIndex;
    String seriesStage;// flop, turn, river

    private int[] playerChipCounts;
    private int currentBet;
    private int currentPot;
    private int[] seriesContribution;

    private boolean validAnswer;
    Scanner input = new Scanner(System.in);

    /**
     * Method used to get the number of players and the position of the human
     * player
     *
     * @return The players number of players and the position of the human
     * player
     */
    @Override
    public int[] getInitPlayerConditions() {

        validAnswer = false;

        while (validAnswer == false) {
            input = new Scanner(System.in);
            System.out.println("How many players do you want?");
            if (input.hasNextInt()) {
                validAnswer = true;
            } else {
                System.out.println("Type in a valid int");
            }

        }
        numberOfPlayers = input.nextInt();

        playerChipCounts = new int[numberOfPlayers];
        seriesContribution = new int[numberOfPlayers];
        for (int i = 0; i < playerChipCounts.length; i++) {
            playerChipCounts[i] = 1000;
            seriesContribution[i] = 0;
        }

        validAnswer = false;
        while (validAnswer == false) {
            input = new Scanner(System.in);

            System.out.println("What is the human position?");
            if (input.hasNextInt()) {
                playerPos = input.nextInt();
                if (playerPos <= numberOfPlayers && playerPos >= 0) {
                    validAnswer = true;
                }

            } else {
                System.out.println("Type in a valid int");
            }
        }

        int[] initPlayConditions = new int[2];
        initPlayConditions[0] = numberOfPlayers;
        initPlayConditions[1] = playerPos;
        return initPlayConditions;
    }

    /**
     * Method used to get users action 1 for bet 2 for call 3 for fold 4 for
     * all-in
     *
     * @return The players the players action bet , call,fold, and amount of the
     * bet
     */
    @Override
    public int[] userAction(boolean mustGoAllInFold) {
        validAnswer = false;

        int[] playerAction = new int[2];
        int betNumber = 0;

        while (validAnswer == false) {
            input = new Scanner(System.in);
            System.out.println("\n\n");
            System.out.println("What would you like to do?");
            System.out.println("Call(1)?");
            System.out.println("Raise(2)?");
            System.out.println("Fold(3)?");
            System.out.println("All-in(4)?");
            if (input.hasNextInt()) {
                userAction = input.nextInt();
                if (userAction == 1 || userAction == 2 || userAction == 3) {
                    validAnswer = true;
                }
            } else {
                System.out.println("Type in a valid int");
            }
        }
        validAnswer = false;
        switch (userAction) {
            case 1: { //Call
                playerAction[0] = 1;
                playerAction[1] = currentBet;

                playerChipCounts[playerPos] -= (currentBet - seriesContribution[playerPos]);
                seriesContribution[playerPos] += currentBet;
                updatePot(currentPot + currentBet);
                System.out.println("You have $" + playerChipCounts[playerPos]);
                break;
            }
            case 2: { //Raise

                while (validAnswer = false) {
                    input = new Scanner(System.in);
                    System.out.println("How much do you want to raise?");
                    if (input.hasNextInt()) {
                        validAnswer = true;
                        betNumber = input.nextInt();
                        if (betNumber >= currentBet && betNumber <= playerChipCounts[playerPos]) {
                            playerAction[1] = betNumber;
                        } else {
                            System.out.println("Type in a valid number");
                        }

                    }
                }
                validAnswer = false;

                playerAction[0] = 2;
                playerAction[1] = betNumber;

                playerChipCounts[playerPos] -= betNumber;
                updateCurrentBet(currentBet + betNumber);
                updatePot(currentPot + betNumber);

                System.out.println("You have $" + playerChipCounts[playerPos]);

                break;
            }
            case 3: { //Fold
                playerAction[0] = 3;
                playerAction[1] = -1;
                break;
            }
            case 4: {//all in
                playerAction[0] = 4;
                playerAction[1] = playerChipCounts[playerPos];

            }
        }
        return playerAction;

    }

    /**
     * Method used to display the gameNumber and the number of player
     *
     * @param gameNum The game number
     * @param numPlayers The number of players in the game
     */
    public void updateStartGame(int gameNum, int numPlayers) {
        System.out.println("Game number: " + gameNum);
        System.out.println("The number of players: " + numPlayers);
        currentBet = 0;
    }

    /**
     * Method used to display the gameNumber and the number of player
     *
     * @param holeCards The set of cards that the human players have
     * @param playerPos The number of players in the game
     */
    @Override
    public void displayPlayerCards(Card[] holeCards, int playerPos) {
        String suit = "";
        String number = "";
        for (int i = 0; i < holeCards.length; i++) {
            if (holeCards[i].getSuit() == 1) {
                suit = "diamonds";
            } else if (holeCards[i].getSuit() == 2) {
                suit = "clubs";
            } else if (holeCards[i].getSuit() == 3) {
                suit = "hearts";
            } else if (holeCards[i].getSuit() == 4) {
                suit = "spades";
            }
            if (holeCards[i].getValue() == 14) {
                number = "Ace";
            } else if (holeCards[i].getValue() == 11) {
                number = "Jack";
            } else if (holeCards[i].getValue() == 12) {
                number = "Queen";
            } else if (holeCards[i].getValue() == 13) {
                number = "King";
            } else {
                number = "" + holeCards[i].getValue();
            }

            if (playerPos == 0) {
                System.out.println(number + " of " + suit);
            } else {
                System.out.println("\n\n\n");
            }

        }
    }

    /**
     * Method used to display the cards on the table
     *
     * @param tableCards The set of cards that are on the table
     *
     */
    @Override
    public void updateTableCards(Card[] tableCards) {
        String suit = "";
        String number = "";

        tableCardIndex = 0;
        for (Card tableCard1 : tableCards) {
            if (tableCard1.getSuit() == 1) {
                suit = "diamonds";
            } else if (tableCard1.getSuit() == 2) {
                suit = "clubs";
            } else if (tableCard1.getSuit() == 3) {
                suit = "hearts";
            } else if (tableCard1.getSuit() == 4) {
                suit = "spades";
            }
            if (tableCard1.getValue() == 14) {
                number = "Ace";
            } else if (tableCard1.getValue() == 11) {
                number = "Jack";
            } else if (tableCard1.getValue() == 12) {
                number = "Queen";
            } else if (tableCard1.getValue() == 13) {
                number = "King";
            } else {
                number = "" + tableCard1.getValue();
            }
            tableCardsT[tableCardIndex] = number + " of " + suit;

            tableCardIndex++;

        }
        for (int i = 0; i < tableCardIndex; i++) {
            System.out.println("Table cards have " + tableCardsT[i]);
        }

    }

    /**
     * Method used to display the amount of money in the pot
     *
     * @param curPot The amount of money in the current pot
     *
     */
    @Override
    public void updatePot(int curPot) {
        System.out.println("\n\n");
        System.out.println("The current pot is: $" + curPot);
        currentPot = curPot;

    }

    /**
     * Method used to display and update the action the player took
     *
     * @param playerPosition Position of the player
     * @param playerActions Action that the player chose to do
     *
     */
    @Override
    public void updatePlayerAction(int playerPosition, int[] playerActions) {
        switch (playerActions[0]) {
            case 1: {//Call
                System.out.println("\n\n");

                System.out.println("Player " + playerPosition + " has called");
                updateCurrentBet(currentBet);
                updatePot(currentPot + 10);

                playerChipCounts[playerPosition] -= (currentBet - seriesContribution[playerPos]);
                seriesContribution[playerPos] += currentBet;
                break;
            }
            case 2: {//Raise
                System.out.println("\n\n");
                System.out.println("Player " + playerPosition + " has raised $"
                        + playerActions[1]);
                updateCurrentBet(currentBet + playerActions[1]);
                updatePot(currentPot + currentBet);
                playerChipCounts[playerPosition] -= currentBet;
                break;
            }
            case 3: {//Fold
                System.out.println("\n\n");
                System.out.println("Player " + playerPosition + " has folded");
                break;
            }
            default: {
                System.out.println("Player " + playerPosition + " has called");
                break;
            }
        }

    }

    /**
     * Method used to display the current bet
     *
     * @param currentBid The current bet in the series
     *
     */
    @Override
    public void updateCurrentBet(int currentBid) {
        System.out.println("\n\n");
        System.out.println("Current bet is: $" + currentBid);
        currentBet = currentBid;

    }

    /**
     * Method used to display the game stage
     *
     * @param gameStage Stage of the game
     *
     */
    @Override
    public void updateGameStage(String gameStage) {
        System.out.println("\n\n\n");
        System.out.println("This is the: " + gameStage);
        currentBet = 0;
        for (int i = 0; i < seriesContribution.length; i++) {
            seriesContribution[i] = 0;
        }
    }

    /**
     * Method used to display the winner of the round
     *
     * @param winnerPos Position of the winner
     * @param winType Type of card the player won with
     * @param winnings Amount the player won
     * @param finalCards The set of cards the player had to win the round
     *
     */
    @Override
    public void displayWinner(int[] winnerPos, int winType, int winnings,
            Card[][] finalCards) {
        String number = "";
        String suit = "";
        String hand = "";
        String type = "";
        switch (winType) {
            case 10:
                type = "Royal Flush";
                break;
            case 9:
                type = "Straight Flush";
                break;
            case 8:
                type = "Four of a Kind";
                break;
            case 7:
                type = "Full House";
                break;
            case 6:
                type = "Flush";
                break;
            case 5:
                type = "Straight";
                break;
            case 4:
                type = "Three of a Kind";
                break;
            case 3:
                type = "Two Pairs";
                break;
            case 2:
                type = "Pair";
                break;
            case 1:
                type += "High Card";
                break;
            default:
                type = "Other Players Folded";
                break;
        }

        for (int i = 0; i < finalCards.length; i++) {
            for (int j = 0; j < finalCards[i].length; j++) {
                if (finalCards[i][j].getSuit() == 1) {
                    suit = "diamonds";
                } else if (finalCards[i][j].getSuit() == 2) {
                    suit = "clubs";
                } else if (finalCards[i][j].getSuit() == 3) {
                    suit = "hearts";
                } else if (finalCards[i][j].getSuit() == 4) {
                    suit = "spades";
                }
                if (finalCards[i][j].getValue() == 14) {
                    number = "Ace";
                } else if (finalCards[i][j].getValue() == 11) {
                    number = "Jack";
                } else if (finalCards[i][j].getValue() == 12) {
                    number = "Queen";
                } else if (finalCards[i][j].getValue() == 13) {
                    number = "King";
                } else {
                    number = "" + finalCards[i][j].getValue();
                }
                hand += number + " of " + suit + " ";
            }

        }

        System.out.println("\n\n\n");
        if (winnerPos.length == 1) {
            System.out.println("Player " + winnerPos[0] + " won $" + winnings + " with " + type);
            System.out.println("Winning hand had " + hand);
        } else if (winnerPos.length == 2) {
            System.out.println("Player " + winnerPos[0] + " and " + winnerPos[1] + " won $" + winnings + " with " + type);
            System.out.println("Winning hand had " + hand);
        }

        for (int i = 0; i < seriesContribution.length; i++) {
            seriesContribution[i] = 0;
        }

    }

    /**
     * Method used to display the number of chips
     *
     * @param playerPos Position of the player
     * @param newChip Number of chips the player has
     *
     */
    @Override
    public void updatePlayerChips(int playerPos, int newChip) {
        System.out.println("Player " + playerPos + " has $" + newChip);
    }

    /**
     * Method used to display the game number and the number of player when a
     * new game starts
     *
     * @param gameNum Game number
     * @param numPlayers Number of players in the game
     *
     */
    @Override
    public void newGame(int gameNum, int numPlayers) {
        System.out.println("Game Number: " + gameNum);
        System.out.println(" Number of players: " + numPlayers);
    }

    /**
     * Method used to display the final winner of the series
     *
     * @param playPos Position of the player
     * @param chipCount Number of chips the player has
     * @param bool Used to determine whether the player wins the series
     *
     */
    @Override
    public void displayFinalWinner(int playPos, int chipCount, boolean bool) {
        System.out.println("Player " + playPos + " has " + chipCount);

    }

    /**
     * Method used to display the series when it gets setup
     *
     * @param gameNum Game number
     * @param numPlayers Number of players
     *
     */
    @Override
    public void setupSeries(int gameNum, int numPlayers) {
        System.out.println("Starting the series of game number: " + gameNum);
        System.out.println("Number of players: " + numPlayers);
    }
}
