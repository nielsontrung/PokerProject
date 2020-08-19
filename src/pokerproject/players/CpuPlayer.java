package pokerproject.players;

/**
 * Subclass of Player class. Computer controlled player type for out poker game.
 */
import java.util.ArrayList;
import java.util.List;
import pokerproject.machinery.Calculator;
import static pokerproject.machinery.Calculator.sortCardsBySuit;
import static pokerproject.machinery.Calculator.sortCardsByVal;
import pokerproject.userinterfaces.UIWrapper;

public class CpuPlayer extends Player {

    /**
     * Method used to get action of the computer player
     *
     * @param mustAllInFold:boolean makes the computer player to be in a fold
     * state when it goes all in
     * @return
     *
     */
    @Override
    protected int[] getAction(boolean mustAllInFold) {
        int[] action = new int[2];
        int gameStage = tableCards.length;
        int[][] avaCards = new int[handSize + gameStage][2];

        for (int i = 0; i < handSize; i++) {
            avaCards[i] = hand[i].getCardInt();
        }
        System.arraycopy(tableCards, 0, avaCards, handSize, gameStage);
        List<int[]> cardGroups = getGroups(avaCards);

        switch (gameStage) {
            case 0: { //Pre-flop
                action = preFlop(cardGroups, avaCards, mustAllInFold);
                break;
            }
            case 3: { //Flop
                action = fourthAndFlop(cardGroups, avaCards, mustAllInFold);
                break;
            }
            case 4: { //Fourth
                action = fourthAndFlop(cardGroups, avaCards, mustAllInFold);
                break;
            }
            case 5: { //River
                action = river(avaCards, mustAllInFold);
                break;
            }
            default: {
                action[0] = 3;
                action[1] = 0;
                break;
            }
        }
        action[1] = (int) (action[1] - action[1] % 10.0);
        if (action[1] == 0) {
            action[0] = 1;
        }
        return (action);
    }

    /**
     * Handles the CPUs actions when it is before the flop.
     *
     * @param cardGroups The groups of cards the player has access to.
     * @param avaCards The avaliable cards.
     * @param mustAllInFold If they must go all in or not.
     * @return
     */
    private int[] preFlop(List<int[]> cardGroups, int[][] avaCards,
            boolean mustAllInFold) {
        int[] action = new int[2];
        int maxBet;
        double betMultiplier = 0;

        /* Multiplier numbers are not precise, but can be modified to work of 
        more advanced statistics */
        for (int[] group : cardGroups) {
            switch (group[0]) {
                case 0: { //Pair
                    betMultiplier += 0.2 * (group[1] / 13.0);
                    break;
                }
                case 1: { //Straight
                    betMultiplier += 0.1 * (group[1] / 13.0);
                    break;
                }
                case 2: { //Flush
                    betMultiplier += 0.1 * (group[1] / 13.0);
                    break;
                }
                case 3: { //Straight flush
                    betMultiplier += 0.12 * (group[1] / 13.0);
                    break;
                }
                default: {
                    //Error
                }
            }
        }
        if (cardGroups.isEmpty()) {
            avaCards = Calculator.sortCardsByVal(avaCards);
            betMultiplier += 0.01 * (avaCards[0][0] / 13.0);
            betMultiplier += 0.005 * ((avaCards[1][0]) / 13.0);
        }
        maxBet = (int) (chipCount * betMultiplier);
        if (mustAllInFold) {
            if (maxBet < currentBid * 1.3) {
                action[0] = 3;
                action[1] = 0;
            } else {
                action[0] = 4;
                action[1] = chipCount;
            }
        } else {
            if (maxBet < currentBid * 1.2) {
                action[0] = 3;
                action[1] = 0;
            } else if (maxBet > chipCount * 1.1) {
                action[0] = 4;
                action[1] = chipCount;
            } else if ((maxBet - currentBid) > (chipCount * .1)) {
                action[0] = 2;
                action[1] = (int) ((maxBet - currentBid) * 0.1 + currentBid) + 10;
            } else {
                action[0] = 1;
                action[1] = 0;
            }
        }
        return (action);
    }

