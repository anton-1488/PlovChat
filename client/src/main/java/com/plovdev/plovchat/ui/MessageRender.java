package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.Utils;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

public abstract class MessageRender {
    protected Message message;
    public MessageRender(Message message) {
        this.message = message;
    }
    public abstract Pane render();

    public boolean isMyMessage() {
        String myId = Utils.getFromPrefs("user-id", "");
        User from = message.getFrom();

        return from != null && from.getId().equals(myId);
    }

    public void showContextMenu(Node node, double x, double y) {
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(a -> Utils.copy(message.getContent()));

        ContextMenu menu = new ContextMenu(copy);
        menu.show(node, x, y);
    }
}