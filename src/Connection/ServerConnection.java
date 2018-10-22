package Connection;

import java.io.*;

import java.net.*;
import java.util.*;

import com.google.gson.Gson;

public class ServerConnection {
    private static Socket socket;
    private static Window.Login loginWindow;
    private static Window.Main mainWindow;
    public static final String serverIP = "192.168.2.106";
    public static final int port = 4567;
    public static Thread listenThread;
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static Gson gson = new Gson();

    public static void connect(Window.Login window) {
        try {
            loginWindow = window;
            socket = new Socket(serverIP, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            start(ServerConnection::listenLoop);
        } catch (Exception e) {
            System.err.println("Error while connecting to server: " + e.toString());
        }
    }

    public static void sendMessage(Map map) {
        try {
            String msg = gson.toJson(map);
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            System.err.println("Error while sending message to server: " + e.toString());
        }
    }

    private static void start(Runnable func) {
        listenThread = new Thread(func);
        listenThread.start();
    }

    private static void listenLoop() {
        System.out.println("Start listening to server!");

        try {
            while (true) {
                String s = "";
                while (s.isEmpty()) s = reader.readLine();
                processMessage(s);
            }
        } catch (Exception e) {
            System.err.println("Error while listening to server: " + e.toString());
        }

    }

    private static void processMessage(String s) {
        if (s.equals("")) return;
        System.out.println(s);
        (new Thread(() -> {
            Map msg = (HashMap<String, Object>) gson.fromJson(s, (new HashMap<String, Object>()).getClass());
            String msgType = msg.get("type").toString();
            if (msgType.equals("loginResponse"))
                checkLoginStatus(msg.get("username").toString(), msg.get("result").toString());
            else if (msgType.equals("getStatusResponse"))
                setStatus(msg.get("status").toString());
            else if (msgType.equals("getFriendlistResponse"))
                setFriendlist((Map) msg.get("friendlist"));
            else if (msgType.equals("notifyOnline"))
                setOnline(msg.get("name").toString());
            else return;
        })).start();
    }

    private static void checkLoginStatus(String username, String result) {
        if (result.equals("notExit"))
            loginWindow.error("Username is not exist");
        else if (result.equals("wrongPassword"))
            loginWindow.error("Password is incorrect");
        else if (result.equals("online"))
            loginWindow.error("This user has been logged in already");
        else if (result.equals("offline")) {
            sendOnlineRequest(username);
            mainWindow = Window.Main.mainWindow(username);
            loginWindow.close();
        }
    }

    private static void sendOnlineRequest(String username) {
        Map map = new HashMap<String, String>();
        map.put("type", "sendOnlineRequest");
        map.put("username", username);
        sendMessage(map);
    }

    private static void sendOfflineRequest(String username) {
        Map map = new HashMap<String, String>();
        map.put("type", "sendOfflineRequest");
        map.put("username", username);
        sendMessage(map);
    }

    public static void setStatus(String s) {
        mainWindow.setStatus(s);
    }

    public static void setFriendlist(Map list) {
        mainWindow.setFriendlist(list);
    }

    public static void setOnline(String usrname) {
        mainWindow.setOnline(usrname);
    }

    public static void setOffline(String usrname) {
        mainWindow.setOffline(usrname);
    }
}
