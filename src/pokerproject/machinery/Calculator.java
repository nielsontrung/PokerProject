package pokerproject.machinery;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that functions as a Calculator(Dealer) for the mathematics(including
 * determining card hand strengths, which is simply a comparison of numbers) for
 * the pokerproject package.
 *
 * @author thomasnewton
 */
public class Calculator {

    ////** Public **////
    /**
     * Determines the strength of a hand and gives an array of integers
     * representing the hands strength.
     *
     * @param avaCards
     * @param newAvaCards
     * @return Array of ints that represent the hands power (hand,val,suit)
     */
    public static int[] detHandStrength(int[][] avaCards) {
        int[] handStrength = {0, 0, 0};
        int[][] hand = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[] zero = {0, 0};

        for (int i = 10; i > 0; i--) {
            switch (i) {
                case 10: {
                    hand = returnRoyal(avaCards);
                    break;
                }
                case 9: {
                    hand = returnStraightFlush(avaCards);
                    break;
                }
                case 8: {
                    hand = returnNKind(avaCards, 4);
                    break;
                }
                case 7: {
                    hand = returnFullHouse(avaCards);
                    break;
                }
                case 6: {
                    hand = returnFlush(avaCards);
                    break;
                }
                case 5: {
                    hand = returnStraight(avaCards);
                    break;
                }
                case 4: {
                    hand = returnNKind(avaCards, 3);
                    break;
                }
                case 3: {
                    hand = return2Pair(avaCards);
                    break;
                }
                case 2: {
                    hand = returnNKind(avaCards, 2);
                    break;
                }
                case 1: {
                    avaCards = sortCardsByVal(avaCards);
                    hand = Arrays.copyOfRange(avaCards, 0, 5);
                    break;
                }
            }
            if (!isEqual(hand[0], zero)) {
                handStrength[0] = i;
                handStrength[1] = hand[0][0];
                handStrength[2] = hand[0][1];
                return (handStrength);
            }
        }
        return (handStrength);
    }

    /**
     * Sorts a array of card values by values.
     *
     * @param cards2Sort
     * @return The sorted list of cards.
     */
    public static int[][] sortCardsByVal(int[][] cards2Sort) {
        int[][] sortedCards = new int[cards2Sort.length][2];
        int[] holderCard1 = new int[2];
        int[] holderCard2 = new int[2];
        sortedCards[0] = new int[]{0, 0};
        for (int i = 0; i < cards2Sort.length; i++) {
            outerloop:
            for (int j = 0; j < 1 + i; j++) {
                if (sortedCards[j][0] < cards2Sort[i][0]) {
                    holderCard1 = sortedCards[j];
                    sortedCards[j] = cards2Sort[i];
                    for (int n = j + 1; n < i + 1; n++) {
                        holderCard2 = sortedCards[n];
                        sortedCards[n] = holderCard1;
                        holderCard1 = holderCard2;
                    }
                    break outerloop;
                } else if (sortedCards[j][0] == cards2Sort[i][0]) {
                    if (sortedCards[j][1] < cards2Sort[i][1]) {
                        holderCard1 = sortedCards[j];
                        sortedCards[j] = cards2Sort[i];
                        for (int n = j + 1; n < i + 1; n++) {
                            holderCard2 = sortedCards[n];
                            sortedCards[n] = holderCard1;
                            holderCard1 = holderCard2;
                        }
                        break outerloop;
                    }
                }
            }
        }

        return (sortedCards);
    }

    /**
     * Sorts a array of card values by suit.
     *
     * @param cards2Sort
     * @return The sorted list of cards.
     */
    public static int[][] sortCardsBySuit(int[][] cards2Sort) {
        int[][] sortedCards = new int[cards2Sort.length][2];
        int[] holderCard1 = new int[2];
        int[] holderCard2 = new int[2];
        sortedCards[0] = cards2Sort[0];
        for (int i = 1; i < cards2Sort.length; i++) {
            outerloop:
            for (int j = 0; j < i + 1; j++) {
                if (sortedCards[j][1] < cards2Sort[i][1]) {
                    holderCard1 = sortedCards[j];
                    sortedCards[j] = cards2Sort[i];
                    for (int n = j + 1; n < i + 1; n++) {
                        holderCard2 = sortedCards[n];
                        sortedCards[n] = holderCard1;
                        holderCard1 = holderCard2;
                    }
                    break outerloop;
                } else if (sortedCards[j][1] == cards2Sort[i][1]) {
                    if (sortedCards[j][0] < cards2Sort[i][0]) {
                        holderCard1 = sortedCards[j];
                        sortedCards[j] = cards2Sort[i];
                        for (int n = j + 1; n < i + 1; n++) {
                            holderCard2 = sortedCards[n];
                            sortedCards[n] = holderCard1;
                            holderCard1 = holderCard2;
                        }
                        break outerloop;
                    }
                } else if (j == (i)) {
                    sortedCards[j] = cards2Sort[i];
                }
            }
        }
        return (sortedCards);
    }

