package Connection;

import com.google.gson.Gson;

import java.net.*;
import java.io.*;
import java.util.*;

public class P2PConnection {

    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    public static final int port = 7654;
    public static List<P2PConnection> peerList = new ArrayList<P2PConnection>();
    Window.Chat window;
    Gson gson = new Gson();
    String peerName = "Unknown user";

    public P2PConnection(String address) { // Connect to another client
        try {
            socket = new Socket(address, port);
            newChat(socket, "Unknown user");
        } catch (Exception e) {
            System.err.println("Error while connecting to peer: " + e.toString());
        }
    }

    public P2PConnection(Socket skt) {  // Accept a connecting request
        try {
            socket = skt;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            System.err.println("Error while connecting to peer: " + e.toString());
        }
    }

    public static void waitConnection() {
        try {
            ServerSocket skt = new ServerSocket(port);
            // Create a new window
            Socket s = skt.accept(); // Blocked
            newChat(s, "Unknown user"); // Create new chatwindow
        } catch (Exception e) {
            System.err.println("Error while waiting to peer: " + e.toString());
        }
    }

    private void waitMessage() {
        new Thread(() -> {
            try {
                while (true) {
                    String st = "";
                    while (st.isEmpty()) st = reader.readLine();
                    processMessage(st);
                }
            } catch (Exception e) {

            }
        }).start();
    }

    private void processMessage(String s) {
        Gson gson = new Gson();
        Map msg = gson.fromJson(s, (new HashMap<String, Object>()).getClass());
        if (msg.get("type").equals("message")) {

        } else return;
    }

    public void setWindow(Window.Chat wd) {
        window = wd;
    }

    public static void newChat(Socket s, String usr) {
        try {
            Window.Chat chatWindow = new Window.Chat();
            P2PConnection peer = new P2PConnection(s);
            peerList.add(peer);
            chatWindow.setConnection(peer);
            chatWindow.setName(usr);
            chatWindow.start();
            peer.setWindow(chatWindow);
            //Start listening to message
            (new Thread(peer::waitMessage)).start();
            peer.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            peer.writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (Exception e) {
            System.err.println("Error while creating new chat session: " + e.toString());
        }
    }

    public void sendMessage(Map msg) {
        try {
            String ms = gson.toJson(msg);
            writer.write(ms);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            System.err.println("Error while sending message to peer: " + e.toString());
        }

    }

    public void setName(String name) {
        peerName = name;
    }
}
