package pokerproject;

import java.util.Scanner;
import java.util.InputMismatchException;
import pokerproject.machinery.Engine;
import pokerproject.userinterfaces.GUIWrapper;
import pokerproject.userinterfaces.TUIWrapper;
import pokerproject.userinterfaces.UIWrapper;

/**
 * Opening class for the pokerproject package. Class gets some basic info about
 * how to run the game and then initiates the engine and appropriate interface.
 *
 */
public class PokerProject {

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        UIWrapper userInterface = null;
        boolean GUIInUse = false;
        boolean badInput = true;
        Engine engine;
        int option = 0;
        while (badInput) {
            System.out.println("What sort of interface do you want?");
            System.out.println("(1) Graphical");
            System.out.println("(2) Textbased");
            try {
                option = keyboard.nextInt();
                if (option == 1 || option == 2) {
                    switch (option) {
                        case 1: {
                            userInterface = new GUIWrapper();
                            GUIInUse = true;
                            badInput = false;
                            break;
                        }
                        case 2: {
                            userInterface = new TUIWrapper();
                            GUIInUse = false;
                            badInput = false;
                            break;
                        }
                    }
                } else {
                    System.out.println("Please enter 1 or 2");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a integer");
                keyboard.next();
            }
        }
        engine = new Engine(userInterface, GUIInUse);
    }
}