    /**
     * Combines two hands into one and returns it.
     *
     * @param hand1
     * @param hand2
     * @return Combination of hand1 and hand2.
     */
    public static int[][] combineHands(int[][] hand1, int[][] hand2) {
        int length = hand1.length + hand2.length;
        int[][] result = new int[length][];
        System.arraycopy(hand1, 0, result, 0, hand1.length);
        System.arraycopy(hand2, 0, result, hand1.length, hand2.length);
        return result;
    }

    /**
     * Tests to see if two hands are the same.
     *
     * @param hand1
     * @param hand2
     * @return True or false if the cards are equal or not.
     */
    public static boolean isEqual(int[] hand1, int[] hand2) {
        if ((hand1[0] == hand2[0]) && (hand1[1] == hand2[1])) {
            return (true);
        }
        return (false);
    }

    ////** Private **////

    /*Hand returning functions*/
    /**
     * If the given cards have a royal flush, the best royal flush in the hand
     * is returned.
     *
     * @param avaCards The avaliable cards.
     * @return The royal in the avaliable cards or a blank hand.
     */
    private static int[][] returnRoyal(int[][] avaCards) {
        avaCards = sortCardsBySuit(avaCards);
        boolean isRoyal = true;
        int[][] hand = new int[5][2];
        int suitVal;
        int count;

        outerloop:
        for (int i = 0; i < avaCards.length; i++) {
            if (avaCards[i][0] == 14) {
                suitVal = avaCards[i][1];
                count = 1;
                for (int n = 1; n < avaCards.length - i; n++) {
                    if ((avaCards[i + n][0] == 14 - n) && (avaCards[i + n][1] == suitVal)) {
                        count++;
                        if (count == 5);
                        {
                            isRoyal = true;
                            hand = Arrays.copyOfRange(avaCards, i, 5 + i);
                            break outerloop;
                        }
                    }
                }
            } else if (i == 3) {
                isRoyal = false;
                break outerloop;
            }
        }
        if (isRoyal == false) {
            hand = new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        }
        return (hand);
    }

    /**
     * If the given cards have a straight flush, the best straight flush in the
     * hand is returned.
     *
     * @param avaCards The avaliable cards.
     * @return The straight flush in the avaliable cards or a blank hand.
     */
    private static int[][] returnStraightFlush(int[][] avaCards) {
        avaCards = sortCardsBySuit(avaCards);
        int[][] hand = new int[5][2];
        hand = new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int count = 1;

        for (int i = 1; i < avaCards.length; i++) {
            if ((avaCards[i][1] == avaCards[i - 1][1]) && (avaCards[i][0] == (avaCards[i - 1][0] - 1))) {
                count++;
            } else {
                count = 1;
            }
            if (count == 5) {
                hand = Arrays.copyOfRange(avaCards, i - 4, i + 1);
                return (hand);
            }
        }
        return (hand);
    }

    /**
     * If the given cards has n of the same cards, these cards are returned.
     *
     * @param avaCards The avaliable cards.
     * @return The group of cards in the avaliable cards or a blank hand.
     */
    private static int[][] returnNKind(int[][] avaCards, int n) {
        avaCards = sortCardsByVal(avaCards);
        int[][] hand = new int[5][2];
        hand = new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int count = 1;

        for (int i = 1; i < avaCards.length; i++) {
            if (avaCards[i][0] == avaCards[i - 1][0]) {
                count++;
            } else {
                count = 1;
            }
            if (count == n) {
                int[][] hand1 = Arrays.copyOfRange(avaCards, i - n + 1, i + 1);
                int[][] hand2 = new int[5 - n][];
                int count2 = 0;
                int count3 = 0;
                outerloop1:
                for (int j = 0; j < avaCards.length; j++) {
                    outerloop2:
                    for (int k = 0; k < hand1.length; k++) {
                        if (!isEqual(avaCards[j], hand1[k])) {
                            count3++;
                        }
                    }
                    if (count3 == hand1.length) {
                        hand2[count2] = avaCards[j];
                        count2++;
                    }
                    if (count2 == (5 - n)) {
                        break outerloop1;
                    }
                    count3 = 0;
                }
                hand = combineHands(hand1, hand2);
                return (hand);
            }
        }
        return (hand);
    }

