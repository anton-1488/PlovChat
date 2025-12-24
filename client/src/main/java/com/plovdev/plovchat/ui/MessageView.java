package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.PlovChatApp;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

public class MessageView extends BorderPane {
    private static final Preferences prefs = Preferences.userNodeForPackage(PlovChatApp.class);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("HH:ss");
    private final Message message;

    private final Label fromName;
    private final Label text;
    private final Label time;

    public MessageView(Message message) {
        this.message = message;
        String myId = prefs.get("user-id", "");
        User from = message.getFrom();

        fromName = new Label();

        if (from != null) {
            fromName.setText(from.getName());
            if (from.getId().equals(myId)) {
                getStyleClass().add("my-message-view");
            } else {
                getStyleClass().add("user-message-view");
            }
        } else {
            getStyleClass().add("system-message-view");
        }

        text = new Label(message.getContent());
        time = new Label(String.valueOf(message.getTimesamp()));

        fromName.getStyleClass().add("system-message-label");
        time.getStyleClass().add("system-message-label");
        text.getStyleClass().add("message-text");

        text.setWrapText(true);
        text.setMaxWidth(300);

        setCenter(text);
        setTop(new HBox(fromName, hReg()));
        setBottom(new HBox(hReg(), time));
    }

    public boolean isMyMessage() {
        String myId = prefs.get("user-id", "");
        User from = message.getFrom(); // null for system message

        return from != null && from.getId().equals(myId);
    }

    public Message getMessage() {
        return message;
    }

    private Region hReg() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }
    private Region vReg() {
        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);
        return region;
    }
}