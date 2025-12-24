package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Chat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatCardView extends HBox {
    private static final Logger log = LoggerFactory.getLogger(ChatCardView.class);
    private Chat chat;

    private ImageView avatar;
    private Label name;

    public ChatCardView(Chat chat) {
        super(30);
        setPrefHeight(60);
        getStyleClass().add("chat-card");
        this.chat = chat;

        try {
            // Стилизация контейнера
            setAlignment(Pos.CENTER_LEFT);
            setPadding(new Insets(5, 10, 5, 10));

            // Аватар
            avatar = new ImageView();
            avatar.setSmooth(true);
            avatar.setFitWidth(40);
            avatar.setFitHeight(40);
            avatar.setPreserveRatio(true);

            // Делаем аватар круглым
            Circle clip = new Circle(20, 20, 20);
            avatar.setClip(clip);

            // Загружаем аватар если он есть
            if (chat.getAvatar() != null) {
                try {
                    Image image = new Image(chat.getAvatar(), 40, 40, true, true);
                    avatar.setImage(image);
                } catch (Exception e) {
                    log.warn("Не удалось загрузить аватар: {}", e.getMessage());
                }
            }

            name = new Label(chat.getName());
            name.getStyleClass().add("chat-name");

            getChildren().addAll(avatar, name);

        } catch (Exception e) {
            log.error("Ошибка создания ChatCardView: {}", e.getMessage());
            // Минимальный fallback
            name = new Label("Ошибка загрузки");
            getChildren().add(name);
        }
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
        // Обновляем отображение если меняем чат
        name.setText(chat.getName());
    }

    public String getChatId() {
        return chat != null ? chat.getId() : null;
    }
}