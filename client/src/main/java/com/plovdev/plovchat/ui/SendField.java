package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Chat;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.Utils;
import com.plovdev.plovchat.utils.WSManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SendField extends HBox {
    private final TextField textField = new TextField();
    private final WSManager manager = WSManager.getInstance();


    public SendField(Chat curentChat, MessageList messageList) {
        super(0);
        textField.setPromptText("Введите текст...");
        textField.setPrefHeight(35);
        textField.getStyleClass().add("prompt-field");

        WSManager manager = WSManager.getInstance();
        textField.prefWidthProperty().bind(messageList.widthProperty());
        setAlignment(Pos.BOTTOM_RIGHT);
        getChildren().addAll(textField);

        textField.setOnAction(a -> {
            String text = textField.getText();
            if (text.isEmpty()) {
                return;
            }
            textField.setText("");
            messageList.addMessage(getMessage(curentChat, text));
            manager.sendChatMessage(curentChat.getId(), text);
        });

        HBox.setMargin(textField, new Insets(5));
    }

    private Message getMessage(Chat chat, String text) {
        Message message = new Message();
        message.setChatId(chat.getId());
        message.setContent(text);
        message.setType(Message.MessageType.TEXT);
        message.setTimesamp(System.currentTimeMillis());
        User from = new User();
        from.setId(Utils.getFromPrefs("user-id", ""));
        from.setName(Utils.getFromPrefs("user-name", ""));
        from.setAvatar(Utils.getFromPrefs("user-avatar", ""));
        from.setBio(Utils.getFromPrefs("user-bio", ""));

        message.setFrom(from);

        return message;
    }
}
