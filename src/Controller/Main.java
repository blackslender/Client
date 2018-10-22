package Controller;

import Connection.ServerConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;


public class Main {
    @FXML
    public Label usernameLabel;
    @FXML
    public TextField statusField;
    @FXML
    public VBox friendZone;

    public void setUser(String username) {
        usernameLabel.setText(username);
    }

    public void setStatus(ActionEvent event) {
        Platform.runLater(() -> {
            Map map = new HashMap<String, String>();
            map.put("type", "updateStatusRequest");
            map.put("username", usernameLabel.getText());
            map.put("status", statusField.getText());

            Connection.ServerConnection.sendMessage(map);
        });
    }



    public void setStatus(String s) {
        statusField.setText(s);
    }
}
