package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Chat;
import com.plovdev.plovchat.models.File;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.RestManager;
import com.plovdev.plovchat.utils.Utils;
import com.plovdev.plovchat.utils.WSManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.nio.file.Path;

public class SendField extends HBox {
    private final TextField textField = new TextField();
    private final Button addFile = new Button("+");
    private final WSManager manager = WSManager.getInstance();


    public SendField(Chat curentChat, MessageList messageList) {
        super(0);
        textField.setPromptText("Введите текст...");
        textField.setPrefHeight(35);
        textField.getStyleClass().add("prompt-field");

        WSManager manager = WSManager.getInstance();
        textField.prefWidthProperty().bind(messageList.widthProperty());
        setAlignment(Pos.BOTTOM_RIGHT);
        getChildren().addAll(textField, addFile);

        addFile.setOnAction(a -> {
            Filer filer = new Filer();
            String data = filer.getData();
            if (data == null) return;
            File file = RestManager.getInstance().uploadFile(Path.of(data));

            data = data.substring(data.lastIndexOf("/") + 1);

            System.out.println(data);
            String ext = data.substring(data.lastIndexOf(".") + 1).toLowerCase();
            Message.MessageType type = switch (ext) {
                case "png", "jpg", "bmp" -> Message.MessageType.IMAGE;
                default -> Message.MessageType.FILE;
            };
            Message message = getFile(curentChat, type, file);
            messageList.addMessage(message);
            manager.sendChatFile(curentChat.getId(), message.getContent(), file, type);
        });

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
        HBox.setMargin(addFile, new Insets(5));
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

    private Message getFile(Chat currentChat, Message.MessageType type, File file) {
        Message message = new Message();
        message.setChatId(currentChat.getId());
        message.setContent(textField.getText());
        message.setType(type);
        message.setTimesamp(System.currentTimeMillis());
        User from = new User();
        from.setId(Utils.getFromPrefs("user-id", ""));
        from.setName(Utils.getFromPrefs("user-name", ""));
        from.setAvatar(Utils.getFromPrefs("user-avatar", ""));
        from.setBio(Utils.getFromPrefs("user-bio", ""));

        message.setFrom(from);
        message.setInfo(file);
        return message;
    }
}
