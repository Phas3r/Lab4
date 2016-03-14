import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import javafx.application.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.geometry.Insets;

public class Main extends Application {
    public static void main(String[] args){
        dl=new DatabaseLogic();
        try {
            launch(args);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        //Button 1
        Label label1 = new Label("Connect to database:");
        Label error = new Label("");
        GridPane.setConstraints(error, 0, 0);

        Label urlLabel = new Label("Server url:");
        GridPane.setConstraints(urlLabel, 0, 0);

        //Name Input
        TextField urlInput = new TextField("jdbc:postgresql://localhost");
        GridPane.setConstraints(urlInput, 1, 0);

        Label portLabel = new Label("Port");
        GridPane.setConstraints(urlLabel, 0, 0);

        //Name Input
        TextField portInput = new TextField("5432");
        GridPane.setConstraints(urlInput, 1, 0);


        //Name Label - constrains use (child, column, row)
        Label nameLabel = new Label("Username:");
        GridPane.setConstraints(nameLabel, 0, 0);

        //Name Input
        TextField nameInput = new TextField("postgres");
        GridPane.setConstraints(nameInput, 1, 0);

        //Password Label
        Label passLabel = new Label("Password:");
        GridPane.setConstraints(passLabel, 0, 1);

        //Password Input
        TextField passInput = new TextField("224244");
        passInput.setPromptText("password");
        GridPane.setConstraints(passInput, 1, 1);

        Button buttonConnect = new Button("Connect");
        buttonConnect.setOnAction(event ->
                {
                    try {
                        dl.connect(urlInput.getText(), portInput.getText(), nameInput.getText(), passInput.getText());
                    }
                    catch (SQLException ex){
                        System.out.println("no");
                    }
                    if (dl.isConnected()) {
                        error.setText("");
                        window.setScene(sceneMain);
                    }
                    else{
                        error.setText("Can't connect");
                    }
                });

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label1, urlLabel, urlInput, portLabel, portInput, nameLabel, nameInput, passLabel, passInput, buttonConnect, error);

        sceneConnect = new Scene(layout, 400, 400);

        makeMain();
        make1();
        make2();
        make3();
        make4();
        make5();
        make6();
        make7();


        window.setScene(sceneConnect);
        window.setTitle("Kostyl application for DB");
        window.show();
    }

    private void makeMain(){

        Button buttonConnect = new Button("Return to connection");
        buttonConnect.setOnAction(event -> {
            try {
                dl.disconnect();
            }
            catch (SQLException ex){

            }
            if (!dl.isConnected()) {
                window.setScene(sceneConnect);
            }
        });

        Button b1 = new Button("Create database");
        b1.setOnAction(event->{

            window.setScene(scene1);
        });

        Button b2 = new Button("Remove database");
        b2.setOnAction(event->{
            if (!dl.hasDB()){
                MessageBox.display( "Database " + dl.getName() + " is already deleted");
            }
            boolean answer=ConfirmBox.display("Are you sur you want to delete "+dl.getName()+" database?");
            if (answer) {
                try {
                    dl.deleteDB();
                    MessageBox.display( "Database " + dl.getName() + " has been deleted");
                } catch (SQLException e) {
                    MessageBox.display( "Error occurred");
                }
            }
        });
        Button b3 = new Button("Clear table");
        b3.setOnAction(event->{
            window.setScene(scene3);
        });
        Button b4 = new Button("Insert new data");
        b4.setOnAction(event->{
            window.setScene(scene4);
        });
        Button b5 = new Button("Search");
        b5.setOnAction(event->{
            window.setScene(scene5);
        });

        Button b6 = new Button("Change record");
        b6.setOnAction(event->{
            window.setScene(scene6);
        });

        Button b7 = new Button("Remove pilot");
        b7.setOnAction(event->{
            window.setScene(scene7);
        });

        Button b8 = new Button("Show table");
        b8.setOnAction(event->{
            window.setScene(scene2);
        });

        VBox layout = new VBox(20);
        Label error = new Label("");
        layout.getChildren().addAll(b1, b2, b3, b4, b5, b6, b7, b8, buttonConnect, error);
        sceneMain = new Scene(layout, 800, 600);
    }

