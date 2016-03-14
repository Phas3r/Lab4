import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import java.sql.Date;
import java.sql.SQLException;

public class InsertBox {


    public static void display( String message, DatabaseLogic dl, Scene s) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("");

        Label error = new Label();
        Label label = new Label();
        label.setText("Enter values to insert");
        GridPane.setConstraints(label, 0, 0);
        Button make = new Button("ok");

        Button goBack = new Button("Cancel");
        goBack.setOnAction(event ->{
            window.setScene(s);
        });

        VBox layout = new VBox(10);

        switch (message.toLowerCase()) {
            case "pilot":{

                Label l1 = new Label("ID:");
                GridPane.setConstraints(l1, 0, 0);

                TextField i1 = new TextField("1");
                GridPane.setConstraints(i1, 1, 0);

                Label l2 = new Label("NAME:");
                GridPane.setConstraints(l2, 0, 0);

                TextField i2 = new TextField("Smith");
                GridPane.setConstraints(i2, 1, 0);

                Label l3 = new Label("CREW:");
                GridPane.setConstraints(l3, 0, 0);

                TextField i3 = new TextField("default_crew");
                GridPane.setConstraints(i3, 1, 0);

                Label l4 = new Label("LIMIT:");
                GridPane.setConstraints(l4, 0, 0);

                TextField i4 = new TextField("40");
                GridPane.setConstraints(i4, 1, 0);

                make.setOnAction(event -> {
                    try {
                        dl.insertPilot(Integer.valueOf(i1.getText()), i2.getText(), i3.getText(), Integer.valueOf(i4.getText()));
                        error.setText("done");
                    } catch (SQLException e) {
                        MessageBox.display(e.getMessage());
                        error.setText("error");
                    }
                });

                layout.getChildren().addAll(label, l1, i1, l2, i2, l3, i3, l4, i4, make, goBack, error);
                break;
        }

            case "navigator": {
                Label l1 = new Label("ID:");
                GridPane.setConstraints(l1, 0, 0);

                TextField i1 = new TextField("1");
                GridPane.setConstraints(i1, 1, 0);

                Label l2 = new Label("NAME:");
                GridPane.setConstraints(l2, 0, 0);

                TextField i2 = new TextField("Smith");
                GridPane.setConstraints(i2, 1, 0);

                Label l3 = new Label("CREW:");
                GridPane.setConstraints(l3, 0, 0);

                TextField i3 = new TextField("default_crew");
                GridPane.setConstraints(i3, 1, 0);

                Label l4 = new Label("LIMIT:");
                GridPane.setConstraints(l4, 0, 0);

                TextField i4 = new TextField("40");
                GridPane.setConstraints(i4, 1, 0);

                make.setOnAction(event -> {
                    try {
                        dl.insertNavigator(Integer.valueOf(i1.getText()), i2.getText(), i3.getText(), Integer.valueOf(i4.getText()));
                        error.setText("done");
                    } catch (SQLException e) {
                        MessageBox.display(e.getMessage());
                        error.setText("error");
                    }
                });

                layout.getChildren().addAll(label, l1, i1, l2, i2, l3, i3, l4, i4, make, goBack, error);
                break;
            }
            case "trip": {
                Label l1 = new Label("ID:");
                GridPane.setConstraints(l1, 0, 0);

                TextField i1 = new TextField("1");
                GridPane.setConstraints(i1, 1, 0);

                Label l2 = new Label("DESTINATION:");
                GridPane.setConstraints(l2, 0, 0);

                TextField i2 = new TextField("Moscow");
                GridPane.setConstraints(i2, 1, 0);

                Label l3 = new Label("CREW:");
                GridPane.setConstraints(l3, 0, 0);

                TextField i3 = new TextField("default_crew");
                GridPane.setConstraints(i3, 1, 0);

                Label l4 = new Label("TIME OF FLIGHT:");
                GridPane.setConstraints(l4, 0, 0);

                TextField i4 = new TextField("4");
                GridPane.setConstraints(i4, 1, 0);

                Label l5 = new Label("COMPLEXITY COEF:");
                GridPane.setConstraints(l5, 0, 0);

                TextField i5 = new TextField("1.0");
                GridPane.setConstraints(i5, 1, 0);

                make.setOnAction(event -> {
                    try {
                        dl.insertTrip(Integer.valueOf(i1.getText()), i2.getText(), i3.getText(), Integer.valueOf(i4.getText()), Double.valueOf(i5.getText()));
                        error.setText("done");
                    } catch (SQLException e) {
                        MessageBox.display(e.getMessage());
                        error.setText("error");
                    }
                });

                layout.getChildren().addAll(label, l1, i1, l2, i2, l3, i3, l4, i4, l5, i5, make, goBack, error);
                break;
            }
            case "flight": {
                Label l1 = new Label("RECORD:");
                GridPane.setConstraints(l1, 0, 0);

                TextField i1 = new TextField("1");
                GridPane.setConstraints(i1, 1, 0);

                Label l2 = new Label("DATE:");
                GridPane.setConstraints(l2, 0, 0);

                TextField i2 = new TextField("2001-01-31");
                GridPane.setConstraints(i2, 1, 0);

                Label l3 = new Label("PILOT ID:");
                GridPane.setConstraints(l3, 0, 0);

                TextField i3 = new TextField("1");
                GridPane.setConstraints(i3, 1, 0);

                Label l4 = new Label("NAV ID:");
                GridPane.setConstraints(l4, 0, 0);

                TextField i4 = new TextField("1");
                GridPane.setConstraints(i4, 1, 0);

                Label l5 = new Label("TRIP ID:");
                GridPane.setConstraints(l5, 0, 0);

                TextField i5 = new TextField("1");
                GridPane.setConstraints(i5, 1, 0);

                Label l6 = new Label("FLIGHTS:");
                GridPane.setConstraints(l6, 0, 0);

                TextField i6 = new TextField("1");
                GridPane.setConstraints(i6, 1, 0);

                Label l7 = new Label("HOURS:");
                GridPane.setConstraints(l7, 0, 0);

                TextField i7 = new TextField();
                GridPane.setConstraints(i7, 1, 0);

                make.setOnAction(event -> {
                    try {
                        dl.insertFlight(Integer.valueOf(i1.getText()), Date.valueOf(i2.getText()),
                                Integer.valueOf(i3.getText()), Integer.valueOf(i4.getText()),
                                Integer.valueOf(i5.getText()), Integer.valueOf(i6.getText()),
                                Integer.valueOf(i7.getText()));
                        error.setText("done");
                    } catch (SQLException e) {
                        MessageBox.display(e.getMessage());
                        error.setText("error");
                    }
                });

                layout.getChildren().addAll(label, l1, i1, l2, i2, l3, i3, l4, i4,l5, i5, l6, i6, l7, i7,  make, goBack, error);
                break;
            }
            default:
                MessageBox.display("Wrong table name");
                break;

        }

        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout,600,600);
        window.setScene(scene);
        window.showAndWait();

    }

}