package pokerproject.userinterfaces;

import pokerproject.Cards.Card;

import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.Stage;

/**
 * class GUI used as the main graphical user interface of our poker project
 * Methods:
 *
 * @author Tien
 *
 */
public class GUI extends Application {

    // instance variables:
    private static Stage window;
    private Button allInButton, betButton, foldButton, callButton,
            playButton, continueButton;
    private Label text1, text2;
    private static Button deck, loadButton;
    private static Label potLabel, playerChipCountLabel, currentBetLabel,
            playerMove, series, gameStage, label1, label2, errorLabel, gameNumLabel;
    private static int playerBet;
    private static Label[][] players = new Label[6][2];
    private static ImageView[] playerImages = new ImageView[6];
    private static Label[] winLabels = new Label[3];
    private static String[] winStrings = new String[3];
    private static ImageView[] tableCards = new ImageView[5];
    private static ImageView[] holeCards = new ImageView[2];
    private static ImageView[][] playerCards = new ImageView[6][2];
    private static Slider slider = null;
    private boolean check1, check2;
    private static Scene gameScene, menuScene, winScene;
    private int windowHeight = 600;
    private static int windowWidth = 800;
    //png card dimensions: 691 1056
    private int cardWidth = 138;
    private int cardHeight = 211;
    private static int cpuCardWidth = 83;
    private static int cpuCardHeight = 126;
    private static AnchorPane table;
    private static Card[][] winnerHands;
    private static VBox winBox;
    // Variables used to pass info to Wrapper, to know when to update and
    // communicate
    private static int numPlayers = 6;
    private static int playerPos;
    private int bet;
    static private int pot, currentBet;
    static private String gameStageString = "";
    static private boolean newPot = false;
    static private boolean newBet = false;
    static private boolean getUserAction = false;
    static private boolean newPlayerCards = false;
    static private boolean newTableCards = false;
    static private boolean newGameStage = false;
    static private boolean newUpdatePlayers = false;
    static private boolean newWinner = false;
    static private boolean finalWinner = false;
    static private int[] chipCounts = new int[6]; // Make this the size of the num of players
    static private String[] playerAction = new String[6];
    static private boolean[] needsUpdates = new boolean[numPlayers];
    static private boolean[] hasNewCards = new boolean[numPlayers];
    static private Card[][] handCards = new Card[numPlayers][2];
    static private Card[] tableCardsCard = new Card[5];
    static private int numTableCardsDisplayed = 0;
    static private int gameNumber = 1;
    static GUIWrapper wrapper;
    static MediaPlayer mp;

