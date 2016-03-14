import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class MessageBox {

    public static void display( String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("");
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);

        Button okButton = new Button("Ok");

        okButton.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox(10);

        layout.getChildren().addAll(label, okButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

}