package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.Utils;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;

public class MessageView extends StackPane {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("HH:ss");
    private final Message message;
    private final BorderPane render = new BorderPane();

    private final Label fromName;
    private final Text text;
    private final Label time;

    public MessageView(Message message) {
        this.message = message;
        String myId = Utils.getFromPrefs("user-id", "");
        User from = message.getFrom();

        fromName = new Label();

        render.getStyleClass().add("message-view");

        if (from != null) {
            fromName.setText(from.getName());
            if (from.getId().equals(myId)) {
                render.getStyleClass().add("my-message-view");
                setAlignment(render, Pos.CENTER_RIGHT);
            } else {
                render.getStyleClass().add("user-message-view");
                setAlignment(render, Pos.CENTER_LEFT);
            }
        } else {
            render.getStyleClass().add("system-message-view");
            setAlignment(render, Pos.CENTER);
        }

        text = new Text(message.getContent());
        time = new Label(Utils.fromatDate(message.getTimesamp()));

        fromName.getStyleClass().add("system-message-label");
        time.getStyleClass().add("system-message-label");
        text.getStyleClass().add("message-text");

        text.wrappingWidthProperty().bind(widthProperty().divide(2.1));

        render.setCenter(text);
        render.setTop(new HBox(fromName, hReg()));
        render.setBottom(new HBox(hReg(), time));

        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showContextMenu(e.getScreenX(), e.getScreenY());
            }
        });

        render.maxWidthProperty().bind(widthProperty().divide(2.1));
        getChildren().add(render);
    }

    public boolean isMyMessage() {
        String myId = Utils.getFromPrefs("user-id", "");
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

    private void showContextMenu(double x, double y) {
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(a -> Utils.copy(message.getContent()));

        ContextMenu menu = new ContextMenu(copy);
        menu.show(this, x, y);
    }
}