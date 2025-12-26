package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.Status;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCardView extends HBox {
    private static final Logger log = LoggerFactory.getLogger(UserCardView.class);
    private User user;

    private ImageView avatar;
    private Label name;
    private Label status;

    public UserCardView(User user) {
        super(60);
        setPrefHeight(60);
        getStyleClass().add("chat-card");
        this.user = user;

        try {
            setAlignment(Pos.CENTER_LEFT);
            setPadding(new Insets(5, 10, 5, 10));

            avatar = new ImageView();
            avatar.setSmooth(true);
            avatar.setFitWidth(40);
            avatar.setFitHeight(40);
            avatar.setPreserveRatio(true);

            Circle clip = new Circle(20, 20, 20);
            avatar.setClip(clip);

            if (user.getAvatar() != null) {
                try {
                    Image image = new Image(user.getAvatar(), 40, 40, true, true);
                    avatar.setImage(image);
                } catch (Exception e) {
                    log.warn("Не удалось загрузить аватар: {}", e.getMessage());
                }
            }

            name = new Label(user.getName());
            name.getStyleClass().add("chat-name");

            status = new Label("offline");
            status.getStyleClass().add("user-status");

            Button more = new Button("\\/");
            more.getStyleClass().add("more-button");
            more.setOnMousePressed(e -> showMore(more, e.getScreenX(), e.getScreenY()));

            getChildren().addAll(avatar, new VBox(20, name, status), Utils.hReg(), more);

        } catch (Exception e) {
            log.error("Ошибка создания UserCardView: {}", e.getMessage());
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageView avatar) {
        this.avatar = avatar;
    }

    public Label getName() {
        return name;
    }

    public void setStatus(Status status) {
        this.status.setText(status.name().toLowerCase());
    }

    private void showMore(Node node, double x, double y) {

    }

    public void setName(Label name) {
        this.name = name;
    }
}