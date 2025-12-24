package com.plovdev.plovchat.ui;

import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

public class ChatTab extends Tab {
    public ChatTab(String text, Pane content) {
        super(text);
        setClosable(false);
        setContent(content);
        getStyleClass().add("chat-tab");
    }
}