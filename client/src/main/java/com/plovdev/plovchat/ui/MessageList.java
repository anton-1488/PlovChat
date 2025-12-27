package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Message;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageList extends ScrollPane {
    private static final Logger log = LoggerFactory.getLogger(MessageList.class);
    private final VBox box = new VBox(20);

    public MessageList() {
        box.setPadding(new Insets(10, 10, 50, 10));
        box.setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("chat-scene");
        box.getStyleClass().add("chat-scene");
        setContent(box);

        setFitToHeight(true);
        setFitToWidth(true);

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setPannable(true);

        box.heightProperty().addListener((p1,p2,p3) -> Platform.runLater(() -> setVvalue(getVmax())));
    }

    public void addAllMessages(List<Message> messages) {
        messages.forEach(this::addMessage);
        setVvalue(getVmax());
    }

    public void addMessage(Message message) {
        MessageRender render = switch (message.getType()) {
            case IMAGE -> new ImageMessageRender(message);
            case FILE -> new FileMessageRender(message);
            default -> new TextMessageRender(message);
        };
        box.getChildren().add(render.render());
    }
}