    private void make1(){

        Label error = new Label("");
        Label nameLabel = new Label("Databasse name:");
        GridPane.setConstraints(nameLabel, 0, 0);

        //Name Input
        TextField nameInput = new TextField("newDB");
        GridPane.setConstraints(nameInput, 1, 0);

        Button make = new Button("ok");
        make.setOnAction(event1 ->  {

                String n = nameInput.getText();
                msg = "wait for DB to create";
                Task <Void> task = new Task<Void>() {
                    @Override public Void call() throws InterruptedException {
                        updateMessage(msg);
                        try {
                            updateMessage(dl.createDB(n));

                        } catch (Exception ex) {
                            updateMessage("can't create");

                            ex.printStackTrace();
                        }
                        return null;
                    }
                };
                error.textProperty().bind(task.messageProperty());

                task.setOnSucceeded(e -> {
                    error.textProperty().unbind();
                });

                error.textProperty().bind(task.messageProperty());

                // java 8 construct, replace with java 7 code if using java 7.
                task.setOnSucceeded(e -> {
                    error.textProperty().unbind();
                });


                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();

        } );
        Button goBack = new Button("Go back to menu");
        goBack.setOnAction(event ->{
            error.setText("");
            window.setScene(sceneMain);
        });
        VBox layout = new VBox(20);
        layout.getChildren().addAll(nameLabel, nameInput, make, goBack, error);
        scene1 = new Scene(layout, 800, 700);

    }


    private void make3(){
        Label error = new Label("");
        Label nameLabel = new Label("Table name:");
        GridPane.setConstraints(nameLabel, 0, 0);

        TextField nameInput = new TextField("pilot");
        GridPane.setConstraints(nameInput, 1, 0);

        Button make = new Button("ok");
        make.setOnAction(event -> {

                String n = nameInput.getText();
                msg = "wait for DB to clear";
                Task <Void> task = new Task<Void>() {
                    @Override public Void call() throws InterruptedException {
                        updateMessage(msg);
                        try {
                            dl.clearTable(n);
                            msg = "Table "+n+" is cleared";

                        } catch (Exception ex) {
                            msg = "can't clear";

                            ex.printStackTrace();
                        }
                        return null;
                    }
                };
                error.textProperty().bind(task.messageProperty());

                task.setOnSucceeded(e -> {
                    error.textProperty().unbind();
                    error.setText(msg);
                });

                error.textProperty().bind(task.messageProperty());

                // java 8 construct, replace with java 7 code if using java 7.
                task.setOnSucceeded(e -> {
                    error.textProperty().unbind();
                    // this message will be seen.
                    error.setText(msg);
                });
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
        } );
        Button goBack = new Button("Go back to menu");
        goBack.setOnAction(event ->{
            error.setText("");
            window.setScene(sceneMain);
        } );
        VBox layout = new VBox(20);
        layout.getChildren().addAll(nameLabel, nameInput, make, goBack, error);
        scene3 = new Scene(layout, 800, 600);

    }

    private void make4(){
        Label error = new Label("");
        Label nameLabel = new Label("Table name:");
        GridPane.setConstraints(nameLabel, 0, 0);

        TextField nameInput = new TextField("pilot");
        GridPane.setConstraints(nameInput, 1, 0);

        Button make = new Button("ok");
        make.setOnAction(event -> {
            try{
                dl.createTables();
            }
            catch (SQLException e){
                MessageBox.display(e.getMessage());
            }
            InsertBox.display(nameInput.getText(), dl, sceneMain);
            window.close();
        });
        Button goBack = new Button("Cancel");
        goBack.setOnAction(event ->{
            error.setText("");
            window.setScene(sceneMain);
        } );
        VBox layout = new VBox(20);
        layout.getChildren().addAll(nameLabel, nameInput, make, goBack, error);
        scene4 = new Scene(layout, 800, 600);

    }

