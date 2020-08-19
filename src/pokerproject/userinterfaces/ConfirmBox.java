package pokerproject.userinterfaces;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Java class used to confirm player input Instance variables: boolean (answer)
 * Methods: display(String title,String message)
 */
public class ConfirmBox {

    //instance variables
    static boolean answer;

    //Methods
    /**
     * Creates a confirmation box with a yes or no for a GUI application.
     *
     * @param title The title of the box.
     * @param message The message to display on the box.
     * @return True or false dependant on the answer.
     */
    public static boolean display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("");
        window.setMinWidth(300);

        Label label = new Label(message);
        Button yes = new Button("Yes");
        Button no = new Button("No");

        yes.setOnAction(e -> {
            answer = true;
            window.close();
        });
        no.setOnAction(e -> {
            answer = false;
            window.close();
        });
        VBox layout = new VBox();
        layout.getChildren().addAll(label, yes, no);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.showAndWait();
        return answer;
    }
}
