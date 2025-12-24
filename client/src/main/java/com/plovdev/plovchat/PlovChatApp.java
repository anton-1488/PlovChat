package com.plovdev.plovchat;

import com.plovdev.plovchat.scenes.BaseScene;
import com.plovdev.plovchat.scenes.ChatScene;
import com.plovdev.plovchat.scenes.LoginScene;
import com.plovdev.plovchat.utils.WSManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.prefs.Preferences;

public class PlovChatApp extends Application {
    private static final Preferences prefs = Preferences.userNodeForPackage(PlovChatApp.class);

    @Override
    public void start(Stage stage) {
        BaseScene baseScene;

        prefs.putBoolean("is-user-registred", true);
        if (!prefs.getBoolean("is-user-registred", false)) {
            baseScene = new LoginScene(stage);
        } else {
            baseScene = new ChatScene(stage);
        }

        baseScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        stage.setScene(baseScene);
        stage.setTitle("PlovChat");
        stage.show();
    }

    @Override
    public void stop() {
        WSManager manager = WSManager.getInstance();
        manager.setOnline(false);
        manager.close();
    }
}