    /**
     * If the given cards have a full house, the best full house in the hand is
     * returned.
     *
     * @param avaCards The avaliable cards.
     * @return The full house in the avaliable cards or a blank hand.
     */
    private static int[][] returnFullHouse(int[][] avaCards) {
        avaCards = sortCardsByVal(avaCards);
        int[][] hand = new int[5][2];
        hand = new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int tCount = 1;
        int dCount = 1;
        boolean tripCard = false;
        int tripVal = 0;

        for (int i = 1; i < avaCards.length; i++) {
            if (avaCards[i][0] == avaCards[i - 1][0]) {
                tCount++;
            } else {
                tCount = 1;
            }
            if (tCount == 3) {
                tripCard = true;
                tripVal = avaCards[i][0];
            }
            if (tripCard == true) {
                for (int j = 1; j < avaCards.length; j++) {
                    if ((avaCards[j][0] == avaCards[j - 1][0]) && (avaCards[j][0] != tripVal)) {
                        dCount++;
                    } else {
                        dCount = 1;
                    }
                    if (dCount == 2) {
                        int[][] hand1 = Arrays.copyOfRange(avaCards, i - 2, i + 1);
                        int[][] hand2 = Arrays.copyOfRange(avaCards, j - 1, j + 1);
                        hand = combineHands(hand1, hand2);
                        return (hand);
                    }
                }
            }
            tripCard = false;
        }
        return (hand);
    }

    /**
     * If the given cards have a flush, the best flush in the hand is returned.
     *
     * @param avaCards The avaliable cards.
     * @return The flush in the avaliable cards or a blank hand.
     */
    private static int[][] returnFlush(int[][] avaCards) {
        avaCards = sortCardsBySuit(avaCards);
        int[][] hand = new int[5][2];
        hand = new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int count = 1;

        for (int i = 1; i < avaCards.length; i++) {
            if (avaCards[i][1] == avaCards[i - 1][1]) {
                count++;
            } else {
                count = 1;
            }
            if (count == 5) {
                hand = Arrays.copyOfRange(avaCards, i - 4, i + 1);
                return (hand);
            }
        }
        return (hand);
    }

    /**
     * If the given cards have a straight, the best straight in the hand is
     * returned.
     *
     * @param avaCards The avaliable cards.
     * @return The straight in the avaliable cards or a blank hand.
     */
    private static int[][] returnStraight(int[][] avaCards) {
        avaCards = sortCardsByVal(avaCards);
        int[][] hand = new int[5][2];
        hand = new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int count = 1;

        for (int i = 1; i < avaCards.length; i++) {
            if (avaCards[i][0] == (avaCards[i - 1][0] - 1)) {
                count++;
            } else {
                count = 1;
            }
            if (count == 5) {
                hand = Arrays.copyOfRange(avaCards, i - 4, i + 1);
                return (hand);
            }
        }
        return (hand);
    }

    /**
     * If the given cards have a two pair, the best two pair in the hand is
     * returned.
     *
     * @param avaCards The avaliable cards.
     * @return The two pair in the avaliable cards or a blank hand.
     */
    private static int[][] return2Pair(int[][] avaCards) {
        avaCards = sortCardsByVal(avaCards);
        int[][] hand = new int[5][2];
        hand = new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int tCount = 1;
        int dCount = 1;
        boolean tripCard = false;
        int tripVal = 0;

        for (int i = 1; i < avaCards.length; i++) {
            if (avaCards[i][0] == avaCards[i - 1][0]) {
                tCount++;
            } else {
                tCount = 1;
            }
            if (tCount == 2) {
                tripCard = true;
                tripVal = avaCards[i][0];
            }
            if (tripCard == true) {
                for (int j = 1; j < avaCards.length; j++) {
                    if ((avaCards[j][0] == avaCards[j - 1][0]) && (avaCards[j][0] != tripVal)) {
                        dCount++;
                    } else {
                        dCount = 1;
                    }
                    if (dCount == 2) {
                        int[][] hand1 = Arrays.copyOfRange(avaCards, i - 1, i + 1);
                        int[][] hand2 = Arrays.copyOfRange(avaCards, j - 1, j + 1);
                        hand1 = combineHands(hand1, hand2);
                        hand2 = new int[1][2];
                        int count2 = 0;
                        int count3 = 0;
                        outerloop1:
                        for (int h = 0; h < avaCards.length; h++) {
                            outerloop2:
                            for (int k = 0; k < hand1.length; k++) {
                                if (!isEqual(avaCards[h], hand1[k])) {
                                    count3++;
                                }
                            }
                            if (count3 == hand1.length) {
                                hand2[count2] = avaCards[h];
                                count2++;
                            }
                            if (count2 == (1)) {
                                break outerloop1;
                            }
                            count3 = 0;
                        }
                        hand = combineHands(hand1, hand2);
                        return (hand);
                    }
                }
            }
            tripCard = false;
        }
        return (hand);
    }
}
