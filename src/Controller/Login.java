package Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.*;
import com.google.gson.Gson;

public class Login {
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private void sendMessage(Map map) {
        Gson gson = new Gson();
        String msg = gson.toJson(map);

    }
    public void login(ActionEvent event) {
        try {
            Map map = new HashMap<String,String>();
            map.put("type","loginRequest");
            map.put("username",usernameField.getText());
            map.put("password",passwordField.getText());
            Connection.ServerConnection.sendMessage(map);
        } catch (Exception e) {
            System.err.println("Error while processing login request: " + e.toString());
        }
    }

    public void cancel(ActionEvent event) {
        Platform.exit();
    }
}