    @Override
    public void start(Stage primaryStage) {
        try {
            window = primaryStage;
            primaryStage.setOnCloseRequest(e -> {
                e.consume();
                boolean answer = ConfirmBox.display("", "Are you sure you want to close the program");
                if (answer) {
                    window.close();
                    System.exit(0);
                }
            });
            
            playMusic();
            makeMenuLayout();
            makeGameLayout();
            makeWinLayout();
                                  
            setButtonActivity(false);

            //// *** Handling Button Events ***////
            allInButton.setOnAction(e -> {
                int[] playerAction = new int[2];
                playerAction[0] = 4;
                playerAction[1] = chipCounts[0];
                wrapper.setPlayerAction(playerAction);
                setButtonActivity(false);
            });
            betButton.setOnAction((e) -> {
                int[] playerAction = new int[2];
                playerAction[0] = 2;
                playerAction[1] = bet;
                playerMove.setText("You Bet " + bet);
                wrapper.setPlayerAction(playerAction);
                playerBet = 0;
                setButtonActivity(false);
            });
            callButton.setOnAction((e) -> {
                int[] playerAction = new int[2];
                playerAction[0] = 1;
                playerAction[1] = wrapper.getCurrentBet();
                playerMove.setText("You Called");
                wrapper.setPlayerAction(playerAction);
                setButtonActivity(false);
            });
            foldButton.setOnAction((e) -> {
                int[] playerAction = new int[2];
                playerAction[0] = 3;
                playerAction[1] = -1;// this means they folded
                playerMove.setText("You Folded");
                wrapper.setPlayerAction(playerAction);
                setButtonActivity(false);
            });
            continueButton.setOnAction(e -> {
                wrapper.setWinnerViewed(true);
                winBox.setVisible(false);
                continueButton.setVisible(false);
                continueButton.setDisable(true);
            });            
            playButton.setOnAction(e -> {
                if (!text1.equals("") && !text2.equals("")) {
                	numPlayers = Integer.parseInt(text1.getText());
                	playerPos = Integer.parseInt(text2.getText());
                    wrapper.setInitPlayers(numPlayers, playerPos);
                    window.setScene(gameScene);
                }
            });
            loadButton.setOnAction(e -> {
                wrapper.setInitPlayers(0, 0);
                window.setScene(gameScene);
            });
            window.getIcons().add(new Image("file:CardImages/14S.PNG"));
            window.setScene(menuScene);
            window.setTitle("PokerGame");
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Thread to check when GUI is changed by external source and update the GUI */
        Thread update;
        update = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                    ;
                    if (getUserAction) {
                        setButtonActivity(true);
                        getUserAction = false;
                    }
                    if (newPlayerCards) {
                        /* Update player cards */
                        if (hasNewCards[0]) {
                            updateHoleCards();
                        }
                        for (int i = 1; i < numPlayers; i++) {
                            if (hasNewCards[i]) {
                                hasNewCards[i] = false;
                            }
                        }
                        Runnable updatePlayerCards = () -> {
                            gameNumLabel.setText("Game Number: " + gameNumber);
                        };
                        Platform.runLater(updatePlayerCards);
                        newPlayerCards = false;
                    }
                    if (newTableCards) {
                        updateTableCards();
                        Runnable updateTableCards = () -> {
                            window.show();
                        };
                        newTableCards = false;
                        Platform.runLater(updateTableCards);
                    }
                    if (newGameStage) {
                        Runnable updateGameStage = () -> {
                            gameStage.setText("This is the: " + gameStageString);
                        };
                        Platform.runLater(updateGameStage);
                        newGameStage = false;
                    }
                    if (newPot) {
                        Runnable updatePot = () -> {
                            potLabel.setText("Pot: " + pot);
                        };
                        Platform.runLater(updatePot);
                        newPot = false;
                    }
                    if (newBet) {
                        Runnable updateBet = () -> {
                            currentBetLabel.setText("Current Bet: " + currentBet);
                            slider.setMin(currentBet + 10);
                            betButton.setText("Bet " + (int) slider.getValue());
                        };
                        Platform.runLater(updateBet);
                        newBet = false;
                    }
                    if (newUpdatePlayers) {
                        Runnable updateChipCount = () -> {
                            for (int pos = 0; pos < needsUpdates.length; pos++) {
                                if (needsUpdates[pos]) {
                                    if (pos == 0) {
                                        playerChipCountLabel.setText("Chip Count: " + chipCounts[0]);
                                        slider.setMax(chipCounts[0]);
                                        betButton.setText("Bet " + (int) slider.getValue());
                                    } else {
                                        players[pos][0].setText("Chip Count: " + chipCounts[pos]);
                                        players[pos][1].setText(playerAction[pos]);
                                    }
                                }
                            }
                        };
                        Platform.runLater(updateChipCount);
                        newUpdatePlayers = false;
                    }
                    if (newWinner) {
                        Runnable udpateWinner = () -> {
                            winLabels[0].setText(winStrings[0]);
                            winLabels[1].setText(winStrings[1]);
                            winLabels[2].setText(winStrings[2]);
                            continueButton.setDisable(false);
                            continueButton.setVisible(true);
                            winBox = new VBox(winLabels[0], winLabels[1], winLabels[2], continueButton);
                            table.getChildren().add(winBox);
                            table.setRightAnchor(winBox, 10.0);
                            winBox.setAlignment(Pos.CENTER);
                            winBox.setLayoutX(windowWidth / 2);
                            for (int i = 0; i < winnerHands.length; i++) {
                                playerCards[i][0].setImage(winnerHands[i][0].getCardImage());
                                playerCards[i][1].setImage(winnerHands[i][1].getCardImage());
                            }

                        };
                        Platform.runLater(udpateWinner);
                        newWinner = false;
                    }
                    if (finalWinner) {
                        Runnable updateFinalWinner = () -> {
                            window.setScene(winScene);
                        };
                        Platform.runLater(updateFinalWinner);
                        finalWinner = false;
                    }
                }
            }
        };
        update.setDaemon(true);
        update.start();
    }
    
    private static void playMusic() {
    	String path = "makeoverpuzzle.mp3";
        Media media = new Media(Paths.get(path).toUri().toString()); 
        mp = new MediaPlayer(media);
        mp.setAutoPlay(true);
        mp.setVolume(0.9);
        mp.play();
    }

    ////*** Handling Button Events ***////
    //// *** Methods for Creating Scenes and Layouts ***////
    ////*** User Layout ***////
    /**
     * Makes buttons for user interaction
     *
     * @return buttonDisplay:HBox the layout containing the buttons
     */
    private HBox makeUserButtons() {
        double buttonWidth = 100;
        double buttonHeight = 50;
        allInButton = new Button("All In");
        allInButton.setVisible(false);
        allInButton.setDisable(true);
        betButton = new Button("Bet");
        betButton.setTooltip(new Tooltip("increase the size of an existing bet in the same betting round"));
        callButton = new Button("Call");
        callButton.setTooltip(new Tooltip("match a bet or match a raise"));
        foldButton = new Button("Fold");
        foldButton.setTooltip(new Tooltip("discard one's hand and forfeit interest in the current pot"));
        slider = new Slider();
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);	
        slider.setMajorTickUnit(100);
        slider.setMinorTickCount(10);
        slider.setBlockIncrement(10.0);
        slider.setSnapToTicks(true);
        slider.setMax(1000);
        slider.setMin(10);
        bet = 0;
        slider.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                bet = (int) slider.getValue();
                int roundBet = bet % 10;
                if (roundBet < 5) {
                    bet = bet - roundBet;
                } else {
                    bet = bet + (10 - roundBet);
                }
                betButton.textProperty().setValue(String.valueOf(bet));
                betButton.setText("Bet " + bet);
            }
        });
        betButton.setMinSize(buttonWidth, buttonHeight);
        callButton.setMinSize(buttonWidth, buttonHeight);
        foldButton.setMinSize(buttonWidth, buttonHeight);
        HBox playerHand = new HBox();
        holeCards[0] = new ImageView();
        holeCards[1] = new ImageView();
        for (ImageView card : holeCards) {
            card.setFitHeight(cardHeight);
            card.setFitWidth(cardWidth);
        }
        playerImages[0] = new ImageView(new Image("file:PlayerImages/player0.png"));
        playerImages[0].setFitHeight(100);
        playerImages[0].setFitWidth(100);
        playerHand.getChildren().addAll(holeCards[0], holeCards[1], playerImages[0]);
        playerHand.setPadding((new Insets(10, 10, 10, 10)));
        HBox buttonDisplay = new HBox(makeUserLabels(), allInButton, betButton,
                slider, callButton, foldButton, playerHand);
        buttonDisplay.setSpacing(10);
        buttonDisplay.setAlignment(Pos.CENTER);
        return buttonDisplay;
    }

    /**
     * Makes the users labels
     *
     * @return labelLayout:VBox the layout containing the players labels
     */
    private VBox makeUserLabels() {
        potLabel = new Label("Pot is: 0");
        potLabel.setStyle("-fx-font-size: 20;");
        playerChipCountLabel = new Label("Player Chip Count: ");
        playerChipCountLabel.setStyle("-fx-font-size: 20;");
        currentBetLabel = new Label("Current Bid: ");
        currentBetLabel.setStyle("-fx-font-size: 20;");
        playerMove = new Label();
        playerMove.setStyle("-fx-font-size: 20;");
        VBox labelLayout = new VBox(potLabel, currentBetLabel, playerChipCountLabel, playerMove);
        labelLayout.setPadding(new Insets(20, 10, 20, 10));
        labelLayout.setSpacing(20);
        return labelLayout;
    }

    ////*** CpuLayout ***////
    /**
     * Makes a layout corresponding to the position provided
     *
     * @param playerPos:int the position of the player that is being made
     * @return cpuPlayer:VBox the layout containing the computer player labels
     */
    private static VBox makeCpuLayout(int playerPos) {
        playerImages[playerPos] = new ImageView(new Image("file:PlayerImages/player" + playerPos + ".png"));
        playerImages[playerPos].setFitHeight(150);
        playerImages[playerPos].setFitWidth(150);
        VBox cpuPlayer = new VBox(playerImages[playerPos]);
        HBox cards = new HBox();
        cards.setStyle("-fx-padding: 3px");
        String name = "";
        if (playerPos == 1) {
            name = "Bob";
        } else if (playerPos == 2) {
            name = "Alpha";
        } else if (playerPos == 3) {
            name = "GERTY";
        } else if (playerPos == 4) {
            name = "Rosie";
        } else if (playerPos == 5) {
            name = "GLaDOS";
        }
        Label playerName = new Label(name);
        playerName.setStyle("-fx-font-size: 18;");
        cards.getChildren().addAll(playerCards[playerPos][0], playerCards[playerPos][1]);
        cards.setAlignment(Pos.CENTER);
        cpuPlayer.getChildren().addAll(playerName, players[playerPos][0], players[playerPos][1], cards);
        cpuPlayer.setAlignment(Pos.CENTER);
        cpuPlayer.setStyle("-fx-background-color: darkseagreen");
        cpuPlayer.setStyle("-fx-background-radius: 5.0");
        cpuPlayer.setLayoutX((windowWidth / numPlayers) * playerPos);
        cpuPlayer.setLayoutY(20);
        return cpuPlayer;
    }

    /**
     * Adds layouts representing a computer player to the main game layout of
     * the interface
     */
    private static void setCpuLayout() {
        // declare all player labels so they can be used
        for (int pos = 0; pos < 6; pos++) {
            players[pos][0] = new Label();
            players[pos][1] = new Label();
//          players[pos][0].setStyle("-fx-font-size: 15;");
//    		players[pos][1].setStyle("-fx-font-size: 15;");
        }
        for (int i = 0; i < 6; i++) {
            playerCards[i][0] = new ImageView();
            playerCards[i][1] = new ImageView();
            setCardImages(playerCards[i][0]);
            setCardImages(playerCards[i][1]);
        }
        for (int pos = 1; pos < numPlayers; pos++) {
            if (pos == 1) {
                table.getChildren().add(makeCpuLayout(pos));
            } else if (pos == 2) {
                table.getChildren().add(makeCpuLayout(pos));
            } else if ((pos > 2) && (pos < numPlayers - 2)) {
                table.getChildren().add(makeCpuLayout(pos));
            } else if (pos == numPlayers - 2) {
                table.getChildren().add(makeCpuLayout(pos));
            } else {
                table.getChildren().add(makeCpuLayout(pos));
            }
        }
    }

    /**
     * Customizes the computer player's card images
     *
     * @param card:ImageView the card being customized
     */
    private static void setCardImages(ImageView card) {
        card.setImage(new Image("file:CardImages/0Blank.png"));
        card.setFitHeight(cpuCardHeight);
        card.setFitWidth(cpuCardWidth);
    }

    ////*** Game Layout ***////
    /**
     * Method sets up and adds nodes to the table
     *
     * @return table:GridPane the layout containing the cpu players and table
     * cards
     */
    private AnchorPane makeTableImages(double sceneWidth, double sceneHeight) {
        table = new AnchorPane();
        ImageView deckImage = new ImageView(new Image("file:CardImages/0Blank.png"));
        deckImage.setFitHeight(cpuCardHeight);
        deckImage.setFitWidth(cpuCardWidth);
        deck = new Button("", deckImage);
        for (int i = 0; i < tableCards.length; i++) {
            tableCards[i] = new ImageView();
            tableCards[i].setFitHeight(cardHeight);
            tableCards[i].setFitWidth(cardWidth);
            table.getChildren().add(tableCards[i]);
            tableCards[i].setLayoutX((windowWidth / 5) * (i + 1) - (cardWidth / 2));
            tableCards[i].setLayoutY(windowHeight - 300);
        }

        table.setPadding(new Insets(10, 10, 10, 10));
        table.setStyle("-fx-background-color: darkseagreen");
        return table;
    }

    /**
     * Method creates an HBox layout showing the type of series and the game
     * number
     *
     * @return infoBar:HBox the layout of the infoBar displayed on the GUI
     */
    private VBox makeStatusBar() {
        VBox infoBar = new VBox();
        HBox statusBar = new HBox();
        series = new Label("Game Series is: Texax Hold'em");
        series.setStyle("-fx-font-size: 20;");
        gameStage = new Label("This is the: ");
        gameStage.setStyle("-fx-font-size: 20;");
        gameNumLabel = new Label("Game Number: " + gameNumber);
        gameNumLabel.setStyle("-fx-font-size: 20;");
        MenuBar menu = new MenuBar();
        Menu file = new Menu("File");
        Menu settings = new Menu("Settings");
        MenuItem save = new MenuItem("Save Game");
        MenuItem load = new MenuItem("Load Game");
        file.getItems().addAll(save, load);
        load.setOnAction(e -> {
        });
        menu.getMenus().addAll(file, settings);
        statusBar.setSpacing(20);
        statusBar.setPadding(new Insets(10, 10, 10, 10));
        statusBar.getChildren().addAll(series, gameNumLabel, gameStage);
        infoBar.getChildren().addAll(menu, statusBar);
        return infoBar;
    }
    

    ////*** Menu Layout ***////
    /**
     * Method makes a GridPane layout seen at run the program is first run where
     * the user inputs the desired number of players and their desired position
     * in the game
     *
     * @return mainMenu:GridPane
     */
    private void makeMenuLayout() {
        playButton = new Button("Click to Play!");
        loadButton = new Button("Load Previous Game");
        Label welcomeLabel = new Label("Welcome to our Poker Game!");
        welcomeLabel.setStyle("-fx-font-size: 26;");
        welcomeLabel.resize(welcomeLabel.getMinWidth(), welcomeLabel.getMinHeight());        
        label1 = new Label("Number of Players");
        label1.setStyle("-fx-font-size: 20;");
        label2 = new Label("Player Position");
        label2.setStyle("-fx-font-size: 20;");
        errorLabel = new Label("");
        text1 = new Label();
        text1.setStyle("-fx-font-size: 20; -fx-padding: 5px;");
        text2 = new Label();
        text2.setStyle("-fx-font-size: 20; -fx-padding: 5px;");
        text1.setMaxWidth(300);
        text2.setMaxWidth(300);
        
        // the number of players
        Button[] buttons = new Button[11];
        for(int i = 0; i < 11; i++) {
        	if(i < 5) {
        		buttons[i] = new Button(""+(i+2));	
        	}else {
        		buttons[i] = new Button(""+(i-4));
        	}
        }        
        
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent event) 
            { 
            	if(event.getSource() == buttons[0] || event.getSource() == buttons[1] || event.getSource() == buttons[2] ||
            			event.getSource() == buttons[3] || event.getSource() == buttons[4]) {
            		text1.setText(((Button) event.getSource()).getText());
            		
            	}else if(event.getSource() == buttons[5] ||event.getSource() == buttons[6] || event.getSource() == buttons[7] || 
            			event.getSource() == buttons[8] || event.getSource() == buttons[9] || event.getSource() == buttons[10]) {
            		text2.setText(((Button) event.getSource()).getText());            	
            	}            	
            } 
        };       
        
        for(int i = 0; i < 11; i++) {
        	buttons[i].setOnAction(eventHandler);  
        	buttons[i].setPadding(new Insets(5));
        }
        
        HBox playerNumOption = new HBox(buttons[0], buttons[1], buttons[2], buttons[3], buttons[4], text1);        
        HBox playerPosOption = new HBox(buttons[5], buttons[6], buttons[7], buttons[8], buttons[9], buttons[10], text2);        
        playerNumOption.setAlignment(Pos.TOP_CENTER);
        playerNumOption.setStyle("-fx-padding: 5px");
        playerPosOption.setAlignment(Pos.TOP_CENTER);
        playerPosOption.setStyle("-fx-padding: 5px");
        
        VBox menuLayout = new VBox(welcomeLabel, label1, playerNumOption, label2, playerPosOption,
                playButton, loadButton, errorLabel);
        menuLayout.setSpacing(8);
        menuLayout.setPadding(new Insets(10, 10, 10, 10));
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(6));
        menuScene = new Scene(menuLayout, 450, 360);
    }

    ////*** Game Layout ***////
    /**
     * Method makes a BorderPane containing other layouts of the game
     *
     * @param labels:VBox, the layout of the labels
     * @param buttonDisplay:HBox, the layout of the buttons
     * @param infoBar:HBox, the layout of the information of the game
     * @param table:GridPane, the layout of the table card
     * @param playerHand:GridPane, the layout of the player hands
     * @return
     */
    private void makeGameLayout() {
        BorderPane gameLayout = new BorderPane();
        gameLayout.setTop(makeStatusBar());
        gameLayout.setCenter(makeTableImages(gameLayout.getPrefWidth(), gameLayout.getPrefHeight()));        
        gameLayout.setBottom(makeUserButtons());
        gameScene = new Scene(gameLayout, gameLayout.getPrefWidth(), gameLayout.getPrefHeight());
    }

    /**
     * Method makes the winning scene used to display the winner(s) final pot
     * and the type of win
     *
     */
    public void makeWinLayout() {
        for (int i = 0; i < 3; i++) {
            winLabels[i] = new Label();
            winLabels[i].setStyle("-fx-font-size: 15pt;");
        }
        continueButton = new Button("Continue to Game");
        VBox winLayout = new VBox(winLabels);
        winLayout.getChildren().add(continueButton);
        continueButton.setStyle("-fx-font-size: 15pt;");
        winLayout.setAlignment(Pos.CENTER);
        winLayout.setSpacing(30);
        winLayout.setStyle("-fx-background: white;");
        winScene = new Scene(winLayout, windowWidth, windowHeight);
    }

    //// *** Actual Methods ***////

    /**
     * Method used to enable or disable button activity
     *
     * @param bool:boolean enable or disable buttons
     */
    public void setButtonActivity(boolean bool) {
        betButton.setDisable(!bool);
        foldButton.setDisable(!bool);
        callButton.setDisable(!bool);
        slider.setDisable(!bool);
    }

    /**
     * Method sets the ImageView objects images using the cards images
     */
    public static void updateTableCards() {
        for (int i = 0; i < numTableCardsDisplayed; i++) {
            tableCards[i].setImage(tableCardsCard[i].getCardImage());
        }
    }

    /**
     * Method updates the holeCards that will be displayed on the user interface
     */
    public static void updateHoleCards() {
        holeCards[0].setImage(handCards[0][0].getCardImage());
        holeCards[1].setImage(handCards[0][1].getCardImage());
        for (int i = 0; i < numPlayers; i++) {
            playerCards[i][0].setImage(new Image("file:CardImages/0Blank.png"));
            playerCards[i][1].setImage(new Image("file:CardImages/0Blank.png"));
        }
    }

    /**
     * Method resets the table cards and hole card images to null clears the
     * screen
     */
    public static void clearTable() {
        for (ImageView imageView : tableCards) {
            imageView.setImage(null);
        }
        for (ImageView imageView : holeCards) {
            imageView.setImage(null);
        }
        for (int i = 0; i < numPlayers; i++) {
            playerCards[i][0].setImage(null);
            playerCards[i][1].setImage(null);
        }
        for (int i = 0; i < numPlayers; i++) {
            playerAction[i] = "";
        }
    }

    //// *** Wrapper Communication ***////
    /**
     * Method sets getUserAction boolean according to the value given this
     * updates
     *
     * @param value:boolean
     */
    public static void setGetUserAction(boolean value) {
        getUserAction = value;
    }

    /**
     * Method updates the player hands according to the newHand
     *
     * @param newHand:Card[],the new player cards
     * @param playerPos:int,the player position
     */
    public static void setNewPlayerCards(Card[] newHand, int playerPos) {
        handCards[playerPos] = newHand;
        hasNewCards[playerPos] = true;
        newPlayerCards = true;
    }

    /**
     * Method updates the tableCardCards
     *
     * @param newTableHand:Card[] the new table cards
     */
    public static void setNewTableCards(Card[] newTableHand) {
        for (int i = 0; i < newTableHand.length; i++) {
            tableCardsCard[i + numTableCardsDisplayed] = newTableHand[i];
        }
        numTableCardsDisplayed = numTableCardsDisplayed + newTableHand.length;
        newTableCards = true;
    }

    /**
     * method clears the table and player hands and status labels
     *
     * @param gameNum
     * @param nNumPlayers
     */
    public static void newGame(int gameNum, int nNumPlayers) {
        gameNumber = gameNum;
        numPlayers = nNumPlayers;
        label1.setText("Number of Players: " + numPlayers);
        label2.setText("Player Position: " + playerPos);

        hasNewCards = new boolean[numPlayers];
        needsUpdates = new boolean[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            playerAction[i] = "";
        }
        clearTable();
        tableCardsCard = new Card[5];
        handCards = new Card[numPlayers][2];
        for (int i = 1; i < numPlayers; i++) {
            handCards[i][0] = new Card(0, 0, true);
            handCards[i][1] = new Card(0, 0, true);
            hasNewCards[i] = true;
        }
        numTableCardsDisplayed = 0;
        newPlayerCards = true;
    }

    /**
     * Method sets the player's bet to the new bet
     *
     * @param newBet:int the new bet
     */
    public static void setPlayerBet(int newBet) {
        playerBet = newBet;
    }

    /**
     * Method updates the game stage label according to the String nGameStage
     *
     * @param nGameStage:String the stage of the game ie, draw, flop,turn river
     */
    public static void setNewGameStage(String nGameStage) {
        gameStageString = nGameStage;
        newGameStage = true;
    }

    /**
     * Method updates the pot to newPot
     *
     * @param newPot:int the new pot
     */
    public static void setPot(int newPot) {
        pot = newPot;
        GUI.newPot = true;
    }

    /**
     * Method updates the currentBet label according to the newCurrentBet given
     *
     * @param newCurrentBet:int the new current bet
     */
    public static void newCurrentBet(int newCurrentBet) {
        newBet = true;
        currentBet = newCurrentBet;
    }

    /**
     * Method updates a players label according to the player position with the
     * chipCount and move
     *
     * @param playerPosition:int the player position
     * @param chipCount:int the player's chip count
     * @param action
     */
    public static void updatePlayerLabels(int playerPosition, int chipCount, String action) {
        int pos = playerPosition;
        int chips = chipCount;
        players[pos][0].setText("Chips: " + chips);
        players[pos][1].setText(action);
    }

    /**
     * Method updates a players label according to the player position and
     * player action.
     *
     * @param playerPosition:int The player position
     * @param playerActions:int[] An integer array containing the players bet
     * and their action
     */
    public static void updatePlayerAction(int playerPosition, int[] playerActions) {
        playerPos = playerPosition;
        switch (playerActions[0]) {
            case 1:
                playerAction[playerPos] = "Called";
                break;
            case 2:
                playerAction[playerPos] = "Bet: " + playerActions[1];
                break;
            case 3:
                playerAction[playerPos] = "Folded";
                break;
            case 4:
                playerAction[playerPos] = "All In";
                break;
            default:
                playerAction[playerPos] = "Folded";
                break;
        }
        newUpdatePlayers = true;
        needsUpdates[playerPos] = true;
    }

    /**
     * Updates the chips count of the player at the given position.
     *
     * @param playerPosition Position of player that needs updating.
     * @param chipCount New chip count.
     */
    public static void updatePlayerChips(int playerPosition, int chipCount) {
        chipCounts[playerPosition] = chipCount;
        newUpdatePlayers = true;
        needsUpdates[playerPosition] = true;
    }

    /**
     * Method to display the winner(s) of the current game, how they won and how
     * much.
     *
     * @param winPos
     * @param winType
     * @param winnings
     * @param winnerCards
     */
    public static void displayWinner(int[] winPos, int winType, int winnings, Card[][] winnerCards) {
        String message = "The Winning hand this game was a ";
        String winners = "The Winners of this round were Players";
        //// check the type of win and update the message displayed
        switch (winType) {
            case 10:
                message += "Royal Flush";
                break;
            case 9:
                message += "Straight Flush";
                break;
            case 8:
                message += "Four of a Kind";
                break;
            case 7:
                message += "Full House";
                break;
            case 6:
                message += "Flush";
                break;
            case 5:
                message += "Straight";
                break;
            case 4:
                message += "Three of a Kind";
                break;
            case 3:
                message += "Two Pairs";
                break;
            case 2:
                message += "Pair";
                break;
            case 1:
                message += "High Card";
                break;
            default:
                message = "Other Players Folded";
                break;
        }
        //// Check the Winning position(s)
        if (winPos.length == 1) {
            winners = "The Winner of this round was Player " + winPos[0]+1;
        } else {
            for (int i = 0; i < winPos.length; i++) {
                winners = winners + winPos[i] + ",";
                if (i == winPos.length - 1) {
                    winners = winners + winPos[i];
                }
            }
        }
        winnerHands = winnerCards;
        winStrings[0] = winners;
        winStrings[1] = message;
        winStrings[2] = "The Winnings this round: " + winnings;
        newWinner = true;
    }

    /**
     *
     * @param playerPosition
     * @param chipCount
     */
    public static void displayFinalWinner(int playerPosition, int chipCount) {
        String winner;
        switch (playerPosition) {
            case 0:
                winner = "You";
                break;
            case 1:
                winner = "bob";
                break;
            case 2:
                winner = "alpha";
                break;
            case 3:
                winner = "GERTY";
                break;
            case 4:
                winner = "Rosie";
                break;
            default:
                winner = "GLaDOS";
                break;
        }

        chipCounts[playerPosition] = chipCount;
        winStrings[0] = "The Winner of This Series is " + winner;
        finalWinner = true;
    }

    /**
     *
     */
    public static void updateAllIn() {

    }

    /**
     * Set up the GUI for a series.
     *
     * @param gameNum The game number.
     * @param nNumPlayers The number of players.
     */
    public static void setupSeries(int gameNum, int nNumPlayers) {
        gameNumber = gameNum;
        numPlayers = nNumPlayers;
        Runnable setCPULayout = () -> {
            setCpuLayout();
            clearTable();
        };
        Platform.runLater(setCPULayout);
    }

}
