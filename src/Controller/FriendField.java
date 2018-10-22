package Controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class FriendField {

    @FXML
    public Label friendname;
    public Circle bulb;
    public TextField status;
    private boolean online = false;
    @FXML
    public HBox box;

    public void startChatSession(MouseEvent mouseEvent) {
        Window.Main.newChatSession(friendname.getText());

    }

    public void lighton(MouseEvent mouseEvent) {
        if (!online) return;
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED)
            box.setStyle("-fx-background-color: NavajoWhite");
        else box.setStyle("-fx-background-color: Bisque");
    }

    public void setName(String s) {
        friendname.setText(s);
    }

    public void setonline(boolean t) {
        if (t) {
            bulb.setFill(Paint.valueOf("#1fff26"));
            friendname.setTextFill(Paint.valueOf("#000000"));
            online = true;
        } else {
            bulb.setFill(Paint.valueOf("gray"));
            friendname.setTextFill(Paint.valueOf("#707070"));
            status.setStyle("-fx-text-fill: gray;");
            box.setStyle("-fx-background-color: White");
            online = false;
        }
    }

    public void setStatus(String s) {
        status.setText(s);
    }

}
