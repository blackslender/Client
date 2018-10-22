package Window;

import Connection.P2PConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Chat extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    Connection.P2PConnection connection;
    FXMLLoader fxmlLoader;
    Controller.Chat controller;
    private String peerName;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("Chat.fxml"));
            Parent root = fxmlLoader.load();
            controller = fxmlLoader.getController();
            primaryStage.setTitle(peerName);
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error while creating new chat window: " + e.toString());
        }
    }

    public void setName(String username) {
        peerName = username;
    }

    public void setConnection(Connection.P2PConnection conn) {
        connection = conn;
    }

    public void start() {
        try {
            Platform.runLater(() -> {
                try {
                    start(new Stage());
                } catch (Exception e) {
                    System.err.println("Error while creating new chat window: " + e.toString());
                }
            });
        } catch (Exception e) {
            System.err.println("Error while creating new chat window: " + e.toString());
        }
    }
}
