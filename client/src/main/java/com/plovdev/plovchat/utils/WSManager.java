package com.plovdev.plovchat.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plovdev.plovchat.PlovChatApp;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.models.utils.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.prefs.Preferences;

public class WSManager extends WebSocketClient {
    private static final Preferences prefs = Preferences.userNodeForPackage(PlovChatApp.class);

    private final Logger log = LoggerFactory.getLogger(WSManager.class);
    private boolean isReconnecting = false;
    private boolean isConnected = false;
    private boolean isAuthenticated = false;
    private boolean isAuthenticating = false;
    private boolean onReadied = false;
    private final Gson gson = new Gson();
    private MessageListener listener;

    private Runnable onReady = () -> {};

    private WSManager() {
        super(URI.create("ws://217.26.27.252:8081/chat"));
        setConnectionLostTimeout(50);

        connect();
    }

    private static WSManager instance = null;
    public static WSManager getInstance() {
        if (instance == null) {
            instance = new WSManager();
        }
        return instance;
    }

    public MessageListener getListener() {
        return listener;
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public boolean isReconnecting() {
        return isReconnecting;
    }


    public boolean isConnected() {
        return isConnected;
    }


    public boolean isAuthenticated() {
        return isAuthenticated;
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        isConnected = true;
        auntificate();
    }

    @Override
    public void onMessage(String s) {
        try {
            JSONObject message = new JSONObject(s);
            String op = message.optString("op");
            switch (op) {
                case "login":
                    checkLogin(message);
                    break;
                case "message":
                    JSONObject msg = message.getJSONObject("args");
                    handleMessage(msg.toString());
                    break;
                case "status":
                    handleStatus(message);
                    break;
            }
        } catch (Exception e) {
            log.error("WebSocket error: {}", e.getMessage());
        }
    }


    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("Connection closed: {}", i);
        isConnected = false;

        if (i != 1000 && !isReconnecting) {
            log.info("Try reconnect");
            reconnect();
        }
    }

    /**
     * Автопереподключение
     */
    @Override
    public void reconnect() {
        new Thread(() -> {
            isReconnecting = true;
            try {
                log.info("reconnect...");
                auntificate();
            } catch (Exception e) {
                log.error("Reconnection failed: ", e);
            } finally {
                isReconnecting = false;
            }
        }).start();
    }

    public boolean isAuthenticating() {
        return isAuthenticating;
    }


    @Override
    public void onError(Exception ex) {
        log.error("WebSocket error: ", ex);
    }

    private void checkLogin(JSONObject msg) {
        JSONObject args = msg.getJSONObject("args");
        boolean success = args.getBoolean("success");
        if (success) {
            isAuthenticated = true;
            onReady.run();
            onReadied = true;
            if (listener != null) {
                listener.onAuntithicated();
            }
        } else {
            log.warn("Auth error: {}", msg.getString("msg"));
            auntificate();
        }
    }

    private void handleMessage(String msg) {
        Message received = JsonParser.jsonToMessage(msg);
        listener.onMessageReceived(received);
    }

    private void auntificate() {
        try {
            String id = prefs.get("user-id", null);
            String password = prefs.get("user-password", null);

            if (id == null || password == null) {
                return;
            }

            isAuthenticating = true;
            JsonObject authArgs = new JsonObject();
            authArgs.addProperty("id", id);
            authArgs.addProperty("password", password);

            JsonArray argsArray = new JsonArray();
            argsArray.add(authArgs);

            JsonObject authMessage = new JsonObject();
            authMessage.addProperty("op", "login");
            authMessage.add("args", argsArray);

            send(gson.toJson(authMessage));
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        isAuthenticating = false;
    }


    public void sendChatMessage(String chatId, String text) {
        JsonObject args = new JsonObject();
        args.addProperty("id", -1);
        args.addProperty("text", text);
        args.addProperty("time", System.currentTimeMillis());
        args.addProperty("chatId", chatId);

        // Добавь информацию об отправителе если нужно
        JsonObject from = new JsonObject();

        from.addProperty("id", prefs.get("user-id", ""));
        from.addProperty("name", prefs.get("user-name", ""));
        from.addProperty("bio", prefs.get("user-bio", ""));
        from.addProperty("picture-url", prefs.get("picture-url", ""));

        args.add("from", from);

        JsonObject message = new JsonObject();
        message.addProperty("op", "message");
        message.add("args", args);

        send(gson.toJson(message));
    }

    public Runnable getOnReady() {
        return onReady;
    }

    public void setOnReady(Runnable onReady) {
        this.onReady = onReady;

        if (isConnected && isAuthenticated && !onReadied) {
            onReady.run();
        }
    }

    public void setOnline(boolean online) {
        JsonObject authArgs = new JsonObject();
        authArgs.addProperty("isOnline", online);

        JsonArray argsArray = new JsonArray();
        argsArray.add(authArgs);

        JsonObject status = new JsonObject();
        status.addProperty("op", "status");
        status.add("args", argsArray);

        send(gson.toJson(status));
    }

    private void handleStatus(JSONObject message) {
        JSONObject args = message.getJSONObject("args");
        JSONObject from = args.getJSONObject("from");
        User fromUser = JsonParser.jsonToUser(from.toString());

        boolean isOnline = args.getBoolean("isOnline");
        if (listener != null) {
            listener.onStatusChanged(isOnline, fromUser);
        }
    }
}