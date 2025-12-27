package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.Utils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;

import static com.plovdev.plovchat.utils.Utils.hReg;

public class TextMessageRender extends MessageRender {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("HH:ss");
    private final BorderPane render = new BorderPane();

    public TextMessageRender(Message message) {
        super(message);
    }

    private Label fromName;
    private Text text;
    private Label time;

    @Override
    public Pane render() {
        StackPane mainContent = new StackPane();
        String myId = Utils.getFromPrefs("user-id", "");
        User from = message.getFrom();

        fromName = new Label();

        render.getStyleClass().add("message-view");

        if (from != null) {
            fromName.setText(from.getName());
            if (from.getId().equals(myId)) {
                render.getStyleClass().add("my-message-view");
                StackPane.setAlignment(render, Pos.CENTER_RIGHT);
            } else {
                render.getStyleClass().add("user-message-view");
                StackPane.setAlignment(render, Pos.CENTER_LEFT);
            }
        } else {
            render.getStyleClass().add("system-message-view");
            StackPane.setAlignment(render, Pos.CENTER);
        }

        text = new Text(message.getContent());
        time = new Label(Utils.fromatDate(message.getTimesamp()));

        fromName.getStyleClass().add("system-message-label");
        time.getStyleClass().add("system-message-label");
        text.getStyleClass().add("message-text");

        text.wrappingWidthProperty().bind(mainContent.widthProperty().divide(2.1));

        render.setCenter(text);
        render.setTop(new HBox(fromName, hReg()));
        render.setBottom(new HBox(hReg(), time));

        render.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showContextMenu(render, e.getScreenX(), e.getScreenY());
            }
        });

        render.maxWidthProperty().bind(mainContent.widthProperty().divide(2.1));
        mainContent.getChildren().add(render);

        return mainContent;
    }
}