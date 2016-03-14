import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import java.sql.Date;
import java.sql.SQLException;

public class TextBox {


    public static void display(String message, DatabaseLogic dl, Scene s) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("");

        Label error = new Label();
        Label label = new Label();
        label.setText("Your output: ");
        GridPane.setConstraints(label, 0, 0);

        TextArea ta = TextAreaBuilder.create()
                .prefWidth(800)
                .prefHeight(600)
                .wrapText(true)
                .build();

        ta.appendText(message);



        Button goBack = new Button("Cancel");
        goBack.setOnAction(event -> {
            window.setScene(s);
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,ta, goBack);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}