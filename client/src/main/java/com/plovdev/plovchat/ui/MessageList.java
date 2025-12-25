package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Message;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class MessageList extends ScrollPane {
    private final VBox box = new VBox(20);
    public MessageList() {
        box.setPadding(new Insets(10, 10, 50, 10));
        box.setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("chat-scene");
        box.getStyleClass().add("chat-scene");
        setContent(box);

        setFitToHeight(true);
        setFitToWidth(true);
    }

    public void addAllMessages(List<MessageView> views) {
        box.getChildren().addAll(views);
    }
    public void addMessage(Message message) {
        box.getChildren().add(new MessageView(message));
    }
}