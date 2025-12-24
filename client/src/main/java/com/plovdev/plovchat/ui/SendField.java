package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.PlovChatApp;
import com.plovdev.plovchat.models.Chat;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.WSManager;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.prefs.Preferences;

public class SendField extends HBox {
    private static final Preferences prefs = Preferences.userNodeForPackage(PlovChatApp.class);

    private final TextField textField = new TextField();
    private final WSManager manager = WSManager.getInstance();


    public SendField(Chat curentChat, MessageList messageList) {
        super(0);
        textField.setPromptText("Введите текст...");

        WSManager manager = WSManager.getInstance();

        HBox.setHgrow(textField, Priority.SOMETIMES);
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
    }

    private Message getMessage(Chat chat, String text) {
        Message message = new Message();
        message.setChatId(chat.getId());
        message.setContent(text);
        message.setType(Message.MessageType.TEXT);
        message.setTimesamp(System.currentTimeMillis());
        User from = new User();
        from.setId(prefs.get("user-id", ""));
        from.setName(prefs.get("user-name", ""));
        from.setAvatar(prefs.get("user-avatar", ""));
        from.setBio(prefs.get("user-bio", ""));

        message.setFrom(from);

        return message;
    }
}
