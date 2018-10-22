package Window;

import Connection.P2PConnection;
import Connection.ServerConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.Socket;
import java.util.*;

public class Main extends Application {

    static String username;
    static FXMLLoader fxmlLoader;
    static Controller.Main controller;
    static Map friendList;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = fxmlLoader.load();
            controller = (Controller.Main) fxmlLoader.getController();
            System.out.println(controller);
            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error while creating new login window: " + e.toString());
        }

        //Get info from server;
        Platform.runLater(() -> controller.usernameLabel.setText(username));
        getInfo();

    }

    public static Main mainWindow(String usrname) {
        Main window = new Main();
        window.username = usrname;
        Platform.runLater(() -> {
            try {
                window.start(new Stage());
            } catch (Exception e) {
                System.err.println("Error while creating new main window: " + e.toString());
            }
        });
        return window;
    }

    public static void getStatus() {
        try {
            HashMap<String, String> msg = new HashMap<String, String>();
            msg.put("type", "getStatusRequest");
            msg.put("username", username);
            Connection.ServerConnection.sendMessage(msg);
        } catch (Exception e) {
            System.err.println("Error while getting status: " + e.toString());
        }
    }

    public static void getFriendlist() {
        try {
            HashMap<String, String> msg = new HashMap<String, String>();
            msg.put("type", "getFriendlist");
            msg.put("username", username);
            Connection.ServerConnection.sendMessage(msg);
            msg.clear();
        } catch (Exception e) {

        }
    }

    public static void getInfo() {
        getStatus();
        getFriendlist();
        (new Thread(() -> {
            Connection.P2PConnection.waitConnection();
        })).start();
    }

    public static void setStatus(String s) {
        TextField sttField = controller.statusField;
        Platform.runLater(() -> sttField.setText(s));
    }

    public void setFriendlist(Map flist) {
        try {
            friendList = flist;
            // Clear current friendlist
            Platform.runLater(() -> {
                controller.friendZone.getChildren().clear();
            });

            for (Object o : flist.keySet())
                Platform.runLater(() -> {
                    try {
                        String fname = (String) o;
                        String fonline = ((Map) flist.get(fname)).get("online").toString();
                        String fstatus = ((Map) flist.get(fname)).get("status").toString();
                        FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("FriendField.fxml"));
                        Parent box = fxmlLoader2.load();
                        Controller.FriendField ctrl = (Controller.FriendField) fxmlLoader2.getController();
                        ctrl.setName(fname);
                        ctrl.setonline(fonline.equals("online"));
                        ctrl.setStatus(fstatus);
                        controller.friendZone.getChildren().add(box);
                    } catch (Exception e) {
                        System.err.println("Error while loading a new friend: " + e.toString());
                    }
                });
        } catch (Exception e) {
            System.err.println("Error while clearing friendlist: " + e.toString());
        }
    }


    public void addNullUser() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FriendField.fxml"));
            Parent box = fxmlLoader.load();

            controller.friendZone.getChildren().add(box);
        } catch (Exception e) {
            System.err.println("Error while loading a new friend: " + e.toString());
        }
    }

    public void setOnline(String usr) {
        Map temp = (Map) friendList.get(usr);
        temp.put("online", "online");
        setFriendlist(friendList);
    }

    public void setOffline(String usr) {
        Map temp = (Map) friendList.get(usr);
        temp.put("online", "offine");
        setFriendlist(friendList);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void newChatSession(String username) {
        try {
            // Refresh friendlist
            getFriendlist();
            Thread.sleep(100);
            // Search for the connection which have the same username
            for (Object fname : friendList.keySet())
                if (((Map)friendList.get(fname)).get("online").equals("online")) {
                    System.out.println(fname);
                    String ip = ((Map) friendList.get(fname)).get("IP").toString();

                    Socket s = new Socket(ip, P2PConnection.port);
                    P2PConnection.newChat(s, username);
                    break;
                }
        } catch (Exception e) {
            System.err.println("Error while creating new chat session: " + e.toString());
        }
        System.out.println("This user is offline...");
    }
}