    /**
     * Handles the CPU actions for the flop and the fourth street stage.
     *
     * @param cardGroups The groups of cards the player has access to.
     * @param avaCards The avaliable cards.
     * @param mustAllInFold If they must go all in or not.
     * @return
     */
    private int[] fourthAndFlop(List<int[]> cardGroups, int[][] avaCards,
            boolean mustAllInFold) {
        int[] action = new int[2];
        int maxBet;
        double betMultiplier = 0;

        /* Multiplier numbers are not precise, but can be modified to work of 
        more advanced statistics */
        for (int[] group : cardGroups) {
            switch (group[0]) {
                case 0: { //Pair, triple, four of a kind
                    betMultiplier += 0.2 * (group[1] / 13.0) * group[3];
                    break;
                }
                case 1: { //Straight
                    if (group[3] > 3) {
                        betMultiplier += 0.25 * (group[1] / 13.0) * (group[3] / 5.0);
                    }
                    break;
                }
                case 2: { //Flush
                    if (group[3] > 3) {
                        betMultiplier += 0.25 * (group[1] / 13.0) * (group[3] / 5.0);
                    }
                    break;
                }
                case 3: { //Straight flush
                    if (group[3] > 3) {
                        betMultiplier += 0.3 * (group[1] / 13.0) * (group[3] / 5.0);
                    }
                    break;
                }
                default: {
                    //Error
                }
            }
        }
        if (cardGroups.isEmpty()) {
            avaCards = Calculator.sortCardsByVal(avaCards);
            betMultiplier += 0.01 * (avaCards[0][0] / 13.0);
            betMultiplier += 0.005 * (avaCards[1][0] / 13.0);
        }

        maxBet = (int) (chipCount * betMultiplier);
        if (mustAllInFold) {
            if (maxBet < currentBid * 1.3) {
                action[0] = 3;
                action[1] = 0;
            } else {
                action[0] = 4;
                action[1] = chipCount;
            }
        } else {
            if (maxBet < currentBid * 1.2) {
                action[0] = 3;
                action[1] = 0;
            } else if (maxBet > chipCount * 1.1) {
                action[0] = 4;
                action[1] = chipCount;
            } else if ((maxBet - currentBid) > (chipCount * .1)) {
                action[0] = 2;
                action[1] = (int) ((maxBet - currentBid) * 0.1 + currentBid) + 10;
            } else {
                action[0] = 1;
                action[1] = 0;
            }
        }
        return (action);
    }

    /**
     * Handles the CPU actions for the river stage.
     *
     * @param avaCards The avaliable cards.
     * @param mustAllInFold If the must go all in or not.
     * @return
     */
    private int[] river(int[][] avaCards,
            boolean mustAllInFold) {
        int[] handStrenght = Calculator.detHandStrength(avaCards);
        int[] action = new int[2];
        int maxBet;
        double betMultiplier = 0.1 * handStrenght[0] + 0.01 * (handStrenght[1] / 13.0);
        maxBet = (int) (chipCount * betMultiplier);

        if (mustAllInFold) {
            if (maxBet < currentBid * 1.3) {
                action[0] = 3;
                action[1] = 0;
            } else {
                action[0] = 4;
                action[1] = chipCount;
            }
        } else {
            if (maxBet < currentBid * 1.3) {
                action[0] = 3;
                action[1] = 0;
            } else if (maxBet > chipCount * 1.2) {
                action[0] = 4;
                action[1] = chipCount;
            } else if ((maxBet - currentBid) > (chipCount * .1)) {
                action[0] = 2;
                action[1] = (int) ((maxBet - currentBid) * 0.1 + currentBid) + 10;
            } else {
                action[0] = 1;
                action[1] = 0;
            }
        }
        return (action);
    }

