package com.plovdev.plovchat.ui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class ChatsList extends ListView<HBox> {
    public ChatsList() {
        setCellFactory(chatCardViewListView -> new ListCell<>() {
            @Override
            protected void updateItem(HBox chatCardView, boolean empty) {
                super.updateItem(chatCardView, empty);
                setText(null);
                if (empty || chatCardView == null) {
                    setGraphic(null);
                } else {
                    setGraphic(chatCardView);
                }
            }
        });
        getStyleClass().add("chats-list");

    }

    public void addChat(ChatCardView chat) {
        getItems().addFirst(chat);
        scrollTo(0);
    }
}