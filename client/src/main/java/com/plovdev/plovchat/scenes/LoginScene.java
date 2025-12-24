package com.plovdev.plovchat.scenes;

import com.plovdev.plovchat.scenes.panes.LoginTab;
import com.plovdev.plovchat.scenes.panes.RegisterTab;
import com.plovdev.plovchat.ui.ChatTab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginScene extends BaseScene {
    private final TabPane mainPane = new TabPane();

    public LoginScene(Stage stage) {
        super(new Pane(), 1000,600, stage);
        setRoot(mainPane);
        mainPane.getTabs().addAll(new ChatTab("Логин", new LoginTab(stage)), new ChatTab("Регистрация", new RegisterTab(stage)));
    }
}