    /**
     * Creates a List<int[]> that can be used to tell if the given cards have
     * groups of cards that; have the same value, the same suit, or are in a row
     * (mini straight). The data structure is <group type, value of first
     * card in group, suit of first card in group, number of cards in group>
     *
     * @param avaCards Cards that need to be sorted into groups
     * @return Data structure that holds required info about group.
     */
    private static List<int[]> getGroups(int[][] avaCards) {
        List<int[]> groups = new ArrayList<>();
        avaCards = sortCardsByVal(avaCards);
        boolean hasStraight = false;
        boolean hasFlush = false;

        /*Add groups of cards with same value to groups list, type = 0*/
        int[] holderCard = avaCards[avaCards.length - 1].clone();
        int count = 1;
        for (int i = avaCards.length - 2; i >= 0; i--) {
            int[] curCard = avaCards[i];
            if (holderCard[0] == curCard[0]) {
                count++;
            } else if (count != 1) {
                int[] intArray = {0, holderCard[0], holderCard[1], count};
                groups.add(intArray);
                count = 1;
            }
            if ((i == 0) && (count != 1)) {
                int[] intArray = {0, curCard[0], curCard[1], count};
                groups.add(intArray);
            }
            holderCard = curCard;
        }

        /*Add groups of cards in "mini" straights to groups list, type = 1*/
        holderCard = avaCards[avaCards.length - 1].clone();
        count = 1;
        for (int i = avaCards.length - 2; i >= 0; i--) {
            int[] curCard = avaCards[i];
            if (holderCard[0] != curCard[0]) {
                if (holderCard[0] == curCard[0] - 1) {
                    count++;
                } else if (count != 1) {
                    int[] intArray = {1, holderCard[0], holderCard[1], count};
                    groups.add(intArray);
                    count = 1;
                    hasStraight = true;
                }
                if ((i == 0) && (count != 1)) {
                    int[] intArray = {1, curCard[0], curCard[1], count};
                    groups.add(intArray);
                    hasStraight = true;
                }
            }
            holderCard = curCard;
        }

        /*Add groups of cards with same suit to groups list, type = 2*/
        avaCards = sortCardsBySuit(avaCards);
        holderCard = avaCards[avaCards.length - 1].clone();
        count = 1;
        for (int i = avaCards.length - 2; i >= 0; i--) {
            int[] curCard = avaCards[i];
            if (holderCard[1] == curCard[1]) {
                count++;
            } else if (count != 1) {
                int[] intArray = {2, holderCard[0], holderCard[1], count};
                groups.add(intArray);
                count = 1;
                hasFlush = true;
            }
            if ((i == 0) && (count != 1)) {
                int[] intArray = {2, curCard[0], curCard[1], count};
                groups.add(intArray);
                hasFlush = true;
            }
            holderCard = curCard;
        }

        /*Add groups of cards with same suit and in order to groups list, type = 3*/
        if (hasStraight && hasFlush) {
            holderCard = avaCards[avaCards.length - 1].clone();
            count = 1;
            for (int i = avaCards.length - 2; i >= 0; i--) {
                int[] curCard = avaCards[i];
                if ((holderCard[1] == curCard[1]) && (holderCard[0] == curCard[0] - 1)) {
                    count++;
                } else if (count != 1) {
                    int[] intArray = {3, holderCard[0], holderCard[1], count};
                    groups.add(intArray);
                    count = 1;
                    hasFlush = true;
                }
                if ((i == 0) && (count != 1)) {
                    int[] intArray = {3, curCard[0], curCard[1], count};
                    groups.add(intArray);
                    hasFlush = true;
                }
                holderCard = curCard;
            }
        }
        return (groups);
    }

    ///////****Constructors****///////
    /**
     * Creates a new CPU player with the given parameters.
     *
     * @param initChipCount The initial size of the player's chip count.
     * @param newHandSize The initial hand size of the player.
     * @param nGUIInUse Whether or not the GUI is in use.
     * @param userInterface The user interface the player should use to update
     * its visible objects.
     * @param playerPosition The position of the player in the game relative to
     * other players.
     */
    public CpuPlayer(int initChipCount, int newHandSize, boolean nGUIInUse, UIWrapper userInterface, int playerPosition) {
        super(initChipCount, newHandSize, nGUIInUse, userInterface,
                playerPosition);
    }
}
