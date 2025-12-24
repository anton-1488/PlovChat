package com.plovdev.plovchat.scenes;

import com.plovdev.plovchat.scenes.panes.LoginTab;
import com.plovdev.plovchat.scenes.panes.RegisterTab;
import com.plovdev.plovchat.ui.ChatTab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginScene extends BaseScene {
    private final TabPane mainPane = new TabPane();

    public LoginScene(Stage stage) {
        super(new Pane(), 1000,600, stage);
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        setRoot(mainPane);
        mainPane.getTabs().addAll(new ChatTab("Логин", new LoginTab(stage)), new ChatTab("Регистрация", new RegisterTab(stage)));
    }
}