    private void make2(){

        Label error = new Label("");
        Label l2 = new Label("Table :");
        GridPane.setConstraints(l2, 0, 0);
        PrintStream std=System.out;

        TextField i2 = new TextField("pilot");
        GridPane.setConstraints(i2, 1, 0);

        Button make = new Button("ok");
        make.setOnAction(event -> {
            try {

                String s = dl.printTable(i2.getText());
                TextBox.display(s, dl, sceneMain);
            }
            catch (SQLException e){

                System.setOut(std);
                System.out.println(e.getMessage());
                MessageBox.display("error");
                window.setScene(sceneMain);
            }
        });
        Button goBack = new Button("Cancel");
        goBack.setOnAction(event ->{
            error.setText("");
            window.setScene(sceneMain);
        } );


        VBox layout = new VBox(20);
        layout.getChildren().addAll(l2, i2, make, goBack, error);
        scene2 = new Scene(layout, 800, 600);

    }

    private void make5(){

        Label error = new Label("");
        Label l2 = new Label("NAME:");
        GridPane.setConstraints(l2, 0, 0);

        TextField i2 = new TextField("Smith");
        GridPane.setConstraints(i2, 1, 0);

        Button make = new Button("ok");
        make.setOnAction(event -> {
            try {

                String s = dl.search(i2.getText());
                TextBox.display(s, dl, sceneMain);
            }
            catch (SQLException e){
                MessageBox.display("error");
                window.setScene(sceneMain);
            }
        });
        Button goBack = new Button("Cancel");
        goBack.setOnAction(event ->{
            error.setText("");
            window.setScene(sceneMain);
        } );


        VBox layout = new VBox(20);
        layout.getChildren().addAll(l2, i2, make, goBack, error);
        scene2 = new Scene(layout, 800, 600);


    }

    private void make6(){

        Button make = new Button("ok");

        Button goBack = new Button("Cancel");
        goBack.setOnAction(event ->{
            window.setScene(sceneMain);
        });
        Label l5 = new Label("RECORD:");
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
            try{
                dl.updateRecord(Integer.valueOf(i5.getText()), Integer.valueOf(i6.getText()),
                        Integer.valueOf(i7.getText()));
                MessageBox.display("done");
            }
            catch (SQLException e){
                MessageBox.display("error");
                window.setScene(sceneMain);
            }
        });

        VBox layout = new VBox(20);
        layout.getChildren().addAll(l5, i5, l6, i6, l7, i7 , make, goBack);
        scene6 = new Scene(layout, 800, 600);

    }

    private void make7(){
        Label error = new Label("");
        Label l2 = new Label("NAME:");
        GridPane.setConstraints(l2, 0, 0);

        TextField i2 = new TextField("Smith");
        GridPane.setConstraints(i2, 1, 0);

        Button make = new Button("ok");
        make.setOnAction(event -> {
            try {

                dl.deletePilot(i2.getText());
                MessageBox.display("done");
            }
            catch (SQLException e){
                MessageBox.display("error");
                window.setScene(sceneMain);
            }
        });
        Button goBack = new Button("Cancel");
        goBack.setOnAction(event ->{
            error.setText("");
            window.setScene(sceneMain);
        } );


        VBox layout = new VBox(20);
        layout.getChildren().addAll(l2, i2, make, goBack, error);
        scene7 = new Scene(layout, 800, 600);

    }

    boolean connected;
    static DatabaseLogic dl;
    String msg;
    Stage window;
    Scene sceneConnect, sceneMain, scene1, scene2, scene3, scene4, scene5, scene6, scene7;
}
